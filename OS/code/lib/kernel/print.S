; 1. 找到对应的光标
; 2. 获取字符
; 3. 如果CR或者LF，调用换行的函数
; 4. 如果BS回退的字符，调用退格的函数




TI_GDT equ 0
RPL0   equ 0

SELECTOR_VIDEO equ (0x0003<<3) + TI_GDT + RPL0

section .data
put_int_buffer    dq    0     ; 定义8字节缓冲区用于数字到字符的转换

; 32位保护模式的代码
[bits 32]

section .text

;--------------------------------------------
;put_str 通过put_char来打印以0字符结尾的字符串
;--------------------------------------------
;输入：栈中参数为打印的字符串
;输出：无

global put_str
put_str:
;由于本函数中只用到了ebx和ecx,只备份这两个寄存器
   push ebx
   push ecx
   xor ecx, ecx		      ; 准备用ecx存储参数,清空
   mov ebx, [esp + 12]	      ; 从栈中得到待打印的字符串地址 
.goon:
   mov cl, [ebx]
   cmp cl, 0		      ; 如果处理到了字符串尾,跳到结束处返回
   jz .str_over
   push ecx		      ; 为put_char函数传递参数
   call put_char
   add esp, 4		      ; 回收参数所占的栈空间
   inc ebx		      ; 使ebx指向下一个字符
   jmp .goon
.str_over:
   pop ecx
   pop ebx
   ret


;------------------------ put_char ---------------
; 功能描述，把栈中的一个字节写入到光标所在位置
;-------------------------------------------------

; 通过global吧函数put_char导出位全局符号
; 这样对外部文件便可见了，通过声明便可以调用
global put_char

put_char:
    ; 备份32位寄存器环境
    ; 一共8个，他们入栈的顺序是:EAX->ECX->EDX->EBX->ESP->EBP->ESI->EDI
    pushad

    ; 需要保证gs中为正确的视频段选择子
    ; 为保险起见，每次打印时都为gs赋值
    ; 这个与系统调用高特权级回到低特权级时gs可能被置0有关
    mov ax, SELECTOR_VIDEO
    mov gs, ax

    ; ------- 获取光标位置 -------
    ; 先获取高8位
    mov dx, 0x03D4
    mov al, 0x0e
    out dx, al
    mov dx, 0x03D5
    in al, dx
    mov ah, al

    ; 再获取低8位
    mov dx, 0x03D4
    mov al, 0x0f
    out dx, al
    mov dx, 0x03D5
    in al, dx

    ; 将光标位置存入到bx中
    mov bx, ax

    ; 获取打印的字符
    ; pushad压入4*8=32个字节，加上调用函数的4个字节返回地址
    mov ecx, [esp + 36]

    ; CR 是0x0D, LF 是 0x0A,此处回车或换行统一为回车换行
    cmp cl, 0xd
    jz .is_carriage_return
    cmp cl, 0xa
    jz .is_line_feed

    ; BS(backspace)的asc码是0x8
    cmp cl, 0x8
    jz .is_backspace
    jmp .put_other

;;;;;;;;;;;;;;;;;

.is_backspace:
    ; backspace本质上是移动光标向前一个显存位置即可，后面输入的字符
    ; 自然就被覆盖了，但如果不输入就显得很怪。所以此处的做法是回退一个
    ; 显存位置后，用空字符填充覆盖掉即可
    dec bx ; bx - 1，显存位置-1，退一个显存位置，处于前一个字符位置
    shl bx, 1 ; bx * 2，每个字符占用两个字节，bx只是位置索引*2表示该位置的偏移量

    mov byte [gs:bx], 0x20 ; 填充空字符
    inc bx
    mov byte [gs:bx], 0x07 ; 填充空字符的属性
    shr bx, 1
    jmp .set_cursor

.put_other:
    ; 打印可见字符,原理类似回退格的逻辑
    shl bx, 1
    mov byte [gs:bx], cl
    inc bx
    mov byte [gs:bx], 0x07
    shr bx, 1
    inc bx

    cmp bx, 2000
    jl .set_cursor

.is_line_feed:
.is_carriage_return:
; 如果是CR或者LF，直接移动到行首
    xor dx, dx
    mov ax, bx
    mov si, 80

    div si ; div 16位除法，dx存储余数，ax存储商

    sub bx, dx 

.is_carriage_return_end:
    add bx, 80
    cmp bx, 2000
.is_line_feed_end:
    jl .set_cursor

