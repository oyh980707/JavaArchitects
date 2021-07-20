# Shell 脚本

## 常用

DOS与Linux的换行符转换

```shell
yum -y install dos2unix*

dos2unix -n oldfile newfile
unix2dos -n oldfile newfile
```

linux 命令补全

```shell
yum -y install bash-completion
```

## vim环境设置参数

```shell
#!/bin/bash
rm -rf ~/.vimrc
echo "set hlsearch" >> ~/.vimrc
echo "set backspace=2" >> ~/.vimrc
echo "set autoindent" >> ~/.vimrc
echo "set ruler" >> ~/.vimrc
echo "set showmode" >> ~/.vimrc
echo "set nu" >> ~/.vimrc
echo "set bg=dark" >> ~/.vimrc
echo "set list" >> ~/.vimrc
echo "syntax on" >> ~/.vimrc

set hlsearch #高亮度反白
set backspace=2	#随时使用退格键删除
set autoindent	#自动缩紧
set ruler		#显示最后一行的状态
set showmode	#左下角一行的状态
set nu	#可以在每行前面显示行数
set bg=dark	#显示不同底色的色调
set list #显示非可见字符 Tab 的地方会以 ^I 显示，而行尾之 EOL 會显示成 $。
syntax on	# 进行语法检查
```

