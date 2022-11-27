#include "memory.h"
#include "stdint.h"
#include "print.h"
#include "debug.h"
#include "string.h"

#define PG_SIZE 4096


/** ************************ 位图地址 ************************
 * 因为0xc009f000是内核主线程的栈帧，0xc009e000 是内核主线程的pcb
 * 一个页框大小的位图可以表示128MB，位图位置安排在地址0xc009a000
 * 这样本系统最大支持4个页框的位图，即512MB
 */
#define MEM_BITMAP_BASE 0xc009a000

#define PDE_IDX(addr) ((addr & 0xffc00000) >> 22)
#define PTE_IDX(addr) ((addr & 0x003ff000) >> 12)

/**
 * 0xc00_00000 是从虚拟地址3G开始起，为内核的虚拟地址空间
 * 0x1_00000 1MB的内存空间
 */
#define K_HEAP_START 0xc0100000

/**
 * 内存池结构，生成两个实例用于管理内核内存池和用户池
 */
struct pool {
    struct bitmap pool_bitmap; //本内存池用到的位图结构，用于管理物理内存
    uint32_t phy_addr_start; //内存吃所管理物理内存的起始地址
    uint32_t pool_size; //内存池的字节容量
};

struct pool kernel_pool, user_pool; // 内核内存池和用户内存池
struct virtual_addr kernel_vaddr; //用来给内核分配虚拟地址

/**
 * 初始化内存池
 */
static void mem_pool_init(uint32_t all_mem) {
    put_str("   mem_pool_init start\n");
    // 页表大小 = 1页目录 + 第0页和第768个页目录指向同一个页表 + 第769～1022个页目录指向的254个页表
    // 共256个页框
    uint32_t page_table_size = PG_SIZE * 256;

    // 1MB低地址已使用 + 页框使用
    uint32_t used_mem = page_table_size + 0x100000;

    uint32_t free_mem = all_mem - used_mem;
    // 1页 = 4KB，不管内存是不是4KB的倍数，对于以页为单位的内存分配策略
    // 不足1页的内存不用考虑
    uint16_t all_free_pages = free_mem / PG_SIZE;

    // 内核 用户地址空间各一半
    uint16_t kernel_free_pages = all_free_pages / 2;
    uint16_t user_free_pages = all_free_pages - kernel_free_pages;

    // 最多丢失掉（1～7页）*2的内存空间，位了方便简化位图操作，丢失一些内存
    uint32_t kbm_length = kernel_free_pages / 8;
    uint32_t ubm_length = user_free_pages / 8;

    // 内核的内存池就紧挨着页框，二用户内存池紧挨着内核的内存池
    uint32_t kp_start = used_mem;
    uint32_t up_start = kp_start + kernel_free_pages * PG_SIZE;

    kernel_pool.phy_addr_start = kp_start;
    user_pool.phy_addr_start = up_start;

    kernel_pool.pool_size = kbm_length;
    user_pool.pool_size = ubm_length;

    kernel_pool.pool_bitmap.btmp_bytes_len = kbm_length;
    user_pool.pool_bitmap.btmp_bytes_len = ubm_length;

    // 内核使用的最高地址是0xc009f000，这是主线程的栈帧
    // 内核的大小预计在70KB左右，32MB内存占用的位图是2KB
    // 内核内存池的位图先定于0xc009a000处
    // 用户内存池的位图跟着内核内存池之后
    kernel_pool.pool_bitmap.bits = (void*)MEM_BITMAP_BASE;
    user_pool.pool_bitmap.bits = (void*)(MEM_BITMAP_BASE + kbm_length);

    put_str("      kernel_pool_bitmap_start:");put_int((int)kernel_pool.pool_bitmap.bits);
    put_str(" kernel_pool_phy_addr_start:");put_int(kernel_pool.phy_addr_start);
    put_str("\n");
    put_str("      user_pool_bitmap_start:");put_int((int)user_pool.pool_bitmap.bits);
    put_str(" user_pool_phy_addr_start:");put_int(user_pool.phy_addr_start);
    put_str("\n");

    // 将位图置0
    bitmap_init(&kernel_pool.pool_bitmap);
    bitmap_init(&user_pool.pool_bitmap);

    // 初始化内核虚拟地址的位图，按实际物理内存大小生成数组
    // 用于维护内核堆的虚拟地址，所以要和内存池大小一致
    kernel_vaddr.vaddr_bitmap.btmp_bytes_len = kbm_length;

    // 位图的数组指向一块未使用的内存，暂时定位于内核内存池和用户内存池之外的一片内存
    kernel_vaddr.vaddr_bitmap.bits = (void*)(MEM_BITMAP_BASE + kbm_length + ubm_length);

    kernel_vaddr.vaddr_start = K_HEAP_START;
    bitmap_init(&kernel_vaddr.vaddr_bitmap);
    put_str("   mem_pool_init done\n");
}


