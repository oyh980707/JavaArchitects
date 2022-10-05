# 操作系统还原

## Bochs
Bochs是一个x86硬件平台的开源模拟器
### 安装

1. 下载版本 https://sourceforge.net/projects/bochs/files/bochs/2.6.2/
2. 配置

```txt
./configure \
--prefix=/home/lihaifei/bochs-2.6.2 \
--enable-debugger \
--enable-disasm \
--enable-iodebug \
--enable-x86-debugger \
--with-x \
--with-x11 \
LDFLAGS='-pthread'
```

3. 安装(make install)

4. 常见错误

```txt
gtk_enh_dbg_osdep.cc:20:21: 致命错误：gtk/gtk.h：没有那个文件或目录

添加依赖
yum install gtk2 gtk2-devel gtk2-devel-docs

常见错误2
/usr/bin/ld: gui/libgui.a(gtk_enh_dbg_osdep.o): undefined reference to symbol 'pthread_create@@GLIBC_2.2.5'
//usr/lib64/libpthread.so.0: error adding symbols: DSO missing from command line

vim Makefile 文件， 在 92 行增加 -lphread.然后重新 make
```


### 配置文件

share/doc/bochs/bochsrc-sample.txt 该文件有事例

bochs根目录创建文件：bochsrc.disk
```txt
megs: 32

romimage: file=/root/bochs/share/bochs/BIOS-bochs-latest
vgaromimage: file=/root/bochs/share/bochs/VGABIOS-lgpl-latest

boot: disk

log: bochsout.txt
mouse: enabled=0
keyboard: type=mf, serial_delay=250

ata0: enabled=1, ioaddr1=0x1f0, ioaddr2=0x3f0, irq=14
# hd60M.img 镜像文件需要创建
# bochs根目录执行：bin/bximage -hd -mode="flat" -size=60 -q hd60M.img
ata0-master: type=disk, path="hd60M.img", mode=flat, cylinders=121, heads=16, spt=63

# 报错：Bochs is not compiled with gdbstub support
# 注释下面 不启动调试
#gdbstub: enabled=1, port=1234, text_base=0, data_base=0, bss_base=0

```

### 启动

```txt
./bin/bochs -f ./bochsrc.disk

报错 >>PANIC<< Cannot connect to X display
原因：我没有图形界面，只有console

# Centos 7x
yum groups install "GNOME Desktop"
# 安装后不能用远程连接，如果是最小版linux，在安装图形界面后输入指令 init 5 来重启系统进入图形界面
# 图形界面下打开终端启动bochs成功

```

### 简单解释

```txt
You can also start bochs with the -q option to skip these menus.

1. Restore factory default configuration
2. Read options from...
3. Edit options
4. Save options to...
5. Restore the Bochs state from...
6. Begin simulation
7. Quit now

Please choose one: [6] 
00000000000i[     ] installing x module as the Bochs GUI
00000000000i[     ] using log file bochsout.txt
Next at t=0
(0) [0x0000fffffff0] f000:fff0 (unk. ctxt): jmp far f000:e05b         ; ea5be000f0
<bochs:1> 


# f000:fff0 表示cs 和 ip
# jmp far f000:e05b 执行的第一条命令 跳转到 0xfe05b

```

## 主引导程序

1. 下载安装汇编编译器

wget https://www.nasm.us/pub/nasm/releasebuilds/2.14/nasm-2.14.tar.gz --no-check-certificate
tar -xvzf nasm-2.14.tar.gz
cd nasm-2.14
./configure
make
make install

