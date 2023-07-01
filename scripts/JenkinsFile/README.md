# README

## 遇到问题总结

在 Jenkins 中执行 shell 脚本（使用 sh 命令）时，每个 shell 命令都是在一个单独的 shell 进程中运行的。因此，如果你在一个 sh 命令中使用 cd 命令来切换工作目录，这只会在该 shell 进程中生效，而不会影响 Jenkins 构建执行的目录。







