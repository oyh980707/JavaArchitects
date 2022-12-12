#include "print.h"
#include "init.h"
#include "memory.h"
#include "thread.h"
#include "interrupt.h"
#include "console.h"
#include "ioqueue.h"
#include "keyboard.h"

void k_thread_a(void*);
void k_thread_b(void*);

int main(void) {
    put_str("I am kernel\n");
    init_all();

    void* addr = get_kernel_pages(3);
    put_str("\n get_kernel_page start vaddr is ");
    put_int((uint32_t)addr);
    put_str("\n");

    thread_start("k_thread_a", 5, k_thread_a, "argA_");
    thread_start("k_thread_b", 5, k_thread_b, "argB_");

    put_str("open interrupt ===== start schedule");
    intr_enable(); // 打开中断使时钟中断起作用

    while(1);
//    {
//        console_put_str("Main ");
//    }
    return 0;
}

void k_thread_a(void * arg){
    char * param = arg;
    while(1) {
        enum intr_status old_status = intr_disable();
        if(!ioq_empty(&kbd_buf)) {
            console_put_str(param);
            char byte = ioq_getchar(&kbd_buf);
            console_put_str(byte);
        }
        intr_set_status(old_status);
    }
}

void k_thread_b(void * arg){
    char * param = arg;
    while(1) {
        enum intr_status old_status = intr_disable();
        if(!ioq_empty(&kbd_buf)) {
            console_put_str(param);
            char byte = ioq_getchar(&kbd_buf);
            console_put_str(byte);
        }
        intr_set_status(old_status);
    }
}

