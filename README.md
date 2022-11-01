## Mx* 编译器

实现语言：Java

分为三部分：

- Semantic
- Codegen
- Optimization

### Semantic

利用 antlr 实现语义分析，以及语法的检查

更具体地说，

1. 利用 antlr 把读入的字符串分解为一个个 token
2. 然后将其还原为 AST（Abstract Syntax Tree 语法树）
3. 进行  Semantic Check（语义检查）

首先，你需要了解 antlr 的[基本语法](https://blog.csdn.net/pourtheworld/article/details/108304505?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522163324419316780255290255%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=163324419316780255290255&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v31_ecpm-2-108304505.first_rank_v2_pc_rank_v29&utm_term=g4%E5%9F%BA%E6%9C%AC%E8%AF%AD%E6%B3%95&spm=1018.2226.3001.4187) 以及 [正则表达式的基本语法](https://www.runoob.com/regexp/regexp-syntax.html)

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

  > 大部分结点的重载和内容与 `g4` 文件相符合，除了 `expression.java` 类，它并不是包含 `assignmentExpression`，而是如下方：
  > >
  > > ```java
  > > public class expressionNode extends ASTNode{
  > >
  > >     public ArrayList<expressionNode> exprList = null;
  > >     public boolean isConst = false;
  > >
  > >     public expressionNode(position pos) {
  > >         super(pos);
  > >     }
  > >
  > >     @Override
  > >     public void accept(ASTVisitor visitor) { 
  > >         // 作用：在semantic check时，用来访问其下方的结点，并得到对应信息
  > >         visitor.visit(this);
  > >     }
  > > }
  > > ```
  > >
  > > 并且所有的 `expressionNode` 都以之为基类。
  > >
  > >`statementNode` 的做法类似

- [x] 进行 Semantic Check，检查是否满足语法规则
  - [x] 实现 `scope`/`globalScope` 类，存储变量的作用范围（使用 `Hashmap` 实现）
  - [x] `Error` 类，用来 throw 错误信息，同时用 `pos` 来存储结点在 原代码 对应的位置。
  - [x] 自行实现 `Type`/`ClassType`/`FuncType`，用来存储 ASTNode 中的信息，进行比较判断
  - [x] 实现一个 `SymbolCollector`，先对 AST 进行遍历，把声明过的 class 都加入 `globalScope `  中。同时也要加入内置类型：int/void/bool/string(作为class存储)，内置函数：print/toString/size...
  - 需要判断的地方（详细语法规则请参考 guide 中的 MxRules.md）
    - 判断变量是否重名/有定义过
    - 赋值表达式是否符合变量类型（对一系列`expressionNode`的运算做判断）
    - 函数的返回值 (return) 是否符合变量类型。`main` 函数可以没有返回值，默认返回值为 `0`，否则只能为int。
    - 函数/Lambda表达式 的嵌套问题，每进入一次就是一个新的scope
    - ​

### Codegen



### Optimization
