#include "print.h"
#include "init.h"

void main(void) {
   put_str("I am kernel\n");
   init_all();
   asm volatile("sti"); // 演示 临时开中断
   while(1);
}

