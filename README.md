## Mx* 编译器

实现语言：Java

分为三部分：

- Semantic
- Codegen
- Optimization

### Semantic

利用 antlr 实现语义分析

更具体地说，

1. 利用 antlr 把读入的字符串分解为一个个 token
2. 然后将其还原为 AST（语法树）

首先，你需要了解 antlr 的[基本语法](https://blog.csdn.net/pourtheworld/article/details/108304505?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522163324419316780255290255%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=163324419316780255290255&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v31_ecpm-2-108304505.first_rank_v2_pc_rank_v29&utm_term=g4%E5%9F%BA%E6%9C%AC%E8%AF%AD%E6%B3%95&spm=1018.2226.3001.4187) 以及 [正则表达式的基本语法](https://www.runoob.com/regexp/regexp-syntax.html)

> 权威指南比较复杂，可以适当选择阅读

- [ ] 完成 `Mx.g4` 文件
- [ ] 补全 `Visitor.java`

### Codegen

### Optimization
