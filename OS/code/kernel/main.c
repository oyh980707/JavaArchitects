#include "print.h"
#include "init.h"
#include "debug.h"
#include "memory.h"
#include "thread.h"
#include "interrupt.h"

void k_thread_a(void*);
void k_thread_b(void*);

int main(void) {
    put_str("I am kernel\n");
    init_all();

    void* addr = get_kernel_pages(3);
    put_str("\n get_kernel_page start vaddr is ");
    put_int((uint32_t)addr);
    put_str("\n");

    thread_start("k_thread_a", 31, k_thread_a, "argA ");
    thread_start("k_thread_a", 10, k_thread_b, "argB ");

    put_str("open interrupt ===== start schedule");
    intr_enable(); // 打开中断使时钟中断起作用

    while(1) {
        put_str("Main ");
    }
    return 0;
}

void k_thread_a(void * arg){
    char * param = arg;
    while(1) {
        put_str(param);
    }
}

void k_thread_b(void * arg){
    char * param = arg;
    while(1) {
        put_str(param);
    }
}

