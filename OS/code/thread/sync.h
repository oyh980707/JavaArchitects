//
// Created by oyh.jerry@Macbook Pro on 2022/11/30.
//

#ifndef __THREAD_SYNC_H
#define __THREAD_SYNC_H
#include "list.h"
#include "stdint.h"
#include "thread.h"

/**
 * 信号量结构
 */
struct semaphore {
    // 信号量
    uint8_t value;
    // 等待队列 -- 次信号上所有等待的线程
    struct list waiters;
};

/**
 * 锁结构
 */
struct lock {
    // 锁持有者
    struct task_struct* holder;
    // 用二元信号量实现锁
    struct semaphore semaphore;
    // 锁持有者重复申请锁的次数
    uint32_t holer_repeat_nr;
};

void sema_init(struct semaphore* semaphore, uint8_t value);
void lock_init(struct lock* lock);
void sema_down(struct semaphore* psema);
void sema_up(struct semaphore* psema);
void lock_release(struct lock* plock);
void lock_acquire(struct lock* plock);

#endif //__THREAD_SYNC_H
