// Generated from D:/Coding/Mx_Compiler/src/Parser\Mx.g4 by ANTLR 4.10.1
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#declarationseq}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationseq(MxParser.DeclarationseqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#declarationseq}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationseq(MxParser.DeclarationseqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#buildInType}.
	 * @param ctx the parse tree
	 */
	void enterBuildInType(MxParser.BuildInTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#buildInType}.
	 * @param ctx the parse tree
	 */
	void exitBuildInType(MxParser.BuildInTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#arraySpecifier}.
	 * @param ctx the parse tree
	 */
	void enterArraySpecifier(MxParser.ArraySpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#arraySpecifier}.
	 * @param ctx the parse tree
	 */
	void exitArraySpecifier(MxParser.ArraySpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterVarType(MxParser.VarTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitVarType(MxParser.VarTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MxParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MxParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(MxParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(MxParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 */
	void enterFunctionParameterDef(MxParser.FunctionParameterDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 */
	void exitFunctionParameterDef(MxParser.FunctionParameterDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationStatement(MxParser.DeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationStatement(MxParser.DeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#initDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void enterInitDeclaratorList(MxParser.InitDeclaratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#initDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void exitInitDeclaratorList(MxParser.InitDeclaratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#declarator}.
	 * @param ctx the parse tree
	 */
	void enterDeclarator(MxParser.DeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#declarator}.
	 * @param ctx the parse tree
	 */
	void exitDeclarator(MxParser.DeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MxParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MxParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statementSeq}.
	 * @param ctx the parse tree
	 */
	void enterStatementSeq(MxParser.StatementSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statementSeq}.
	 * @param ctx the parse tree
	 */
	void exitStatementSeq(MxParser.StatementSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(MxParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(MxParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(MxParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(MxParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement(MxParser.SelectionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement(MxParser.SelectionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement(MxParser.IterationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement(MxParser.IterationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement(MxParser.JumpStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement(MxParser.JumpStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(MxParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(MxParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#forInitStatement}.
	 * @param ctx the parse tree
	 */
	void enterForInitStatement(MxParser.ForInitStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#forInitStatement}.
	 * @param ctx the parse tree
	 */
	void exitForInitStatement(MxParser.ForInitStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(MxParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(MxParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#idExpression}.
	 * @param ctx the parse tree
	 */
	void enterIdExpression(MxParser.IdExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#idExpression}.
	 * @param ctx the parse tree
	 */
	void exitIdExpression(MxParser.IdExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpression(MxParser.LambdaExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpression(MxParser.LambdaExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#lambdaIntroducer}.
	 * @param ctx the parse tree
	 */
	void enterLambdaIntroducer(MxParser.LambdaIntroducerContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#lambdaIntroducer}.
	 * @param ctx the parse tree
	 */
	void exitLambdaIntroducer(MxParser.LambdaIntroducerContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#lambdaDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterLambdaDeclarator(MxParser.LambdaDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#lambdaDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitLambdaDeclarator(MxParser.LambdaDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression(MxParser.PostfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression(MxParser.PostfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(MxParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(MxParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(MxParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(MxParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#newExpression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#newExpression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#newArrayType}.
	 * @param ctx the parse tree
	 */
	void enterNewArrayType(MxParser.NewArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#newArrayType}.
	 * @param ctx the parse tree
	 */
	void exitNewArrayType(MxParser.NewArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(MxParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(MxParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(MxParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(MxParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression(MxParser.ShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression(MxParser.ShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(MxParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(MxParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(MxParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(MxParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(MxParser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(MxParser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterExclusiveOrExpression(MxParser.ExclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitExclusiveOrExpression(MxParser.ExclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterInclusiveOrExpression(MxParser.InclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitInclusiveOrExpression(MxParser.InclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression(MxParser.LogicalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression(MxParser.LogicalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression(MxParser.LogicalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression(MxParser.LogicalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(MxParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(MxParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MxParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MxParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterClassSpecifier(MxParser.ClassSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitClassSpecifier(MxParser.ClassSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classHead}.
	 * @param ctx the parse tree
	 */
	void enterClassHead(MxParser.ClassHeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classHead}.
	 * @param ctx the parse tree
	 */
	void exitClassHead(MxParser.ClassHeadContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(MxParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(MxParser.MemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#constructFunctionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterConstructFunctionDefinition(MxParser.ConstructFunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#constructFunctionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitConstructFunctionDefinition(MxParser.ConstructFunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#theOperator}.
	 * @param ctx the parse tree
	 */
	void enterTheOperator(MxParser.TheOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#theOperator}.
	 * @param ctx the parse tree
	 */
	void exitTheOperator(MxParser.TheOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(MxParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(MxParser.LiteralContext ctx);
}