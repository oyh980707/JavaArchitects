//
// Created by oyh.jerry@Macbook Pro on 2022/11/28.
//

#include "thread.h"
#include "stdint.h"
#include "string.h"
#include "global.h"
#include "memory.h"
#include "interrupt.h"
#include "debug.h"
#include "print.h"

#define PG_SIZE 4096

/**
 * 主线程PCB
 */
struct task_struct* main_thread;
/**
 * 就绪队列
 */
struct list thread_ready_list;
/**
 * 所有的任务队列
 */
struct list thread_all_list;
/**
 * 保存队列中的线程节点
 */
struct list_elem* thread_tag;

/**
 *
 * @param cur
 * @param next
 */
extern void switch_to(struct task_struct* cur, struct task_struct* next);

/**
 * 获取当前正在运行的任务
 * @return 正在运行线程的pcb的起始地址
 */
struct task_struct* running_thread(void){
    uint32_t esp;
    asm volatile("movl %%esp, %0":"=g"(esp));
    // 取esp整数部分，即pcb的起始地址
    return (struct task_struct*)(esp & 0xfffff000);
}

/**
 * 由kernel_thread去执行function(arg)函数
 */
static void kernel_thread(thread_func* function, void* func_arg) {
    // 开中断，避免后面的时钟中断被屏蔽，而且无法调度其他线程
    // 很关键，也就是说调度器是由时钟中断触发的，当时钟中断触发之后，处理器会自动关闭中断，也就是屏蔽中断
    // 然后始终中断处理函数会调用调度器，来调度线程执行，要是当前线程没有执行完，是在关中断下是接收不到时钟中断的
    // 也就不能执行调度器进行调度了
    intr_enable();

    function(func_arg);
}

/**
 * 初始化线程栈 thread_stack
 * 将待执行的函数和参数放到thread_stack中相应的位置
 */
void thread_create(struct task_struct* pthread, thread_func function, void* func_arg) {
    // 先预处理中断使用的栈空间
    pthread->self_kstack -= sizeof(struct intr_stack);
    // 留出线程栈空间，用于线程第一次调用做准备
    pthread->self_kstack -= sizeof(struct thread_stack);

    struct thread_stack * kthread_stack = (struct thread_stack*)pthread->self_kstack;
    // 线程首次执行的函数入口
    kthread_stack->eip = kernel_thread;
    // 如上函数执行需要的参数
    kthread_stack->function = function;
    kthread_stack->func_arg = func_arg;

    kthread_stack->ebp = kthread_stack->ebx =kthread_stack->edi =kthread_stack->esi = 0;
}

/**
 * 初始化线程的基本信息
 */
void init_thread(struct task_struct* pthread, char* name, int prio) {
    // 清0，避免脏页
    memset(pthread, 0, sizeof (*pthread));

    // 线程名字 支持16个字符
    strcpy(pthread->name, name);

    // 线程的状态
    if(pthread == main_thread) {
        // 由于main函数也是一个线程，并且他一直运行的，故直接将其设置成运行
        pthread->status = TASK_RUNNING;
    } else {
        pthread->status = TASK_READY;
    }

    // 优先级
    pthread->priority = prio;

    // 每次在处理器上执行的时间嘀嗒数，根据优先级来定，优先级越高获得的越多
    pthread->ticks = prio;

    // 初始执行的时间片为0
    pthread->elapsed_ticks = 0;

    // 初始化栈顶指针，也就是线程自己的栈，栈顶指针指向在该页的最高地址处
    pthread->self_kstack = (uint32_t*)((uint32_t)pthread + PG_SIZE);

    // 魔数 TODO
    pthread->stack_magic = 0xcafebabe;

    // 表示线程尚未执行过，线程没有自己的地址空间，所以没有还没有页表
    pthread->pgdir = NULL;
}

/**
 * 创建一优先级为prio的线程，线程名为name
 */
struct task_struct* thread_start(char* name, int prio, thread_func function, void* func_arg) {
    // pcb都位于内核空间，包括用户进程的pcb也应是在内核空间
    struct task_struct* thread = get_kernel_pages(1);

    init_thread(thread, name, prio);
    thread_create(thread, function, func_arg);

    // 确保不在就绪队列中
    ASSERT(!elem_find(&thread_ready_list, &thread->general_tag));

    // 加入就绪队列
    list_append(&thread_ready_list, &thread->general_tag);

    // 确保不在总队列中
    ASSERT(!elem_find(&thread_all_list, &thread->all_list_tag));

    // 加入就绪队列
    list_append(&thread_all_list, &thread->all_list_tag);

    return thread;
}

/**
 * 将当前线程阻塞
 * @param status 需要设置为的状态
 */
