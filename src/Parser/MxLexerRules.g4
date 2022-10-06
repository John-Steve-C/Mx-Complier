lexer grammar MxLexerRules;

//----------------------------------keywords
Void : 'void';
Bool : 'bool';
Int : 'int';
String : 'string';
New : 'new';
Class : 'class';
Null : 'null';
True : 'true';
False : 'false';
This : 'this';
If : 'if';
Else : 'else';
For : 'for';
While : 'while';
Break : 'break';
Continue : 'continue';
Return : 'return';

//----------------------------------opt
Star : '*';
Div : '/';
Mod : '%';
Plus : '+';
Minus : '-';

Caret : '^';
And : '&';
Or : '|';
Tilde : '~';
Not : '!';
LeftShift : '<<';
RightShift : '>>';

Assign : '=';
Less : '<';
Greater : '>';
Equal : '==';
NotEqual : '!=';
LessEqual : '<=';
GreaterEqual : '>=';

AndAnd : '&&';
OrOr : '||';
PlusPlus : '++';
MinusMinus : '--';

//------------------------------special symbols
LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';
Comma : ',';
Semi : ';';
Dot : '.';

//fragment 类似于 private
fragment Digit : [0-9];
fragment EscapeSequence : '\\n' | '\\"' | '\\\\'; // 表示转义字符集，\\ -> \
fragment Schar : ~["\n\r\\] | EscapeSequence;

// 每个类型对应的内容
IntegerLiteral : [1-9] Digit*
               | '0';
StringLiteral : '"' Schar* '"';
PointerLiteral : Null;
BoolLiteral : True | False;

Identifier : [A-Za-z] [A-Za-z0-9_]*;    //标识符

Whitespace : [ \t]+ -> skip;      //skip表示匹配后丢弃
Newline : ('\r' '\n'? | '\n') -> skip;
BlockComment : '/*' .*? '*/' -> skip; //*后面的?表示非贪婪匹配(尽量短)
LineComment : '//' ~ [\r\n]* -> skip;
