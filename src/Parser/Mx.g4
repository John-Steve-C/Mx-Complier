grammar Mx;
import MxLexerRules;

//------------------------------------Parser(参考自 Cpp14Parser.g4)
//------------------------------------Main Body
program : declarationseq? EOF;

declarationseq : declaration+;

//--------------------------------type of varieties
buildInType : String | Int | Bool;

arraySpecifier : (buildInType | Identifier) (LeftBracket RightBracket)+;    //与c++不同，要考虑多维数组情况

returnType : Void | buildInType | arraySpecifier | Identifier;

varType : buildInType | arraySpecifier | Identifier | classSpecifier;

//------------------------------declaration

declaration : declarationStatement | functionDefinition;

functionDefinition : returnType Identifier LeftParen functionParameterDef? RightParen compoundStatement;

functionParameterDef : varType Identifier (Comma varType Identifier)*;  //函数参数

declarationStatement : varType initDeclaratorList? Semi;

initDeclaratorList : declarator (Comma declarator)*;    //初始的变量声明表

declarator : Identifier (Assign assignmentExpression)?; //变量声明单元

//--------------------------------statement

statement : declarationStatement
		 | expressionStatement
		 | compoundStatement
		 | selectionStatement
		 | iterationStatement
		 | jumpStatement
		 | Semi;

statementSeq : statement+;

compoundStatement : LeftBrace statementSeq? RightBrace;

expressionStatement : expression Semi;

// = 表示起别名
selectionStatement : If LeftParen condition RightParen trueStatement=statement (Else falseStatement=statement)?;

iterationStatement :
    While LeftParen condition RightParen statement
    | For LeftParen forInitStatement condition? Semi expression? RightParen statement;

jumpStatement :
    (
        Return expression?
        | Break
        | Continue
    ) Semi;

condition : expression;     //起别名，便于表示

forInitStatement :          // for循环起始条件
    expressionStatement
    | declarationStatement
    | Semi;

//---------------------------------------expression
//表达式的最底层分类
primaryExpression :
	literal
	| This
	| LeftParen expression RightParen
	| idExpression
	| lambdaExpression;

idExpression : Identifier;

// lambda 表达式，比c++精简
lambdaExpression : lambdaIntroducer lambdaDeclarator '->'  compoundStatement;

lambdaIntroducer : LeftBracket And RightBracket;

lambdaDeclarator :
    (LeftParen functionParameterDef? RightParen)?;

postfixExpression :     //在后面表意的 ‘后缀’表达式
    primaryExpression
    | postfixExpression LeftParen expression? RightParen
    | postfixExpression LeftBracket expression RightBracket
    | postfixExpression Dot idExpression
    | postfixExpression (PlusPlus | MinusMinus);

unaryExpression :        // 一元表达式
    postfixExpression
    | (PlusPlus | MinusMinus | unaryOperator) unaryExpression
    | newExpression;

unaryOperator : Or | Star | And | Plus | Tilde | Minus | Not;

newExpression :
    New
    (
        buildInType
        | Identifier
        | newArrayType              // 多维数组嵌套
    ) (LeftParen RightParen)?;

newArrayType :
    (
        buildInType
        | Identifier
    ) (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)*;

//利用嵌套顺序来确定运算优先级
multiplicativeExpression :
    unaryExpression (
        (Star | Div | Mod) unaryExpression
    )*;

additiveExpression :
    multiplicativeExpression (
        (Plus | Minus) multiplicativeExpression
    )*;

shiftExpression :
    additiveExpression (
        (RightShift | LeftShift) additiveExpression
    )*;

relationalExpression :
    shiftExpression (
        (Less | Greater | LessEqual | GreaterEqual) shiftExpression
    )*;

equalityExpression :
    relationalExpression (
        (Equal | NotEqual) relationalExpression
    )*;

andExpression : equalityExpression (And equalityExpression)*;

exclusiveOrExpression : andExpression (Caret andExpression)*;

inclusiveOrExpression : exclusiveOrExpression (Or exclusiveOrExpression)*;

logicalAndExpression :
    inclusiveOrExpression (AndAnd inclusiveOrExpression)*;

logicalOrExpression :
    logicalAndExpression (OrOr logicalAndExpression)*;

assignmentExpression :
    logicalOrExpression (Assign assignmentExpression)?;

// 考虑优先级的嵌套表达式
expression : assignmentExpression (Comma assignmentExpression)*;

//--------------------------------class

classSpecifier:
    classHead LeftBrace memberDeclaration* RightBrace;

classHead:
    Class Identifier;

memberDeclaration:
    functionDefinition
    | declarationStatement
    | constructFunctionDefinition       //构造函数
    | Semi;

constructFunctionDefinition: Identifier LeftParen RightParen compoundStatement;

// 一些 Lexer 的集合
theOperator :
    New (LeftBracket RightBracket)?
    | Plus
    | Minus
    | Star
    | Div
    | Mod
    | Caret
    | And
    | Or
    | Tilde
    | Not
    | Assign
    | Greater
    | Less
    | GreaterEqual
    | LessEqual
    | RightShift
    | LeftShift
    | Equal
    | NotEqual
    | AndAnd
    | OrOr
    | PlusPlus
    | MinusMinus
    | Comma
    | LeftParen RightParen
    | LeftBracket RightBracket;

literal :   //变量字素
    IntegerLiteral
    | StringLiteral
    | ( True | False)
    | Null;
