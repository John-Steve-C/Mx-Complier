package AST;

import Parser.MxParser;
import Parser.MxBaseVisitor;
import Utility.GlobalScope;
import Utility.Position;

import java.util.ArrayList;

public class ASTBuilder extends MxBaseVisitor<ASTNode> {

    public GlobalScope globalScope;

    public ASTBuilder(GlobalScope gScope) {
        globalScope = gScope;
    }

    @Override
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        RootNode root = new RootNode(new Position(ctx));

        root.decList = new ArrayList<>();
        ctx.declarationseq().declaration().forEach(it -> root.decList.add((declarationNode) visit(it)));
        return root;
    }

    @Override
    public ASTNode visitArraySpecifier(MxParser.ArraySpecifierContext ctx) {
        arraySpecifierNode node = new arraySpecifierNode(new Position(ctx));

        if (ctx.buildInType() != null) node.type = ctx.buildInType().getText();
        else node.type = ctx.Identifier().getText();
        node.emptyBracketPair = ctx.LeftBracket().size();

        return node;
    }

    @Override
    public ASTNode visitClassSpecifier(MxParser.ClassSpecifierContext ctx) {
        classSpecifierNode node = new classSpecifierNode(new Position(ctx));

        node.name = ctx.classHead().Identifier().getText();
        if (ctx.memberDeclaration() != null) {
            node.declareList = new ArrayList<>();
            ctx.memberDeclaration().forEach(it -> {
                if (it.constructFunctionDefinition() != null) {
                    node.constructFunc = (functionDefNode) visit(it.constructFunctionDefinition());
                } else if (it.Semi() == null) {
                    declarationNode dec = new declarationNode(new Position(it));
                    if (it.declarationStatement() != null) {
                        dec.isDeclareStmt = true;
                        dec.declStmt = (declarationStatementNode) visit(it.declarationStatement());
                    } else {
                        dec.isFuncDef = true;
                        dec.funcDef = (functionDefNode) visit(it.functionDefinition());
                    }
                    node.declareList.add(dec);
                }
            });
        }

        return node;
    }

    @Override
    public ASTNode visitFunctionParameterDef(MxParser.FunctionParameterDefContext ctx) {
        functionParameterDefNode node = new functionParameterDefNode(new Position(ctx));

        ctx.varType().forEach(it -> {
            arraySpecifierNode varType;
            if (it.arraySpecifier() != null) {
                varType = (arraySpecifierNode) visit(it);
            } else {
                // not arrayType
                varType = new arraySpecifierNode(new Position(it));
                varType.type = it.getText();
            }
            node.typeList.add(varType);
        });

        ctx.Identifier().forEach(it -> node.idList.add(it.getText()));

        return node;
    }

    @Override
    public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        literalNode node = new literalNode(new Position(ctx));
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

    //----------------------------------------expression

    @Override
    public ASTNode visitIdExpression(MxParser.IdExpressionContext ctx) {
        idExpressionNode node = new idExpressionNode(new Position(ctx));
        node.content = ctx.Identifier().getText();
        return node;
    }

    @Override
    public ASTNode visitLambdaExpression(MxParser.LambdaExpressionContext ctx) {
        lambdaExpressionNode node = new lambdaExpressionNode(new Position(ctx));

        if (ctx.lambdaDeclarator() != null) {
            if (ctx.lambdaDeclarator().functionParameterDef() != null) {
                node.funcPar = (functionParameterDefNode) visit(ctx.lambdaDeclarator().functionParameterDef());
            }
        }
        node.compoundStmt = (compoundStatementNode) visit(ctx.compoundStatement());
        return node;
    }

    @Override
    public ASTNode visitPrimaryExpression(MxParser.PrimaryExpressionContext ctx) {
        primaryExpressionNode node = new primaryExpressionNode(new Position(ctx));

        if (ctx.literal() != null) {
            node.isLiteral = true;
            node.expr = (expressionNode) visit(ctx.literal());
        } else if (ctx.This() != null) {
            node.isThis = true;
        } else if (ctx.LeftParen() != null) {
            node.isExpr = true;
            node.expr = (expressionNode) visit(ctx.expression());
        } else if (ctx.idExpression() != null) {
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
        expressionNode node = new expressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.assignmentExpression().forEach(it -> node.exprList.add((assignExpressionNode) visit(it)));

        return node;
    }

    @Override
    public ASTNode visitAssignmentExpression(MxParser.AssignmentExpressionContext ctx) {
        assignExpressionNode node = new assignExpressionNode(new Position(ctx));

        node.logicExpr = (logicOrExpressionNode) visit(ctx.logicalOrExpression());
        if (ctx.Assign() != null) {
            node.exprList = new ArrayList<>();
            node.exprList.add((expressionNode) visit(ctx.assignmentExpression()));
            // if the return type is assignNode, it'll try to read orExpression that has already been read before, wrong!
        }
        return node;
    }

    @Override
    public ASTNode visitLogicalOrExpression(MxParser.LogicalOrExpressionContext ctx) {
        logicOrExpressionNode node = new logicOrExpressionNode(new Position(ctx));

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
        logicAndExpressionNode node = new logicAndExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.inclusiveOrExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        return node;
    }

    @Override
    public ASTNode visitInclusiveOrExpression(MxParser.InclusiveOrExpressionContext ctx) {
        inclusiveOrExpressionNode node = new inclusiveOrExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.exclusiveOrExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        return node;
    }

    @Override
    public ASTNode visitExclusiveOrExpression(MxParser.ExclusiveOrExpressionContext ctx) {
        exclusiveOrExpressionNode node = new exclusiveOrExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.andExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        return node;
    }

    @Override
    public ASTNode visitAndExpression(MxParser.AndExpressionContext ctx) {
        andExpressionNode node = new andExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.equalityExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        return node;
    }

    @Override
    public ASTNode visitEqualityExpression(MxParser.EqualityExpressionContext ctx) {
        equalityExpressionNode node = new equalityExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.relationalExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        node.opList = new ArrayList<>();
        ctx.theEqualOp().forEach(it -> node.opList.add(it.getText()));
        return node;
    }

    @Override
    public ASTNode visitRelationalExpression(MxParser.RelationalExpressionContext ctx) {
        relationalExpressionNode node = new relationalExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.shiftExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        node.opList = new ArrayList<>();
        ctx.theCmpOp().forEach(it -> node.opList.add(it.getText()));
        return node;
    }

    @Override
    public ASTNode visitShiftExpression(MxParser.ShiftExpressionContext ctx) {
        shiftExpressionNode node = new shiftExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.additiveExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        node.opList = new ArrayList<>();
        ctx.theShiftOp().forEach(it -> node.opList.add(it.getText()));
        return node;
    }

    @Override
    public ASTNode visitAdditiveExpression(MxParser.AdditiveExpressionContext ctx) {
        additiveExpressionNode node = new additiveExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.multiplicativeExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        node.opList = new ArrayList<>();
        ctx.thePlusMinusOp().forEach(it -> node.opList.add(it.getText()));
        return node;
    }

    @Override
    public ASTNode visitMultiplicativeExpression(MxParser.MultiplicativeExpressionContext ctx) {
        multiplicativeExpressionNode node = new multiplicativeExpressionNode(new Position(ctx));

        node.exprList = new ArrayList<>();
        ctx.unaryExpression().forEach(it -> node.exprList.add((expressionNode) visit(it)));
        node.opList = new ArrayList<>();
        ctx.theStarDivModOp().forEach(it -> node.opList.add(it.getText()));
        return node;
    }

    @Override
    public ASTNode visitNewArrayType(MxParser.NewArrayTypeContext ctx) {
        newArrayTypeNode node = new newArrayTypeNode(new Position(ctx));

        if (ctx.buildInType() != null) node.type = ctx.buildInType().getText();
        else node.type = ctx.Identifier().getText();
        node.emptyBracketPair = ctx.LeftBracket().size();
        node.length = new ArrayList<>();
        ctx.expression().forEach(it -> node.length.add(it.getText()));

        return node;
    }

    @Override
    public ASTNode visitNewExpression(MxParser.NewExpressionContext ctx) {
        newExpressionNode node = new newExpressionNode(new Position(ctx));

        if (ctx.buildInType() != null) node.type = ctx.buildInType().getText();
        else if (ctx.Identifier() != null) node.type = ctx.Identifier().getText();
        else node.newArray = (newArrayTypeNode) visit(ctx.newArrayType());
        return node;
    }

    @Override
    public ASTNode visitPostfixExpression(MxParser.PostfixExpressionContext ctx) {
        postfixExpressionNode node = new postfixExpressionNode(new Position(ctx));

        if (ctx.primaryExpression() != null) {
            node.primaryExpr = (primaryExpressionNode) visit(ctx.primaryExpression());
        } else {
            if (ctx.LeftBracket() != null) {
                node.isBracket = true;
                node.exprList = new ArrayList<>();
                node.exprList.add((expressionNode) visit(ctx.expression()));
            } else if (ctx.LeftParen() != null) {
                node.isParen = true;
                if (ctx.expression() != null) {
                    node.exprList = new ArrayList<>();
                    node.exprList.add((expressionNode) visit(ctx.expression()));
                }
            } else if (ctx.Dot() != null) {
                node.isDot = true;
                node.exprList = new ArrayList<>();
                node.exprList.add((expressionNode) visit(ctx.idExpression()));
            } else if (ctx.PlusPlus() != null) {
                node.isPlusPlus = true;
            } else {
                node.isMinusMinus = true;
            }
            node.postfixExpr = (postfixExpressionNode) visit(ctx.postfixExpression());
        }
        return node;
    }

    @Override
    public ASTNode visitUnaryExpression(MxParser.UnaryExpressionContext ctx) {
        unaryExpressionNode node = new unaryExpressionNode(new Position(ctx));

        if (ctx.postfixExpression() != null) {
            node.postfixExpr = (postfixExpressionNode) visit(ctx.postfixExpression());
        } else if (ctx.newExpression() != null) {
            node.newExpr = (newExpressionNode) visit(ctx.newExpression());
        } else {
            if (ctx.PlusPlus() != null) node.op = "++";
            else if (ctx.MinusMinus() != null) node.op = "--";
            else node.op = ctx.unaryOperator().getText();

            node.unaryExpr = (unaryExpressionNode) visit(ctx.unaryExpression());
        }
        return node;
    }

    //------------------------------------------------------statement

    @Override
    public ASTNode visitStatement(MxParser.StatementContext ctx) {

        if (ctx.declarationStatement() != null) {
            return visit(ctx.declarationStatement());
        } else if (ctx.expressionStatement() != null) {
            return visit(ctx.expressionStatement());
        } else if (ctx.compoundStatement() != null) {
            return visit(ctx.compoundStatement());
        } else if (ctx.selectionStatement() != null) {
            return visit(ctx.selectionStatement());
        } else if (ctx.iterationStatement() != null) {
            return visit(ctx.iterationStatement());
        } else if (ctx.jumpStatement() != null) {
            return visit(ctx.jumpStatement());
        } else {
            statementNode node = new statementNode(new Position(ctx));
            node.isEmpty = true;
            return node;
        }
    }

    @Override
    public ASTNode visitCompoundStatement(MxParser.CompoundStatementContext ctx) {
        compoundStatementNode node = new compoundStatementNode(new Position(ctx));

        if (ctx.statementSeq() != null) {
            node.stmtList = new ArrayList<>();
            ctx.statementSeq().statement().forEach(it -> node.stmtList.add((statementNode) visit(it)));
        }
        return node;
    }

    @Override
    public ASTNode visitExpressionStatement(MxParser.ExpressionStatementContext ctx) {
        expressionStatementNode node = new expressionStatementNode(new Position(ctx));

        node.expr = (expressionNode) visit(ctx.expression());
        return node;
    }

    @Override
    public ASTNode visitSelectionStatement(MxParser.SelectionStatementContext ctx) {
        selectionStatementNode node = new selectionStatementNode(new Position(ctx));

        node.cond = (statementNode) visit(ctx.condition());
        node.trueStmt = (statementNode) visit(ctx.trueStatement);
        if (ctx.Else() != null) node.falseStmt = (statementNode) visit(ctx.falseStatement);
        return node;
    }

    @Override
    public ASTNode visitIterationStatement(MxParser.IterationStatementContext ctx) {
        iterationStatementNode node = new iterationStatementNode(new Position(ctx));

        node.todoStmt = (statementNode) visit(ctx.statement());
        if (ctx.While() != null) {
            node.isWhile = true;
            node.cond = (expressionNode) visit(ctx.condition());
        } else {
            node.isFor = true;
            if (ctx.condition() != null) node.cond = (expressionNode) visit(ctx.condition());
            if (ctx.expression() != null) node.stepExpr = (expressionNode) visit(ctx.expression());
            node.init = (statementNode) visit(ctx.forInitStatement());
        }
        return node;
    }

    @Override
    public ASTNode visitForInitStatement(MxParser.ForInitStatementContext ctx) {
        if (ctx.expressionStatement() != null) {
            return visit(ctx.expressionStatement());
        } else if (ctx.declarationStatement() != null) {
            return visit(ctx.declarationStatement());
        } else {
            statementNode node = new statementNode(new Position(ctx));
            node.isEmpty = true;
            return node;
        }
    }

    @Override
    public ASTNode visitJumpStatement(MxParser.JumpStatementContext ctx) {
        jumpStatementNode node = new jumpStatementNode(new Position(ctx));

        if (ctx.Return() != null) {
            node.isReturn = true;
            if (ctx.expression() != null) {
                node.retExpr = (expressionNode) visit(ctx.expression());
            }
        } else if (ctx.Break() != null) {
            node.isBreak = true;
        } else {
            node.isContinue = true;
        }
        return node;
    }

    //---------------------------------------------declaration

    @Override
    public ASTNode visitDeclarationStatement(MxParser.DeclarationStatementContext ctx) {
        declarationStatementNode node = new declarationStatementNode(new Position(ctx));

        if (ctx.initDeclaratorList() == null) {
            // it must be a class definition
            // or it's illegal
            if (ctx.varType().classSpecifier() != null) {
                node.isClassDef = true;
                node.struct = (classSpecifierNode) visit(ctx.varType().classSpecifier());
            }
        } else {
            if (ctx.varType().arraySpecifier() != null) {
                node.arraySpec = (arraySpecifierNode) visit(ctx.varType().arraySpecifier());
            } else {
                if (ctx.varType().classSpecifier() != null) node.fail = true;
                node.arraySpec = new arraySpecifierNode(node.pos);
                node.arraySpec.type = ctx.varType().getText();
            }
            node.initList = new ArrayList<>();
            ctx.initDeclaratorList().declarator().forEach(it -> node.initList.add((declaratorNode) visit(it)));
        }

        return node;
    }

    @Override
    public ASTNode visitDeclarator(MxParser.DeclaratorContext ctx) {
        declaratorNode node = new declaratorNode(new Position(ctx));

        node.id = ctx.Identifier().getText();
        if (ctx.Assign() != null) {
            node.expr = (assignExpressionNode) visit(ctx.assignmentExpression());
        }
        return node;
    }

    @Override
    public ASTNode visitDeclaration(MxParser.DeclarationContext ctx) {
        declarationNode node = new declarationNode(new Position(ctx));

        if (ctx.declarationStatement() != null) {
            node.isDeclareStmt = true;
            node.declStmt = (declarationStatementNode) visit(ctx.declarationStatement());
        } else {
            node.isFuncDef = true;
            node.funcDef = (functionDefNode) visit(ctx.functionDefinition());
        }

        return node;
    }

    @Override
    public ASTNode visitFunctionDefinition(MxParser.FunctionDefinitionContext ctx) {
        functionDefNode node = new functionDefNode(new Position(ctx));
        node.funcName = ctx.Identifier().getText();

        if (ctx.returnType().arraySpecifier() != null) {
            node.retType = (arraySpecifierNode) visit(ctx.returnType().arraySpecifier());
        } else {
            node.retType = new arraySpecifierNode(new Position(ctx));
            node.retType.type = ctx.returnType().getText();
        }
        if (ctx.functionParameterDef() != null)
            node.funcPar = (functionParameterDefNode) visit(ctx.functionParameterDef());
        node.compoundStmt = (compoundStatementNode) visit(ctx.compoundStatement());
        return node;
    }
}