void malloc_init(void) {

}


/**
 * 得到虚拟地址vaddr对应的pte指针
 */
uint32_t* pte_ptr(uint32_t vaddr) {
    // 先访问到页表自己
    // 再用页目录项pde（页目录内页表的索引）作为pte的索引访问到页表
    // 再用ptc的索引作为页内偏移
    uint32_t* pte = (uint32_t*) (0xffc00000 + ((vaddr & 0xffc00000) >> 10) + PTE_IDX(vaddr) * 4);
    // 0xffc00000 虚拟地址指向的就是页目录 前十位的表示1023项，也就是页目录的最后一项，它里面的内容是指向页目录表自己的首地址
    // 把自己作为二级表 然后用vaddr的前十位来索引指向pte页表的地址的首地址
    // 要知道哪个页，就需要vaddr的中间10为来确定具体的哪个页表
    // 整合相加就得到了 页表的虚拟地址
    return pte;
}

/**
 * 得到虚拟地址vaddr对应的pde指针
 */
uint32_t* pde_ptr(uint32_t vaddr) {
    // 类似于构建pte的过程
    uint32_t* pde = (uint32_t*)(0xfffff000 + PDE_IDX(vaddr) * 4);
    return pde;
}

/**
 * 在m_pool指向的物理内存池中分配一个物理页
 * 成功则返回页框的物理地址，失败则返回NULL
 */
static void* palloc(struct pool* m_pool){
    // 扫描且设置位图，也就是找到一个物理页，要保证原子性
    int bit_idx = bitmap_scan(&m_pool->pool_bitmap, 1);
    if(bit_idx == -1) {
        return NULL;
    }
    bitmap_set(&m_pool->pool_bitmap, bit_idx, 1);
    uint32_t page_phyaddr = (m_pool->phy_addr_start + bit_idx * PG_SIZE);
    return (void*) page_phyaddr;
}

/**
 * 在pf表示的虚拟内存池中申请pg_cnt个虚拟页，成功则返回虚拟页的起始地址, 失败则返回NULL
 */
static void* vaddr_get(enum pool_flags pf, uint32_t pg_cnt) {
    int vaddr_start = 0, bit_idx_start = -1;
    uint32_t cnt = 0;
    if(pf == PF_KERNEL) {
        // 内核
        bit_idx_start = bitmap_scan(&kernel_vaddr.vaddr_bitmap, pg_cnt);
        if(bit_idx_start == -1) {
            return NULL;
        }
        while (cnt < pg_cnt) {
            bitmap_set(&kernel_vaddr.vaddr_bitmap, bit_idx_start + cnt++, 1);
        }
        vaddr_start = kernel_vaddr.vaddr_start + bit_idx_start * PG_SIZE;
    } else {
        // 用户
    }
    return (void*) vaddr_start;
}

