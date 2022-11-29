#include "timer.h"
#include "io.h"
#include "print.h"
#include "thread.h"
#include "debug.h"
#include "interrupt.h"

#define IRQ0_FREQUENCY 100 // 设置的时钟频率 100HZ，也就是IRQ0引脚上的时钟中断信号 每秒钟100次中断
#define INPUT_FREQUENCY 1193180 // 计数器0的工作脉冲信号频率
#define COUNTER0_VALUE INPUT_FREQUENCY / IRQ0_FREQUENCY // 计数器0的初始值
#define COUNTER0_PORT 0x40 // 计数器0的端口号0x40
#define COUNTER0_NO 0 // 控制字中选择计数器的号码，表示计数器0
#define COUNTER0_MODE 2 // 计数器0的工作方式，比率发生器
#define READ_WRITE_LATCH 3 // 读写方式，表示先读写低8位，在读写高8位
#define PIT_CONTROL_PORT 0x43 // 控制字寄存器的端口

/**
 * ticks表示内核自中断开启以来总共的滴答数
 */
uint32_t ticks;

static void intr_timer_handler(void) {
    struct task_struct* cur_thread = running_thread();

    // 检查是否溢出
    ASSERT(cur_thread->stack_magic == 0xcafebabe);

    // 记录时间片
    cur_thread->elapsed_ticks++;
    ticks++;

    // 若当前线程没有时间片则发生调度，否则继续执行
    if(cur_thread->ticks == 0) {
        schedule();
    } else {
        cur_thread->ticks--;
    }
}

// 把操作的计数器counter_no，读写锁属性rwl，计数器模式counter_mode写入模式控制寄存器并赋予初始值counter_value
static void frequency_set(uint8_t counter_port, \
							uint8_t counter_no, \
							uint8_t rwl, \
							uint8_t counter_mode, \
							uint16_t counter_value) {
	// 先写控制字
	// 7~6 指定计数器，5～4 读写方式，3～1 工作方式，最后一位进制格式0二进制 1BCD码
	outb(PIT_CONTROL_PORT, (uint8_t)(counter_no << 6 | rwl << 4 | counter_mode << 1));
	// 在写counter_value值的低8位
	outb(counter_port, (uint8_t)(counter_value));
	// 在写counter_value值的高8位
	outb(counter_port, (uint8_t)(counter_value >> 8));
}

void timer_init() {
	put_str("timer_init start\n");
	frequency_set(COUNTER0_PORT, COUNTER0_NO, READ_WRITE_LATCH, COUNTER0_MODE, COUNTER0_VALUE);
    // 注册中断向量号和对应的中断处理函数
    register_handler(0x20, intr_timer_handler);
	put_str("timer_init done\n");
}


				