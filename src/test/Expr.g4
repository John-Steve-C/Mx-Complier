grammar Expr;
import CommonLexerRules;

prog : stat+ ;
stat : expr NEWLINE
     | ID '=' expr NEWLINE;
expr : expr ('*' | '/') expr
     | expr ('+' | '-') expr
     | INT
     | ID
     | '(' expr ')';
