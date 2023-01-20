## Mx* 编译器

实现语言：Java

### 简介

一个编译器可以分为三部分：

1. Frontend
   词法分析、语法分析、语义分析、生成中间代码
2. Optimizer
   中间代码优化
3. Backend
   生成机器码

而在我的实现上，分为以下三个阶段：

- Semantic
- Codegen
- Optimization

### Semantic

利用 antlr 实现语义分析，以及语法的检查

更具体地说，

1. 利用 antlr 把读入的字符串分解为一个个 token，建立一棵 CST（antlr根据g4自动完成）
2. 然后将其还原为 AST（Abstract Syntax Tree 语法树）
3. 进行 Semantic Check（语义检查）

首先，你需要了解 antlr 的[基本语法](https://blog.csdn.net/pourtheworld/article/details/108304505?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522163324419316780255290255%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=163324419316780255290255&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v31_ecpm-2-108304505.first_rank_v2_pc_rank_v29&utm_term=g4%E5%9F%BA%E6%9C%AC%E8%AF%AD%E6%B3%95&spm=1018.2226.3001.4187) 以及 [正则表达式的基本语法](https://www.runoob.com/regexp/regexp-syntax.html)

有一个小问题：G4 匹配时，优先匹配最长的lexer，如果有多个，再匹配写在前面的 lexer（顺序上靠前）

> 权威指南比较复杂，可以适当选择阅读。最后有语法规则详解，建议认真阅读

- [x] 完成 `Mx.g4` 文件

  有两种实现方法：（以 expression 的遍历为例）

  - 参考 antlr 官方给的 `Cpp14Parser.g4` 实现（我的选择） 
    语法树上的一系列 `expressionNode` 是一条长链，保存信息 **较麻烦**
  - 利用 `#tag` 进行别名的命名。
    `expressionNode` 是分叉的，统统称为 `binaryExpression`，方便用 if 区分

- [x] 将 Antlr 生成的 CST 重载，舍弃一部分信息，生成 AST，重载相关结点与函数
  即实现一个 `ASTBuilder.java` 以及相应的 `node.java, visitor.java`
  **目的**：对某些结点的信息进行重新收集，以提高读取时的效率
  大部分结点的重载和内容与 `g4` 文件相符合，除了 `expression.java` 类，它并不是包含 `assignmentExpression`，而是一个`Arraylist<expressionNode>`，并且所有的 `expressionNode` 都以之为基类。
  `statementNode` 的做法类似

- [x] 进行 Semantic Check，检查是否满足语法规则
  - [x] 实现 `scope`/`globalScope` 类，存储变量的作用范围（使用 `Hashmap` 实现）
  - [x] `Error` 类，用来 throw 错误信息，同时用 `pos` 来存储结点在 原代码 对应的位置。
  - [x] 自行实现 `Type`/`ClassType`/`FuncType`，用来存储 ASTNode 中的信息，进行比较判断
  - string is a special class, not a type. 所以Type中没有 `STRING_TYPE`，而是通过 `name = "string"`来区分
  - [x] 实现一个 `SymbolCollector`，先对 AST 进行遍历，把声明过的 class 都加入 `globalScope `  中（只需要遍历第一层结点）。同时也要加入内置类型：int/void/bool/string(作为class存储)，内置函数：print/toString/size...
  - 需要判断的地方（详细语法规则请参考 guide 中的 MxRules.md）
    - 判断变量是否重名/有定义过
    - 赋值表达式是否符合变量类型（对一系列`expressionNode`的运算做判断）
    - 函数的返回值 (return) 是否符合变量类型。`main` 函数可以没有返回值，默认返回值为 `0`，否则只能为int。
    - 函数/Lambda表达式 的嵌套问题，每进入一次就是一个新的scope; 注意 & 符号限制了外部变量的访问

### Codegen

1. 把上个阶段生成的AST转换为IR（中间代码 Intermediate Representation，类似线性结构），然后用顺序遍历IR，输出一个 `llvm.ll` 文件
2. 然后用 `llvm.ll` 生成最后的　`.s` 汇编文件

用 IF 语句为例，实际上就是把原本树形的AST转化为一个个线性的 basic block（每个大写标识符之间的部分）

```
IF_COND
  a>b
  jump IF_ELSE
IF_THEN
  xxx
  jump END
IF_ELSE
  yyy
  jump END
END      
```

~~事实上，实现的ll远比它更复杂~~

建议直接使用 [LLVM](https://www.zhihu.com/column/c_1267851596689457152)，有利于第三阶段的优化

[LLVM指令集参考](https://blog.csdn.net/qq_37206105/article/details/115274241)

LLVM（low level virtual machine）是一个开源编译器框架，能够提供程序语言的编译期优化、链接优化、在线编译优化、代码生成。

> clang是一款基于llvm实现的轻量级C语言编译器

还要学习 llvm IR 的语法规则，一些重要概念：

- 数据存放的区域：Disk/Stack Memory/register
- ~~可执行文件的符号表(与变量的可见性有关，在Mx中不需要实现)~~
- Disk上的全局变量 `@varName`
- 虚拟寄存器 `%1`，`%2`... 寄存器的速度远大于栈内存
  - 需要模拟寄存器的分配（在asm阶段实现）
- 栈上变量 `%local_variable = alloca i32` (我们定义的Mx没有指针)
- 全局变量和栈上变量，都是指针
- SSA(Static Single Assignment), 每个变量只能被赋值一次
  - 把可变变量放到全局变量或者栈内变量里，虚拟寄存器只存储不可变的变量
- `align 4` 的意义就应该是：向4对齐，即便数据没有占用4个字节，也要为其分配4字节的内存
- 聚合类型（结构体）的定义，聚合指针 `getelementptr`

Todo:

- [x] 实现 `IRType`，保存必要信息
- [x] 确定 IR 大致架构，分为哪些部分
- [x] 完成具体的Nodes，比如 `classDef`
- [x] 在 `statement` 中，实现对某些 LLVM IR 指令的存储和翻译，比如 `alloca`, `br`...
- [x] 实现 `IRBuilder` 以及 `IRPrinter`

Assembly Language 即汇编语言（RISC-V），可以简写为 ASM。

把自己定义的 IR 转化为 assembly 输出

![](https://img-blog.csdnimg.cn/76b4f20b8bb447d297759f98d3434a07.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQ2VybWFu,size_20,color_FFFFFF,t_70,g_se,x_16)

- [x] 定义 Asm 中的相关指令，得到大致的 Asm code 框架
- [x] 输出大致的 Asm，实现 AsmBuilder
- [x] 进行 [liveness analysis](https://en.wikipedia.org/wiki/Live-variable_analysis)（live variable analysis），即变量的活跃性分析
- [x] 进行寄存器分配（把用数字编号的虚拟寄存器，转化为RV32I指令集的通用寄存器）
- [x] 实现 AsmPrinter，得到 `.s` 文件

> 检查正确性：
> 
> `clang -S -emit-llvm test.c` ：从 test.c 生成 test.ll
> 
> `llc test.ll` ：从 test.ll 生成 test.s
> 
> `clang -S test.c` ：直接从test.c 生成 test.s
> 
> 配置好 [ravel](https://github.com/Yveh/ravel/tree/bd8e38e0cfd57dd6b1d108b224c1c4966485de96)，一个用 c++ 实现的 simulator
> 
> `export PATH="/usr/local/opt/bin:$PATH"` 配置好[环境变量](https://blog.csdn.net/xkx_07_10/article/details/128143925)（也就是ravel安装的位置），这是针对每次打开的窗口都要做。也可以直接到 .bashrc 里面修改
> 
> `ravel --oj-mode` ，以 `test.s` 和 `builtin.s` 作为 source，输入 `test.in`，输出到 test.out 中

有一些必须实现/注意的点：
- 高维数组
- 短路求值
- x86的指针与 RISCV32 的指针的区别

[java_final解释](https://blog.csdn.net/tuoniaoxs/article/details/125114271)

### Optimization

对 codegen 阶段进行优化，加快速度

包括图染色等算法？