; 屏幕行数显示范围0～24，滚屏原理将屏幕的1～24行搬到0～23行
; 再将24行用空格填充
; 另一种可缓存的实现复杂一些，这里简单实现

.roll_screen:
    cld ; 清除方向为，在eflags寄存器中
    mov ecx, 960 ; 2000-80=1920个字符需要搬，1920*2=3840字节需要搬
                 ; 一次搬4个字节，3840/4=960次搬运
    mov esi, 0xc00b80a0 ; 第一行行首地址
    mov edi, 0xc00b8000 ; 第0行行首

    rep movsd ; 开始复制960次

    ; 最后一行填充空白
    mov ebx, 3840
    mov ecx, 80

.cls:
    mov word [gs:ebx], 0x0720 ; 0x0720 黑底白字的空格键
    add ebx, 2
    loop .cls ; ecx 为条件 自动减1 直到为0 退出循环
    mov bx, 1920 ; 光标放于最后一行行首

.set_cursor:
; 将光标设为bx的值
    ; 1. 先设置高8位
    mov dx, 0x03D4
    mov al, 0x0e
    out dx, al

    mov dx, 0x03D5
    mov al, bh
    out dx, al

    ; 2. 在设置高8位
    mov dx, 0x03D4
    mov al, 0x0f
    out dx, al

    mov dx, 0x03D5
    mov al, bl
    out dx, al

.put_char_done:
    popad
    ret


;--------------------   将小端字节序的数字变成对应的ascii后，倒置   -----------------------
;输入：栈中参数为待打印的数字
;输出：在屏幕上打印16进制数字,并不会打印前缀0x,如打印10进制15时，只会直接打印f，不会是0xf
;------------------------------------------------------------------------------------------
global put_int
put_int:
   pushad
   mov ebp, esp
   mov eax, [ebp+4*9]              ; call的返回地址占4字节+pushad的8个4字节
   mov edx, eax
   mov edi, 7                          ; 指定在put_int_buffer中初始的偏移量
   mov ecx, 8                  ; 32位数字中,16进制数字的位数是8个
   mov ebx, put_int_buffer

;将32位数字按照16进制的形式从低位到高位逐个处理,共处理8个16进制数字
.16based_4bits:                ; 每4位二进制是16进制数字的1位,遍历每一位16进制数字
   and edx, 0x0000000F             ; 解析16进制数字的每一位。and与操作后,edx只有低4位有效
   cmp edx, 9                  ; 数字0～9和a~f需要分别处理成对应的字符
   jg .is_A2F 
   add edx, '0'                ; ascii码是8位大小。add求和操作后,edx低8位有效。
   jmp .store
.is_A2F:
   sub edx, 10                 ; A~F 减去10 所得到的差,再加上字符A的ascii码,便是A~F对应的ascii码
   add edx, 'A'

;将每一位数字转换成对应的字符后,按照类似“大端”的顺序存储到缓冲区put_int_buffer
;高位字符放在低地址,低位字符要放在高地址,这样和大端字节序类似,只不过咱们这里是字符序.
.store:
; 此时dl中应该是数字对应的字符的ascii码
   mov [ebx+edi], dl               
   dec edi
   shr eax, 4
   mov edx, eax 
   loop .16based_4bits

;现在put_int_buffer中已全是字符,打印之前,
;把高位连续的字符去掉,比如把字符000123变成123
.ready_to_print:
   inc edi                 ; 此时edi退减为-1(0xffffffff),加1使其为0
.skip_prefix_0:  
   cmp edi,8                   ; 若已经比较第9个字符了，表示待打印的字符串为全0 
   je .full0 
;找出连续的0字符, edi做为非0的最高位字符的偏移
.go_on_skip:   
   mov cl, [put_int_buffer+edi]
   inc edi
   cmp cl, '0' 
   je .skip_prefix_0               ; 继续判断下一位字符是否为字符0(不是数字0)
   dec edi                 ;edi在上面的inc操作中指向了下一个字符,若当前字符不为'0',要恢复edi指向当前字符              
   jmp .put_each_num

.full0:
   mov cl,'0'                  ; 输入的数字为全0时，则只打印0
.put_each_num:
   push ecx                ; 此时cl中为可打印的字符
   call put_char
   add esp, 4
   inc edi                 ; 使edi指向下一个字符
   mov cl, [put_int_buffer+edi]        ; 获取下一个字符到cl寄存器
   cmp edi,8
   jl .put_each_num
   popad
   ret


