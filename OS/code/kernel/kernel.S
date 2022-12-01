[bits 32]

%define ERROR_CODE nop ; 如果有异常，CPU已经自动压入错误码了，不做任何操作
%define ZERO push 0    ; 如果没有异常，CPU没有压入错误码，需要我们补齐格式，压入一个0，保持格式统一

extern put_str	;声明外部函数
extern idt_table ;idt_table是C中注册的中断处理程序数组

section .data
intr_str db "interrupt occur!", 0xa, 0
global intr_entry_table
intr_entry_table:

%macro VECTOR 2
section .text
; 每个中断处理程序都要压入中断向量号，所以每个中断类型都有一个中断处理程序
intr%1entry:
	%2
	; 以下是保存上下文环境
	push ds
	push es
	push fs
	push gs
	pushad ; pushad 压入32位寄存器，EAX,ECX,EDX,EBX,ESP,EBP,ESI,EDI

	push intr_str ; 打印函数的入参
	call put_str ; 调用打印函数
	add esp, 4 ; 跳过打印函数的入参

	; 如果是从片上进行的中断，除了忘从片上发送EOI外，还要往主片上发送EOI
	mov al, 0x20
	out 0xa0, al
	out 0x20, al

	; 不管idt_table中的目标程序是否需要参数，都一律压入中断向量号
	push %1
	; 调用idt_table中的C版本的中断处理函数
	call [idt_table + %1*4]

	jmp intr_exit

section .data
	; 存储个个中断入口程序的地址，形成intr_entry_table数组
	; 编译器会将属性相同的section合并到一个大的segment中
	; 所以编译之后，所有中断处理程序的地址都会乖乖的做为数组intr_entry_table的元素紧凑的排在一起
	dd intr%1entry

%endmacro

section .text
global intr_exit
intr_exit:
	; 跳过之前压入的中断号参数
	add esp, 4
	; 恢复上下文环境
	popad
	pop gs
	pop fs
	pop es
	pop ds

	add esp, 4 ; 跳过 error code，调用中断CPU会压入error code，如果没有压入，我们手动压入0，填充补齐
	
	; 此处直接iret即可，剩下的参数就是一些返回地址等等
	iret


VECTOR 0x00,ZERO
VECTOR 0x01,ZERO
VECTOR 0x02,ZERO
VECTOR 0x03,ZERO
VECTOR 0x04,ZERO
VECTOR 0x05,ZERO
VECTOR 0x06,ZERO
VECTOR 0x07,ZERO 
VECTOR 0x08,ERROR_CODE
VECTOR 0x09,ZERO
VECTOR 0x0a,ERROR_CODE
VECTOR 0x0b,ERROR_CODE 
VECTOR 0x0c,ZERO
VECTOR 0x0d,ERROR_CODE
VECTOR 0x0e,ERROR_CODE
VECTOR 0x0f,ZERO 
VECTOR 0x10,ZERO
VECTOR 0x11,ERROR_CODE
VECTOR 0x12,ZERO
VECTOR 0x13,ZERO 
VECTOR 0x14,ZERO
VECTOR 0x15,ZERO
VECTOR 0x16,ZERO
VECTOR 0x17,ZERO 
VECTOR 0x18,ERROR_CODE
VECTOR 0x19,ZERO
VECTOR 0x1a,ERROR_CODE
VECTOR 0x1b,ERROR_CODE 
VECTOR 0x1c,ZERO
VECTOR 0x1d,ERROR_CODE
VECTOR 0x1e,ERROR_CODE
VECTOR 0x1f,ZERO 
VECTOR 0x20,ZERO	;时钟中断对应的入口
VECTOR 0x21,ZERO	;键盘中断对应的入口
VECTOR 0x22,ZERO	;级联用的
VECTOR 0x23,ZERO	;串口2对应的入口
VECTOR 0x24,ZERO	;串口1对应的入口
VECTOR 0x25,ZERO	;并口2对应的入口
VECTOR 0x26,ZERO	;软盘对应的入口
VECTOR 0x27,ZERO	;并口1对应的入口
VECTOR 0x28,ZERO	;实时时钟对应的入口
VECTOR 0x29,ZERO	;重定向
VECTOR 0x2a,ZERO	;保留
VECTOR 0x2b,ZERO	;保留
VECTOR 0x2c,ZERO	;ps/2鼠标
VECTOR 0x2d,ZERO	;fpu浮点单元异常
VECTOR 0x2e,ZERO	;硬盘
VECTOR 0x2f,ZERO	;保留