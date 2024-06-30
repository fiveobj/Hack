## Assembler-asmTohack
#### 目录结构

* main/java-代码文件
  * main：文件读取、字符处理、输出结果
  * parser：语法分析器
    * list结构存储命令行
  * code：将Hack汇编语言助记符翻译成二进制码
    * map结构存储助记符及对应二进制
  * SymbolTable：符号表
    * map结构存储符号及对应地址
  * tools：工具类
    * isNumeric：判断字符串内容是否为数字
    * mergeByteArraysToString：将比特数组拼接成字符串
    * getFileNameWithoutExtension：获取文件路径中的文件名称，不带文件格式后缀
* main/resources-测试文件

#### 实现思路

1. 读取文件，将每行命令用List结构存储，存储前处理注释行/空行
2. 处理符号，将新符号添加到符号表(SymbolTable)中
3. 翻译文件

## VirtualMachine-vmToasm