2. 编码主引导MBR程序
vim mbr.S
```txt
; 主引导程序MBR
; SECTION是伪指令，cpu不运行，只是方便程序员规划程序分段使用
; `vstart=0x7c00`表示在程序编译时将起始地址编译为0x7c00
; SS存放栈顶的段地址，SP存放栈顶的偏移地址。在任何时刻 ，SS:SP都是指向栈顶元素 
; CS存放内存中代码段入口的段基址，CS:IP表示下一条要运行的指令内存地址   
    
; 初始化部分
SECTION MBR vstart=0x7c00 ; =前后不能有空格
	mov ax,cs			; 由于BIOS是通过`jmp 0:Ox7c00`转到MBR的，故cs此时为0
    mov ds,ax			; 段寄存器不能使用立即数进行赋值，可以使用通用寄存器ax
    mov es,ax
    mov ss,ax
    mov fs,ax
	mov sp,0x7c00
    mov ax,0xb800
    mov gs,ax
        
; 使用10号中断的0x06功能号，进行窗口上卷清屏，避免BIOS检测信息影响显示
	mov ax,0600h		; ah存放将要调用的中断子功能号
    mov bx,0700h
    mov cx,0			; (CL,CH)＝窗口左上角的（X,Y）位置
    mov dx,184fh		; (DL,DH)＝窗口右下角的（X,Y）位置(80,25)
    int 10h				; 调用中断
; 输出背景色是绿色，前景色是红色，并且跳动的字符串为“1 MBR”
    mov byte [gs:0x00],'1' 	;把字符1的ASCII写入以gs:0x00为起始，大小为1字节的内存
    mov byte [gs:0x01],0xA4	; A表示绿色背景闪烁，4表示前景色为红色
    
    mov byte [gs:0x02],' '
    mov byte [gs:0x03],0xA4
    
    mov byte [gs:0x04],'M'
    mov byte [gs:0x05],0xA4
    
    mov byte [gs:0x06],'B'
    mov byte [gs:0x07],0xA4
    
    mov byte [gs:0x08],'R'
    mov byte [gs:0x09],0xA4
    
; $表示本行指令所在的地址，$$表示本section的起始地址，$-$$表示执行代码行到段首的偏移量
	jmp $					; 在本行代码死循环
    times 510-($-$$) db 0	; 将剩余字节用0进行填充
    db 0x55,0xaa			; 最后两个字节填充MBR的标识
```

3. 编译汇编程序
nasm -o mbr.bin mbr.S

4. 将编译好的二进制数据写入到用来启动的镜像文件

dd if=/root/bochs/mbr.bin of=/root/bochs/hd60M.img bs=512 count=1 conv=notrunc

结果如下：
记录了1+0 的读入
记录了1+0 的写出
512字节(512 B)已复制，0.0002695 秒，1.9 MB/秒

5. 启动

bochs根目录下：bin/bochs -f bochsrc.disk

6. 在bochs控制台输入 c + 回车 表示continue 继续下一步调试

可以看到bochs所模拟的机器开始运行了



## 实模式

实模式是指 8086 CPU 的寻址方式、寄存器大小、指令用法等，用来反应CPU在该环境下如何工作的概念



## 汇编语言 介绍
### 8086 的汇编指令


。。。


16位是模式下的call调用

16位实模式call调用的源代码案例：1call.asm
```asm
call near near_proc
jmp $
addr dd 4
near_proc:
 mov ax,0x1234
 ret
```
编译成二进制文件：nasm -o 1call.bin 1call.asm
查看查看文件对应的十六进制形式：xxd -u -a -g 1 -s 0 -l 13 1call.bin // 文件13个字节， 0-13

输出结果：
0000000: E8 06 00 EB FE 04 00 00 00 B8 34 12 C3           ..........4..

E8 06 00：表示指令，近调用。E8表示近调用，0x06操作数
EB FE：表示 jmp $ 指令，EB操作码，0xFE表示操作数，由于操作数有符号数，所以这个表示-2
04 00 00 00：表示定义四个字节的数据
B8 34 12：B8表示mov操作码，3412表示0x1234
C3：表示ret操作码

16位实模式间接绝对近调用的源代码案例：2call.asm

```asm
section call_test vstart=0x900
# word 16位的实模式下表示两个字节 16位
mov word [addr], near_proc
call [addr]
mov ax, near_proc
call ax
jmp $
addr dd 4
near_proc:
 mov ax, 0x1234
 ret
```
编译成二进制文件：nasm -o 2call.bin 2call.asm
查看查看文件对应的十六进制形式：xxd -u -a -g 1 -s 0 -l 25 2call.bin

