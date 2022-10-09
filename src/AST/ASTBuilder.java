package AST;

import AST.*;
import Parser.MxParser;
import Parser.MxBaseVisitor;
import Utility.position;

import java.util.ArrayList;

public class ASTBuilder extends MxBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        RootNode root = new RootNode(new position(ctx));
        ctx.declarationseq().declaration().forEach(it -> {

        });
        return root;
    }

    @Override
    public ASTNode visitArraySpecifier(MxParser.ArraySpecifierContext ctx) {
        arraySpecifierNode node = new arraySpecifierNode(new position(ctx));

        if (ctx.buildInType() != null) node.type = ctx.buildInType().getText();
        else node.type = ctx.Identifier().getText();
        node.emptyBracketPair = ctx.LeftBracket().size();

        return node;
    }

    @Override
    public ASTNode visitFunctionParameterDef(MxParser.FunctionParameterDefContext ctx) {
        functionParameterDefNode node = new functionParameterDefNode(new position(ctx));

        ctx.varType().forEach(it -> {
            arraySpecifierNode varType;
            if (it.arraySpecifier() != null) {
                varType = (arraySpecifierNode) visit(it);
            } else {
                // not arrayType
                varType = new arraySpecifierNode(new position(it));
                varType.type = it.getText();
            }
            node.typeList.add(varType);
        });

        ctx.Identifier().forEach(it -> {
            node.idList.add(it.getText());
        });

        return node;
    }

    @Override
    public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        literalNode node = new literalNode(new position(ctx));
        if (ctx.IntegerLiteral() != null) {
            node.isInt = true;
            node.content = ctx.IntegerLiteral().getText();
        } else if (ctx.StringLiteral() != null) {
            node.isString = true;
            node.content = ctx.StringLiteral().getText();
        } else if (ctx.Null() != null) {
            node.isNull = true;
            node.content = "null";
        } else {
            node.isBool = true;
            if (ctx.True() != null) node.content = "true";
            else node.content = "false";
        }

        return node;
    }

    @Override
    public ASTNode visitIdExpression(MxParser.IdExpressionContext ctx) {
        idExpressionNode node = new idExpressionNode(new position(ctx));
        node.content = ctx.Identifier().getText();
        return node;
    }

    @Override
    public ASTNode visitLambdaExpression(MxParser.LambdaExpressionContext ctx) {
        lambdaExpressionNode node = new lambdaExpressionNode(new position(ctx));

        if (ctx.lambdaDeclarator() != null) {

        }
        return node;
    }

    @Override
    public ASTNode visitPrimaryExpression(MxParser.PrimaryExpressionContext ctx) {
        primaryExpressionNode node = new primaryExpressionNode(new position(ctx));

        if (ctx.literal() != null) {
            node.isLiteral = true;
            node.expr = (expressionNode) visit(ctx.literal());
        }
        else if (ctx.This() != null) {
            node.isThis = true;
        }
        else if (ctx.LeftParen() != null) {
            node.isExpr = true;
            node.expr = (expressionNode) visit(ctx.expression());
        }
        else if (ctx.idExpression() != null) {
            node.isIdExpr = true;
            node.expr = (expressionNode) visit(ctx.idExpression());
        } else {
            node.isLambda = true;
            node.expr = (expressionNode) visit(ctx.lambdaExpression());
        }

        return node;
    }

    @Override
    public ASTNode visitExpression(MxParser.ExpressionContext ctx) {
        expressionNode node = new expressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.assignmentExpression().forEach(it -> {
            node.exprList.add( (assignExpressionNode) visit(it));
        });

        return node;
    }

    @Override
    public ASTNode visitAssignmentExpression(MxParser.AssignmentExpressionContext ctx) {
        assignExpressionNode node = new assignExpressionNode(new position(ctx));

        node.logicExpr = (logicOrExpressionNode) visit(ctx.logicalOrExpression());
        if (ctx.Assign() != null) {
            node.exprList = new ArrayList<>();
            node.exprList.add( (expressionNode) visit(ctx.assignmentExpression()));
            // if the return type is assignNode, it'll try to read orExpression that has already been read before, wrong!
        }
        return node;
    }

    @Override
    public ASTNode visitLogicalOrExpression(MxParser.LogicalOrExpressionContext ctx) {
        logicOrExpressionNode node = new logicOrExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.logicalAndExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
            // if there is andExpr,
            // todo : check whether 'IF' is correct
        });

        return node;
    }

    @Override
    public ASTNode visitLogicalAndExpression(MxParser.LogicalAndExpressionContext ctx) {
        logicAndExpressionNode node = new logicAndExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.inclusiveOrExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
        });
        return node;
    }

    @Override
    public ASTNode visitInclusiveOrExpression(MxParser.InclusiveOrExpressionContext ctx) {
        inclusiveOrExpressionNode node = new inclusiveOrExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.exclusiveOrExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
        });
        return node;
    }

    @Override
    public ASTNode visitExclusiveOrExpression(MxParser.ExclusiveOrExpressionContext ctx) {
        exclusiveOrExpressionNode node = new exclusiveOrExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.andExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
        });
        return node;
    }

    @Override
    public ASTNode visitAndExpression(MxParser.AndExpressionContext ctx) {
        andExpressionNode node = new andExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.equalityExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
        });
        return node;
    }

    @Override
    public ASTNode visitEqualityExpression(MxParser.EqualityExpressionContext ctx) {
        equalityExpressionNode node = new equalityExpressionNode(new position(ctx));

        node.exprList = new ArrayList<>();
        ctx.relationalExpression().forEach(it -> {
            node.exprList.add((expressionNode) visit(it));
        });

        ctx.

        return node;
    }


    @Override
    public ASTNode visitFunctionDefinition(MxParser.FunctionDefinitionContext ctx) {
        functionDefNode node = new functionDefNode(new position(ctx));

        return node;
    }


    @Override
    public ASTNode visitDeclarationseq(MxParser.DeclarationseqContext ctx) {
        declarationNode node = new declarationNode(new position(ctx));

        return node;
    }

    @Override
    public ASTNode visitDeclaration(MxParser.DeclarationContext ctx) {
        declarationNode node = new declarationNode(new position(ctx));

        if (ctx.declarationStatement() != null) {
            node.type = ctx.declarationStatement().getText();
        } else {
            node.type = ctx.functionDefinition().getText();
        }

        return node;
    }

}
