#include "memory.h"
#include "stdint.h"
#include "print.h"

#define PG_SIZE 4096


/** ************************ 位图地址 ************************
 * 因为0xc009f000是内核主线程的栈帧，0xc009e000 是内核主线程的pcb
 * 一个页框大小的位图可以表示128MB，位图位置安排在地址0xc009a000
 * 这样本系统最大支持4个页框的位图，即512MB
 */
#define MEM_BITMAP_BASE 0xc009a000

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







