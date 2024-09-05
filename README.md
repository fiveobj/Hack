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
#### 目录结构

* main/java-代码文件
  * main：文件读取、字符处理、输出结果
  * Parser：分析命令类型、获取命令参数
    * list结构存储命令行
  * CodeWriter：将VM命令翻译成Hack汇编代码
  * CommandType：命令类型
  * tools：工具类
* main/resources-测试文件

#### 实现思路

1. 读取文件夹下的所有vm文件，去掉空行、注释行等
2. 构造Parser对象，解析命令
3. 构造CodeWriter对象，翻译命令
4. Parser遍历命令行，解析结果作为命令判断条件和参数，通过CodeWriter进行翻译
5. 文件夹下的所有vm文件翻译结果汇总
6. 添加初始化命令到结果文件
7. 追加翻译结果到结果文件
   
## Compiler-hackToxml
#### 目录结构

* main/java-代码文件
  * Main:文件读取、代码预处理、执行结果
  * CompilationEngine:根据字元和语法分析输出结果
  * CompilationEngine2:在CompilationEngine的基础上 翻译成VM代码
  * JackTokenizer:解析代码字元
  * VMWriter:生成对应的Vm代码
  * SymbolTable:符号表
  * tools:工具类
  * xmlParam:标签类
  * enumfile
    * VMCommand:运算符
    * VMkind:虚拟段
    * symbolKind:变量分类
    * tokenType:五种字元类型
* main/resources-测试文件

#### 实现思路

1. 读取文件，处理单行注释、多行注释、文档注释，并生成中间文件
2. 构造JackTokenizer对中间文件进行处理，获得字元
   * 使用在正则表达式对字元进行提取
3. 构造CompilationEngine2对JackTokenizer中的字元根据语法规则进行匹配并生成对应的VM代码
4. 最终输出结果到xml文件、vm文件
