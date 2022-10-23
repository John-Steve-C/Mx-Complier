// Generated from D:/Coding/Mx_Compiler/src/Parser\Mx.g4 by ANTLR 4.10.1
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MxParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#declarationseq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationseq(MxParser.DeclarationseqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#buildInType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBuildInType(MxParser.BuildInTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#arraySpecifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArraySpecifier(MxParser.ArraySpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarType(MxParser.VarTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(MxParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#functionDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefinition(MxParser.FunctionDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameterDef(MxParser.FunctionParameterDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#declarationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationStatement(MxParser.DeclarationStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#initDeclaratorList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitDeclaratorList(MxParser.InitDeclaratorListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#declarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarator(MxParser.DeclaratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MxParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#statementSeq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementSeq(MxParser.StatementSeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#compoundStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundStatement(MxParser.CompoundStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(MxParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#selectionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectionStatement(MxParser.SelectionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterationStatement(MxParser.IterationStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#jumpStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJumpStatement(MxParser.JumpStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(MxParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#forInitStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInitStatement(MxParser.ForInitStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpression(MxParser.PrimaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#idExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExpression(MxParser.IdExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#lambdaExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaExpression(MxParser.LambdaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#lambdaIntroducer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaIntroducer(MxParser.LambdaIntroducerContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#lambdaDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaDeclarator(MxParser.LambdaDeclaratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#postfixExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExpression(MxParser.PostfixExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(MxParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(MxParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#newExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#newArrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayType(MxParser.NewArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(MxParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(MxParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#shiftExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShiftExpression(MxParser.ShiftExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(MxParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(MxParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#andExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpression(MxParser.AndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExclusiveOrExpression(MxParser.ExclusiveOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclusiveOrExpression(MxParser.InclusiveOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpression(MxParser.LogicalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpression(MxParser.LogicalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#assignmentExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentExpression(MxParser.AssignmentExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MxParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#classSpecifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassSpecifier(MxParser.ClassSpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#classHead}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassHead(MxParser.ClassHeadContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(MxParser.MemberDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#constructFunctionDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructFunctionDefinition(MxParser.ConstructFunctionDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#theEqualOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheEqualOp(MxParser.TheEqualOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#theCmpOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheCmpOp(MxParser.TheCmpOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#theShiftOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheShiftOp(MxParser.TheShiftOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#thePlusMinusOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThePlusMinusOp(MxParser.ThePlusMinusOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#theStarDivModOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheStarDivModOp(MxParser.TheStarDivModOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#theOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheOperator(MxParser.TheOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(MxParser.LiteralContext ctx);
}