输出结果：
0000001: 06 11 09 15 09 · 11 09 B8 15 09 FF D0 EB FE  ................
0000011: 04 00 00 00 B8 34 12 C3                          .....4..

16位实模式直接绝对远调用的源代码案例：3call.asm
```asm
section call_test vstart=0x900
call 0: far_proc
jmp $
far_proc:
 mov ax, 0x1234
 retf
```
编译成二进制文件：nasm -o 3call.bin 3call.asm
查看查看文件对应的十六进制形式：xxd -u -a -g 1 -s 0 -l 11 3call.bin
输出结果：
0000000: 9A 07 09 00 00 EB FE B8 34 12 CB                 ........4..

9A：操作码 call
07090000：操作数 0000 ： 0907 表示地址 0x907
EB FE：jmp $的指令 jmp -2
B8：mov指令
34 12：操作数，0x1234
CB：retf

16位实模式间接绝对远调用的源代码案例：4call.asm
```asm
section call_test vstart=0x900
call far [addr]
jmp $
addr dw far_proc,0
far_proc:
 mov ax, 0x1234
 retf
```
编译成二进制文件：nasm -o 4call.bin 4call.asm
查看查看文件对应的十六进制形式：xxd -u -a -g 1 -s 0 -l 14 4call.bin
输出结果：
0000000: FF 1E 06 09 EB FE 0A 09 00 00 B8 34 12 CB        ...........4..

FF 1E：间接绝对远调用call的操作码

16位实模式下的jmp调用
jmp指令通过修改段寄存器CS和IP就可以实现程序流程的跳转，无需保存CS和IP，因为无需返回

16位实模式相对短转移的源码案例：1jmp.asm
```asm
section jmp_test vstart=0x900
jmp short start ;short 可以省略 短转移的操作数范围：-128 ～ 127
times 127 db 0 ;定义了127个字节为0
start:
 mov ax, 0x1234
 jmp $
```
编译成二进制文件：nasm -o 1jmp.bin 1jmp.asm
查看查看文件对应的十六进制形式：xxd -u -a -g 1 -s 0 -l 134 1jmp.bin
输出结果：
0000000: EB 7F 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
0000010: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
*
0000080: 00 B8 34 12 EB FE                                ..4...

EB：操作码jmp
7F：操作数，由于有正负，所以该数值表示的127   0111 1111：表示的是127

如果将上述的 127 改为 128
```asm
section jmp_test vstart=0x900
jmp short start ;short 可以省略 短转移的操作数范围：-128 ～ 127
times 128 db 0 ;定义了128个字节为0
start:
 mov ax, 0x1234
 jmp $
```
则操作数位128 ， 编译就会报错
1jmp.asm:2: error: short jump is out of range
1jmp.asm:2: warning: byte data exceeds bounds \[-w+number-overflow\]
去掉short就不会报错，改成near或者不加，让其成为相对近转移

16位实模式相对近转移的源码案例：2jmp.asm
```asm
section jmp_test vstart=0x900
jmp near start ;near 可以省略 近转移的操作数范围：-32768 ～ 32767
times 128 db 0 ;定义了128个字节为0
start:
 mov ax, 0x1234
 jmp $
```
编译二进制文件结果：
0000000: E9 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
0000010: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
*
0000080: 00 00 00 B8 34 12 EB FE                          ....4...

还有
16位实模式间接绝对近转移，与call类似

16位实模式绝对远转移的源码案例：3jmp.asm
```asm
section jmp_test vstart=0x900
jmp 0: start
times 128 db 0
start:
 mov ax, 0x1234
 jmp $
```
二进制文件：
0000000: EA 85 09 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
0000010: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
*
0000080: 00 00 00 00 00 B8 34 12 EB FE                    ......4...

还有
16位实模式间接绝对远转移


标志寄存器flags
上面说的是无条件转移，有条件转移中的该条件就存储在flags寄存器中，有条件转移的指令族 jxx
条件转移指令jxx 一定得在某个能够影响标志位的指令之后进行，






