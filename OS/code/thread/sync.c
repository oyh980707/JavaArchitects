//
// Created by oyh.jerry@Macbook Pro on 2022/11/30.
//
#include "sync.h"
#include "global.h"
#include "interrupt.h"
#include "debug.h"

/**
 * 初始化信号量
 */
void sema_init(struct semaphore* semaphore, uint8_t value) {
    semaphore->value = value;
    list_init(&semaphore->waiters);
}

/**
 * 初始化锁
 */
void lock_init(struct lock* lock) {
    lock->holder = NULL;
    // 信号量初始值为1
    sema_init(&lock->semaphore, 1);
    lock->holer_repeat_nr = 0;
}

/**
* 信号量down操作
*/
void sema_down(struct semaphore* psema) {
    // 保证原子性
    enum intr_status old_status = intr_disable();

    while (psema->value == 0) {
        // 当前锁被别的线程抢占，判断当前线程是否在等待队列中，理论不应在等待队列中
        ASSERT(!elem_find(&psema->waiters, &running_thread()->general_tag));
        if(elem_find(&psema->waiters, &running_thread()->general_tag)) {
            PANIC("sema_down: thread blocked has been in waiters_list.\n");
        }
        // 加入到当前信号的等待队列中
        list_append(&psema->waiters, &running_thread()->general_tag);
        // 阻塞自己
        thread_block(TASK_BLOCKED);
    }
    // 若value=1或被唤醒后可以拿到锁，会执行下面代码，获取锁
    psema->value--;
    // 恢复中断之前状态
    intr_set_status(old_status);
}

/**
 * 信号量up操作
 */
void sema_up(struct semaphore* psema) {
    // 保证原子性
    enum intr_status old_status = intr_disable();
    ASSERT(psema->value == 0);
    // 等待队列中有线程等待，唤醒
    if(!list_empty(&psema->waiters)) {
        struct task_struct* thread_blocked = elem2entry(struct task_struct, general_tag, list_pop(&psema->waiters));
        thread_unblok(thread_blocked);
    }
    psema->value++;
    // 恢复中断之前状态
    intr_set_status(old_status);
}

/**
 * 释放锁
 */
void lock_release(struct lock* plock) {
    ASSERT(plock->holder == running_thread());
    if(plock->holer_repeat_nr > 1) {
        plock->holer_repeat_nr--;
        return;
    }

    // 必须在V操作之前，因为这里随时被线程切换，要是先释放信号，被切换其他线程拿到做，获得锁后，切换回来，被这里把锁持有给清空了，会有异常发生
    plock->holder = NULL;
    plock->holer_repeat_nr = 0;
    // 信号的V操作，原子操作
    sema_up(&plock->semaphore);
}

/**
 * 获得锁
 */
void lock_acquire(struct lock* plock) {
    if(plock->holder != running_thread()) {
        // 信号P操作，原子操作
        sema_down(&plock->semaphore);
        plock->holder = running_thread();
        ASSERT(plock->holer_repeat_nr == 0);
        plock->holer_repeat_nr = 1;
    } else {
        plock->holer_repeat_nr++;
    }
}