/**
 * 页表中添加虚拟地址_vaddr与物理地址_page_phyaddr的映射，起始就是将页表中的物理地址填充进去
 */
 static void page_table_add(void* _vaddr, void* _page_phyaddr) {
    uint32_t vaddr = (uint32_t) _vaddr, page_phyaddr = (uint32_t) _page_phyaddr;
    uint32_t* pde = pde_ptr(vaddr);
    uint32_t* pte = pte_ptr(vaddr);

    // **************************** 注意
    // 执行*pte，会访问到空的pde，所以确保pde创建后才能执行*pte
    // 否则会引发page_fault。因为在*pde为0时，*pte只能出现在下面的else语句块中的*pde后面

    // *pde是页目录的虚拟地址，判断页目录是否存在 P位为1表示存在该页目录
    if(*pde & 0x00000001) {
        // 断言pte即页表是否存在
        ASSERT(!(*pte & 0x00000001));

//        if(*pte & 0x00000001) {
//            // 存在打印信息 提示下
//            PANIC("pte repeat");
//        }
//        *pte = (page_phyaddr | PG_US_U | PG_RW_W | PG_P_1);

        if(!(*pte & 0x00000001)) {
            // 不存在页表，需创建，只要是创建页表，肯定是不存在的
            *pte = (page_phyaddr | PG_US_U | PG_RW_W | PG_P_1);
        } else {
            PANIC("pte repeat");
            *pte = (page_phyaddr | PG_US_U | PG_RW_W | PG_P_1);
        }
    } else {
        // 页目录项不存在，所以要先创建页目录项
        // 页表中用到的页框一律从内核空间中分配
        uint32_t pde_phyaddr = (uint32_t) palloc(&kernel_pool);
        *pde = (pde_phyaddr | PG_US_U | PG_RW_W | PG_P_1);

        // 分配到的物理页地址pde_phyaddr对应的物理内存清0
        // 避免里面的脏数据编程页表项，从而让页表混乱
        // 访问到pde对应的物理地址，用pte去高20位便可
        // 因为pte基于该pde对应的物理地址内在寻址
        // 把低12位置0便是该pde对应的物理页的起始地址
        memset((void*)((int)pte & 0xfffff000), 0, PG_SIZE);

        ASSERT(!(*pte & 0x00000001));
        *pte = (page_phyaddr | PG_US_U | PG_RW_W | PG_P_1);
    }
 }

/**
 * 分配pg_cnt个页空间，成功则返回起始虚拟地址，失效时返回NULL
 */
void* malloc_page(enum pool_flags pf, uint32_t pg_cnt) {
    // 分配页小于内存池的大小 以32M为例，16MB内核空间，保险起见 15MB计算，pg_cnt < 15 * 1024 * 1024 / 4096
    ASSERT(pg_cnt > 0 && pg_cnt < 3840);
    void* vaddr_start = vaddr_get(pf, pg_cnt);
    if(NULL == vaddr_start) {
        return NULL;
    }

    uint32_t vaddr = (uint32_t) vaddr_start, cnt = pg_cnt;
    struct pool* mem_pool = pf & PF_KERNEL ? &kernel_pool : &user_pool;
    // 因为虚拟地址是连续的，物理地址是不连续的，所以依次遍历逐个做映射
    while (cnt--) {
        // 分配一个物理页
        void* page_phyaddr = palloc(mem_pool);
        if(NULL == page_phyaddr) {
            return NULL;
        }
        // 将虚拟地址和物理地址做映射
        page_table_add((void*)vaddr, page_phyaddr);
        vaddr += PG_SIZE;
    }
    return vaddr_start;
}

/**
 * 从内核物理内存池申请1页内存，成功则返回其虚拟地址，失败则返回NULL
 */
void* get_kernel_pages(uint32_t pg_cnt) {
    void* vaddr = malloc_page(PF_KERNEL, pg_cnt);
    if(NULL != vaddr) {
        // 如果分配的地址不为空，将页框清0后返回
        memset(vaddr, 0, pg_cnt * PG_SIZE);
    }
    return vaddr;
}


/**
 * 内存管理部分初始化入口
 */
void mem_init() {
    put_str("mem_init start\n");
    // 之前通过在loader.S程序中获取到了内存的容量放置这个位置，只需要转换成32为指针取值即可
    uint32_t mem_bytes_total = (*(uint32_t*)(0xb00));
    mem_pool_init(mem_bytes_total);	  // 初始化内存池
    put_str("mem_init done\n");
}