void thread_block(enum task_status status) {
    // status 取值为TASK_BLOCKED、TASK_WAITING、TASK_HANGING，也就是这三种状态不会被调度
    ASSERT(status == TASK_BLOCKED || status == TASK_WAITING || status == TASK_HANGING);

    // 关中断 TODO 不理解，这里关了，之后没发现有地方打开，怎么进行时钟调度? 一个线程有自己的eflags？
    enum intr_status old_status = intr_disable();
    // 将当前线程设置成status状态然后调度
    struct task_struct* cur_thread = running_thread();
    cur_thread->status = status;
    // 将当前线程换下处理器
    schedule();
    // 当前线程解除后继续执行，恢复之前中断状态
    intr_set_status(old_status);
}

/**
 * 将阻塞的线程解除阻塞
 * @param task 需要解除阻塞的线程
 */
void thread_unblok(struct task_struct* task) {
    // 关中断
    enum intr_status old_status = intr_disable();
    ASSERT(task->status == TASK_BLOCKED || task->status == TASK_WAITING || task->status == TASK_HANGING);
    if(task->status != TASK_READY) {
        ASSERT(!elem_find(&thread_ready_list, &task->general_tag));
        if(elem_find(&thread_ready_list, &task->general_tag)) {
            PANIC("thread_unblock: blocked thread in read_list.\n");
        }
        // 放到队列前面使其尽快得到调度
        list_push(&thread_ready_list, &task->general_tag);
        task->status = TASK_READY;
    }
    intr_set_status(old_status);
}

/**
 * 实现任务调度
 */
void schedule(void) {
    // 输出当前的队列信息
    // ready queue
//    put_str("\n====  ready thread list  ===\n");
//    struct list_elem* ready = (&thread_ready_list)->head.next;
//    while(ready != &(&thread_ready_list)->tail) {
//        put_int((uint32_t)(ready));
//        put_str(" -> ");
//        ready = ready->next;
//    }
//    put_str("\n====  all thread list  ===\n");
//    struct list_elem* all = (&thread_all_list)->head.next;
//    while(all != &(&thread_all_list)->tail) {
//        put_int((uint32_t)(all));
//        put_str(" -> ");
//        all = all->next;
//    }
//    put_str("\n====  end  ===\n");

    // 必须在中断关闭状态下？ 因为这是是中断产生的，进中断CPU会自动关闭，判断是关中断就处理，中断处理完CPU会将开中断
    ASSERT(intr_get_status() == INTR_OFF);

    struct task_struct* cur = running_thread();
    if(cur->status == TASK_RUNNING) {
        // 若此线程的cpu时间片到了，将其加入到就绪队列

        ASSERT(!elem_find(&thread_ready_list, &cur->general_tag));
        // 加入就绪队列
        list_append(&thread_ready_list, &cur->general_tag);
        // 重新将当前线程的ticks设置成priority，也就是重置时间片，待下次运行
        cur->ticks = cur->priority;
        // 处于预备状态
        cur->status = TASK_READY;
    } else {
        // 若此线程需要某事件发生才继续上CPU运行，不要将其加入队列，因为当前线程不在就绪队列中。 TODO 待实现其他的队列
    }

    // 就绪队列不能为空，否则没有线程可运行
    ASSERT(!list_empty(&thread_ready_list));
    thread_tag = NULL;
    thread_tag = list_pop(&thread_ready_list);
    // TODO  (int)(&((struct task_struct*)0)->general_tag) 这段没懂
    // (struct task_struct*)((int)thread_tag - (int)(&((struct task_struct*)0)->general_tag)
    struct task_struct* next = elem2entry(struct task_struct, general_tag, thread_tag);

    next->status = TASK_RUNNING;
    switch_to(cur, next);
}

/**
 * 将kernel中的main为主线程 完善该主线程，该线程不是由thread_start创建
 * 他是从开机启动后一直执行的一个执行流，他开始是没有PCB的，所有需要针对其做初始化PCB
 */
static void make_main_thread(void) {
    main_thread = running_thread();
    init_thread(main_thread, "main", 31);

    // main函数是当前线程，当前线程不在thread_ready_list中，将其加入到thread_all_list中
    ASSERT(!elem_find(&thread_all_list, &main_thread->all_list_tag));
    list_append(&thread_all_list, &main_thread->all_list_tag);
}

/**
 * 初始化线程的环境
 */
void thread_init(void) {
    put_str("thread_init start\n");
    list_init(&thread_ready_list);
    list_init(&thread_all_list);
    // 将当前main函数创建为线程
    make_main_thread();
    put_str("thread_init end\n");
}
