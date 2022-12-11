package IR;

import AST.*;
import AST.Node.*;
import AST.Node.Expression.*;
import AST.Node.Statement.*;
import Backend.Pass;
import IR.Node.*;
import IR.Node.GlobalUnit.*;
import IR.Node.Program;
import IR.Node.Instruction.*;
import IR.TypeSystem.*;
import Utility.GlobalScope;
import Utility.Scope;
import Utility.Type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class IRBuilder implements ASTVisitor {
    // build IR from AST
    // 部分变量与 symbolCollector 类似
    public Program program;
    public Scope currentScope;
    public GlobalScope globalScope;
    public ClassType currentStruct = null;
    public block currentBlock = null, loopExitBlock = null, loopContinueBlock = null;
    public classDef stringDef = null;

    public HashMap<String, classDef> idToClsDef;    // 包括内置类和自定义class
    public HashMap<String, funcDef> idToFuncDef;

    private int initFuncCounter = 0;
    public funcDef mainFunc = null, currentFunc = null, globalInit = new funcDef(), builtInFunc = new funcDef();
    private int loopDepth = 0, iterCount = 0, selectCount = 0;

    private FuncType lastCallFunc = null;
    private String lastCallId = null;
    private register lastFuncCaller = null;
    private IRType lastFuncCallerIRType = null;
    private boolean funcSuite = false; // like semantic checker

    // some define constant & type;
    private IRType voidType = new IRType(), i1 = new IRType(1), i8 = new IRType(8), i32 = new IRType(32),
            i8Star = i8.getPtr(), stringStar = new IRType(0, 1, 0, null), i32Star = i32.getPtr();
    // todo: stringStar should be i8*?
    private constant constZero = new constant(0), constVoid = new constant(), constUnit = new constant(1), constFull = new constant(-1);

    public IRBuilder(Program p, GlobalScope gscope, HashMap<String, classDef> idToClsDef, HashMap<String, funcDef> idToFuncDef) {
        program = p;
        this.idToClsDef = idToClsDef;  // why not gscope.idToClassDef?
        this.idToFuncDef = idToFuncDef;
        currentScope = globalScope = gscope;

        voidType.isVoid = true;

        globalInit.returnType = voidType;
        globalInit.returnBlock = globalInit.rootBlock = new block(loopDepth);
        globalInit.funcName = "_GLOBAL_";

        program.builtinFunc = builtInFunc;

        builtInDeclareInit();
    }

    private void builtInDeclareInit() {
        stringStar.isString = true;
        stringDef = globalScope.getClassDef("string");
        stringStar.cDef = stringDef;
        program.push_back(stringDef);

        // add string's member function
        declare declareStringLength = new declare(i32, "_string_length");
        declareStringLength.parameter.add(stringStar);
        program.push_back(declareStringLength);
        funcDef stringLength = new funcDef("_string_length", i32, declareStringLength.parameter);
        idToFuncDef.put("_string_length", stringLength);

        declare declareStringSubString = new declare(stringStar, "_string_substring");
        declareStringSubString.parameter.add(stringStar);
        declareStringSubString.parameter.add(i32);
        declareStringSubString.parameter.add(i32);
        program.push_back(declareStringSubString);
        funcDef stringSubString = new funcDef("_string_substring", stringStar, declareStringSubString.parameter);
        idToFuncDef.put("_string_substring", stringSubString);

        declare declareStringParseInt = new declare(i32, "_string_parseInt");
        declareStringParseInt.parameter.add(stringStar);
        program.push_back(declareStringParseInt);
        funcDef stringParseInt = new funcDef("_string_parseInt", i32, declareStringParseInt.parameter);
        idToFuncDef.put("_string_parseInt", stringParseInt);

        declare declareStringOrd = new declare(i32, "_string_ord");
        declareStringOrd.parameter.add(stringStar);
        declareStringOrd.parameter.add(i32);
        program.push_back(declareStringOrd);
        funcDef stringOrd = new funcDef("_string_ord", i32, declareStringOrd.parameter);
        idToFuncDef.put("_string_ord", stringOrd);

        declare declareStringAppend = new declare(stringStar, "_string_stringAppend");
        declareStringAppend.parameter.add(stringStar);
        declareStringAppend.parameter.add(stringStar);
        program.push_back(declareStringAppend);
//        builtInStringAppend = new funcDef("_string_stringAppend", stringStar, declareStringAppend.parameter);

        declare declareStringGetStrcmp = new declare(i32, "_string_getStrcmp");
        declareStringGetStrcmp.parameter.add(stringStar);
        declareStringGetStrcmp.parameter.add(stringStar);
        program.push_back(declareStringGetStrcmp);
//        builtInStringGetStrcmp = new funcDef("_string_getStrcmp", i32, declareStringGetStrcmp.parameter);

        // add global built-in functions
        declare declareToString = new declare(stringStar, "toString");
        declareToString.parameter.add(i32);
        funcDef ToString = new funcDef("toString", stringStar, declareToString.parameter);
        idToFuncDef.put("toString", ToString);
        program.push_back(declareToString);

        declare declarePrint = new declare(voidType, "print");
        declarePrint.parameter.add(new IRType(8, 1, 0, null));
        funcDef print = new funcDef("print", voidType, declarePrint.parameter);
        idToFuncDef.put("print", print);
        program.push_back(declarePrint);

        declare declarePrintln = new declare(voidType, "println");
        declarePrintln.parameter.add(new IRType(8, 1, 0, null));
        funcDef println = new funcDef("println", voidType, declarePrintln.parameter);
        idToFuncDef.put("println", println);
        program.push_back(declarePrintln);

        declare declarePrintInt = new declare(voidType, "printInt");
        declarePrintInt.parameter.add(i32);
        funcDef printInt = new funcDef("printInt", voidType, declarePrintInt.parameter);
        idToFuncDef.put("printInt", printInt);
        program.push_back(declarePrintInt);

        declare declarePrintlnInt = new declare(voidType, "printlnInt");
        declarePrintlnInt.parameter.add(i32);
        funcDef printlnInt = new funcDef("printlnInt", voidType, declarePrintlnInt.parameter);
        idToFuncDef.put("printlnInt", printlnInt);
        program.push_back(declarePrintlnInt);

        declare declareGetInt = new declare(i32, "getInt");
        funcDef getInt = new funcDef("getInt", i32, declareGetInt.parameter);
        idToFuncDef.put("getInt", getInt);
        program.push_back(declareGetInt);

        declare declareGetString = new declare(stringStar, "getString");
        funcDef getString = new funcDef("getString", declareGetString.returnType, declareGetString.parameter);
        idToFuncDef.put("getString", getString);
        program.push_back(declareGetString);

        declare declareMyNew = new declare(new IRType(8, 1, 0, null), "myNew");
        declareMyNew.parameter.add(i32);
        funcDef myNew = new funcDef("myNew", declareMyNew.returnType, declareMyNew.parameter);
        idToFuncDef.put("myNew", myNew);
        program.push_back(declareMyNew);
    }

    @Override
    public void visit(RootNode it) {
        it.declList.forEach(dec -> dec.accept(this));
        if (initFuncCounter > 0) {  // 调用 全局初始化函数
            mainFunc.directCall.add(globalInit);
            mainFunc.rootBlock.push_front(new call(null, voidType, "_GLOBAL_", globalInit));
            globalInit.rootBlock.push_back(new ret(null, voidType));
            program.push_back(globalInit);
        }
        program.mainFunc = mainFunc;
    }

    @Override
    public void visit(declarationNode it) {
        if (it.isDeclareStmt) it.declStmt.accept(this);
        else it.funcDef.accept(this);
    }

    @Override
    public void visit(functionDefNode it) {
        iterCount = selectCount = 0;
        currentScope = new Scope(currentScope);
        // add prefix to confirm the struct of function
        String idPrefix = (currentStruct != null) ? ("_" + currentStruct.name + "_") : "";
        currentFunc = idToFuncDef.get(idPrefix + it.funcName);
        if (Objects.equals(currentFunc.funcName, "main")) mainFunc = currentFunc;
        currentFunc.rootBlock = new block(loopDepth);
        currentFunc.returnBlock = new block(loopDepth);
        currentFunc.returnBlock.jump = true;
        currentBlock = currentFunc.rootBlock;
        program.push_back(currentFunc);

        FuncType func;
        if (currentStruct != null) {
            func = currentStruct.method.get(it.funcName);
            IRType tmpType = new IRType(idToClsDef.get(currentStruct.name), 1, 0);
            register rd = new register(), rs = new register();

            // add *this pointer for struct
            currentFunc.parameterRegs.add(rs);
            currentScope.defineVar("this", currentStruct, it.pos);
            currentScope.linkReg("this", rd, tmpType.getPtr());

            alloca Alloca = new alloca(rd, tmpType);
            currentFunc.push_back(Alloca);
            currentBlock.push_back(new store(rs, rd, tmpType));
        } else func = globalScope.queryFuncType(it.funcName, it.pos);

        // return
        if (func.returnType.kind != Type.Types.VOID_TYPE && func.returnType.kind != Type.Types.NULL) {
            currentFunc.retReg = new register();
            entity en;
            if (currentFunc.returnType.ptrNum > 0) en = constVoid;
            else en = constZero;
            alloca Alloca = new alloca(currentFunc.retReg, currentFunc.returnType);
            currentBlock.push_back(new store(en, currentFunc.retReg, currentFunc.returnType));
            currentFunc.push_back(Alloca);
        }

        // parameters
        if (it.funcPar != null) {
            int loopLen = it.funcPar.idList.size();
            for (int i = 0; i < loopLen; ++i) {
                String tmpId = it.funcPar.idList.get(i);
                Type t = func.parameter.get(i);
                register rd = new register(), rs = new register();
                IRType tmpIrType;
                if (t.kind == Type.Types.CLASS_TYPE) {
                    tmpIrType = new IRType(idToClsDef.get(t.name), t.dimension + 1, 0);
                    if (Objects.equals(t.name, "string") && t.dimension == 0) tmpIrType.isString = true;
                } else tmpIrType = new IRType(t);
                currentFunc.parameterRegs.add(rs);
                currentScope.defineVar(tmpId, t, it.funcPar.pos);
                currentScope.linkReg(tmpId, rd, tmpIrType.getPtr());
                alloca Alloca = new alloca(rd, tmpIrType);
                currentFunc.push_back(Alloca);
                currentBlock.push_back(new store(rs, rd, tmpIrType));
            }
        }

        funcSuite = true;
        it.compoundStmt.accept(this);
        if (currentBlock.tail == null) {
            if (!(func.returnType.kind == Type.Types.VOID_TYPE || func.returnType.kind == Type.Types.NULL)) {
                entity en;
                if (currentFunc.returnType.ptrNum > 0) en = constVoid;
                else en = constZero;
                currentBlock.push_back(new store(en, currentFunc.retReg, currentFunc.returnType));
            }
            currentBlock.push_back(new br(null, currentFunc.returnBlock, null));
        }

        currentBlock = currentFunc.returnBlock;
        entity rd = null;
        if (currentFunc.retReg != null) {
            rd = new register();
            new load((register) rd, currentFunc.retReg, currentFunc.returnType.getPtr());
            currentBlock.push_back(new load((register) rd, currentFunc.retReg, currentFunc.returnType.getPtr()));
        }
        ret retStmt = new ret(rd, currentFunc.returnType);
        currentBlock.push_back(retStmt);
        currentScope = currentScope.parentScope;
        currentFunc = null;
    }

    @Override
    public void visit(compoundStatementNode it) {
        if (it.stmtList != null) {
            boolean flag = false;
            if (!funcSuite) currentScope = new Scope(currentScope);
            else {
                funcSuite = false;
                flag = true;
            }
            for (statementNode node : it.stmtList) {
                node.accept(this);
            }
            if (!flag) currentScope = currentScope.parentScope;
        }
    }

    @Override
    public void visit(statementNode it) {
    }

    @Override
    public void visit(selectionStatementNode it) {
        it.cond.accept(this);
        block trueBlock = new block(loopDepth), falseBlock, coverBlock = new block(loopDepth);  // coverBlock 表示返回原本的 block
        trueBlock.comment = "True block in " + currentFunc.funcName + " selectCount " + selectCount;
        coverBlock.comment = "Cover block in " + currentFunc.funcName + " selectCount " + selectCount;
        if (it.falseStmt != null) {
            falseBlock = new block(loopDepth);
            falseBlock.comment = "False block in " + currentFunc.funcName + " selectCount " + selectCount;
            if (it.cond.rd instanceof constant con) {
                // 条件固定，直接优化
                coverBlock.jump = true;
                if (con.getBoolValue()) {
                    currentBlock.push_back(new br(null, trueBlock, null));
                    trueBlock.jump = true;
                    currentBlock = trueBlock;
                    currentScope = new Scope(currentScope);
                    it.trueStmt.accept(this);
                    currentScope = currentScope.parentScope;
                } else {
                    currentBlock.push_back(new br(null, falseBlock, null));
                    falseBlock.jump = true;
                    currentBlock = falseBlock;
                    currentScope = new Scope(currentScope);
                    it.falseStmt.accept(this);
                    currentScope = currentScope.parentScope;
                }
                currentBlock.push_back(new br(null, coverBlock, null));
            } else {
                register rdCmp;
                if (it.cond.irType.intLen != 1) {
                    rdCmp = new register();
                    currentBlock.push_back(new convertOp(rdCmp, it.cond.rd, convertOp.convertType.TRUNC, i1, it.cond.irType));
                } else rdCmp = (register) it.cond.rd;
                currentBlock.push_back(new br(rdCmp, trueBlock, falseBlock));
                trueBlock.jump = falseBlock.jump = coverBlock.jump = true;

                currentBlock = trueBlock;
                currentScope = new Scope(currentScope);
                it.trueStmt.accept(this);
                currentScope = currentScope.parentScope;
                currentBlock.push_back(new br(null, coverBlock, null)); // 返回原本 block

                currentBlock = falseBlock;
                currentScope = new Scope(currentScope);
                it.falseStmt.accept(this);
                currentScope = currentScope.parentScope;
                currentBlock.push_back(new br(null, coverBlock, null));
            }
            currentBlock = coverBlock;
        } else {
            // no False Branch
            currentScope = new Scope(currentScope);
            if (it.cond.rd instanceof constant con) {
                coverBlock.jump = true;
                if (con.getBoolValue()) {
                    currentBlock.push_back(new br(null, trueBlock, null));
                    trueBlock.jump = true;
                    currentBlock = trueBlock;
                    it.trueStmt.accept(this);
                    currentBlock.push_back(new br(null, coverBlock, null));
                } else {
                    currentBlock.push_back(new br(null, coverBlock, null));
                }
            } else {
                register rdCmp;
                if (it.cond.irType.intLen != 1) {
                    rdCmp = new register();
                    currentBlock.push_back(new convertOp(rdCmp, it.cond.rd, convertOp.convertType.TRUNC, i1, it.cond.irType));
                } else rdCmp = (register) it.cond.rd;
                currentBlock.push_back(new br(rdCmp, trueBlock, coverBlock));
                trueBlock.jump = coverBlock.jump = true;
                currentBlock = trueBlock;
                it.trueStmt.accept(this);
                currentBlock.push_back(new br(null, coverBlock, null));
            }
            currentBlock = coverBlock;
            currentScope = currentScope.parentScope;
        }
        selectCount++;
    }

    @Override
    public void visit(iterationStatementNode it) {
        ++loopDepth;
        currentScope = new Scope(currentScope);
        block parentLoopExitBlock = loopExitBlock;
        block parentLoopContinueBlock = loopContinueBlock;
        
        if (it.isFor) {
            block body = new block(loopDepth), checkBlock = new block(loopDepth), exitBlock = new block(loopDepth), stepBlock = new block(loopDepth);
            body.jump = checkBlock.jump = exitBlock.jump = true;
            body.comment = "loop body " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            checkBlock.comment = "loop check block " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            exitBlock.comment = "loop exit block " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            stepBlock.comment = "loop increase block " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;

            if (it.stepExpr != null) {
                stepBlock.jump = true;
                loopContinueBlock = stepBlock;  // continue 直接进入下一次循环, 即 stepExpr
            } else loopContinueBlock = checkBlock;  // 直接 check
            loopExitBlock = exitBlock;
            if (it.init != null) it.init.accept(this);
            currentBlock.push_back(new br(null, checkBlock, null));

            currentBlock = checkBlock;
            if (it.cond != null) {
                it.cond.accept(this);
                currentBlock.push_back(new br((register) it.cond.rd, body, exitBlock));
            } else { // no condition, dead loop
                currentBlock.push_back(new br(null, body, null));
                currentBlock.successors.add(exitBlock);
            }

            currentBlock = body;
            it.todoStmt.accept(this);
            if (it.stepExpr != null) {
                currentBlock.push_back(new br(null, stepBlock, null));
                currentBlock = stepBlock;
                it.stepExpr.accept(this);
            }
            currentBlock.push_back(new br(null, checkBlock, null));
            currentBlock = exitBlock;
        } else {
            // is while
            // turn while to loop until for loop invariant
            block body = new block(loopDepth), checkBlock = new block(loopDepth), exitBlock = new block(loopDepth), preHeader = new block(loopDepth);
            loopExitBlock = exitBlock;
            loopContinueBlock = checkBlock;
            body.jump = checkBlock.jump = exitBlock.jump = preHeader.jump = true;
            currentBlock.push_back(new br(null, preHeader, null));

            currentBlock = preHeader;
            it.cond.accept(this);
            if (it.cond.rd instanceof constant con) {
                if (con.getBoolValue()) {
                    currentBlock.push_back(new br(null, body, null));
                    currentBlock.successors.add(exitBlock);
                } else currentBlock.push_back(new br(null, exitBlock, null));
            } else currentBlock.push_back(new br((register) it.cond.rd, body, exitBlock));

            currentBlock = body;
            it.todoStmt.accept(this);
            currentBlock.push_back(new br(null, checkBlock, null));

            currentBlock = checkBlock;
            it.cond.accept(this);
            if (it.cond.rd instanceof constant con) {
                if (con.getBoolValue()) {
                    currentBlock.push_back(new br(null, body, null));
                    currentBlock.successors.add(exitBlock);
                } else currentBlock.push_back(new br(null, exitBlock, null));
            } else currentBlock.push_back(new br((register) it.cond.rd, body, exitBlock));
            currentBlock = exitBlock;
        }
        loopExitBlock = parentLoopExitBlock;
        loopContinueBlock = parentLoopContinueBlock;
        currentScope = currentScope.parentScope;
        --loopDepth;
        ++iterCount;
    }

    @Override
    public void visit(jumpStatementNode it) {
        if (it.isReturn) {
            if (it.retExpr != null) {
                it.retExpr.accept(this);
                if (currentFunc.retReg != null) {
                    entity rd;
                    IRType rsIRType = it.retExpr.irType, targetIRType = currentFunc.returnType;
                    if (!(it.retExpr.rd instanceof constant)) {
                        if (rsIRType.isString || targetIRType.isString) {
                            if (rsIRType.cDef == null) rd = constStringToString(it.retExpr);
                            else rd = it.retExpr.rd;
                        } else if (!rsIRType.equal(targetIRType)) {
                            rd = new register();
                            convertOp.convertType convertType;
                            if (rsIRType.intLen < targetIRType.intLen) convertType = convertOp.convertType.ZEXT;
                            else convertType = convertOp.convertType.TRUNC;
                            currentBlock.push_back(new convertOp((register) rd, it.retExpr.rd, convertType, targetIRType, rsIRType));
                        } else {
                            rd = it.retExpr.rd;
                        }
                    } else {
                        rd = it.retExpr.rd;
                    }
                    currentBlock.push_back(new store(rd, currentFunc.retReg, currentFunc.returnType));
                }
                currentBlock.push_back(new br(null, currentFunc.returnBlock, null));
            } else { // return block directly
                currentBlock.push_back(new br(null, currentFunc.returnBlock, null));
            }
        } else if (it.isBreak) {
            currentBlock.push_back(new br(null, loopExitBlock, null));
        } else {    // is Continue
            currentBlock.push_back(new br(null, loopContinueBlock, null));
        }
    }

    @Override
    public void visit(expressionStatementNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(declarationStatementNode it) {
        if (it.isClassDef) it.struct.accept(this);
        else {
            Type t = new Type(globalScope.queryType(it.arraySpec.type, it.arraySpec.pos));
            t.dimension = it.arraySpec.emptyBracketPair;
            IRType irType;
            if (t.kind == Type.Types.CLASS_TYPE) {
                irType = new IRType(idToClsDef.get(t.name), t.dimension + 1, 0);
                if (Objects.equals(t.name, "string") && t.dimension == 0) irType.isString = true;
            } else {
                irType = new IRType(t);
            }

            for (declaratorNode node : it.initList) {
                if (currentFunc != null || currentStruct == null) {
                    register rd = new register();
                    if (currentFunc != null) {
                        // declare var inside the function
                        currentScope.defineVar(node.id, t, node.pos);
                        currentScope.linkReg(node.id, rd, irType.getPtr());
                        alloca Alloca = new alloca(rd, irType);
                        currentFunc.push_back(Alloca);
                        if (node.expr != null) {
                            node.expr.accept(this);
                            entity rs;
                            IRType exprType = node.expr.irType;
                            if (irType.isString && exprType.cDef == null) { // const string
                                rs = constStringToString(node.expr);
                            } else if (irType.ptrNum == 0 && exprType.ptrNum == 0 && irType.intLen != exprType.intLen) {
                                convertOp.convertType op;
                                if (irType.intLen > exprType.intLen) op = convertOp.convertType.SEXT;
                                else op = convertOp.convertType.TRUNC;
                                rs = new register();
                                currentBlock.push_back(new convertOp((register) rs, node.expr.rd, op, irType, exprType));
                            } else rs = node.expr.rd;
                            store Store = new store(rs, rd, irType);
                            currentBlock.push_back(Store);
                        }
                    } else {
                        // global var
                        currentScope.linkReg(node.id, rd, irType.getPtr());
                        entity en;
                        if (node.expr != null) {
                            currentBlock = new block(loopDepth);
                            currentFunc = new funcDef();
                            currentFunc.returnType = voidType;
                            currentFunc.funcName = "_global_var_init." + initFuncCounter++;
                            currentFunc.rootBlock = currentBlock;
                            node.expr.accept(this);
                            currentFunc.returnBlock = currentBlock;
                            if (node.expr.rd instanceof constant) en = node.expr.rd;
                            else {
                                if (irType.ptrNum > 0) en = constVoid;
                                else en = constZero;
                                register rs;
                                if (irType.isString && node.expr.irType.cDef == null) {
                                    rs = constStringToString(node.expr);
                                } else rs = (register) node.expr.rd;
                                currentBlock.push_back(new store(rs, rd, irType));
                                currentBlock.push_back(new ret(null, voidType));
                                program.push_back(currentFunc);
//                                globalInit.directedCall.add(currentFunc);
                                globalInit.rootBlock.push_back(new call(null, voidType, currentFunc.funcName, currentFunc));
                            }
                            currentFunc = null;
                            currentBlock = null;
                        } else {
                            if (irType.ptrNum > 0) en = constVoid;
                            else en = constZero;
                        }
                        rd.label = node.id;
                        program.push_back(new globalVarDeclaration(rd, irType, en, node.id));
                    }
                } else {
                    // struct member init, illegal ?
                    currentScope.defineVar(node.id, t, node.pos);
                }
            }
        }
    }

    @Override
    public void visit(classSpecifierNode it) {
        currentStruct = (ClassType) globalScope.queryType(it.name, it.pos);
        currentScope = new Scope(currentScope);

        program.push_back(idToClsDef.get(it.name));
        it.declList.forEach(decl -> decl.accept(this));  // todo: need to check the type of decl?
        if (it.constructFunc != null) it.constructFunc.accept(this);

        currentStruct = null;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(assignExpressionNode it) {
        if (it.exprList != null) {
            // 此时 logicExpr 表示 等号左边的 expr
            it.exprList.forEach(expr -> expr.accept(this));
            expressionNode firstExpr = it.exprList.get(0);
            it.rd = firstExpr.rd;
            it.irType = firstExpr.irType;

            it.logicExpr.accept(this);
            store Store;
            if (!(firstExpr.rd instanceof constant && (((constant) firstExpr.rd).kind == constant.constType.VOID))
                    && !it.logicExpr.irType.equal(firstExpr.irType)) {
                // 类型不同，需要转换
                register rd = new register();
                if (it.logicExpr.irType.isString || firstExpr.irType.isString) {
                    rd = constStringToString(firstExpr);
                } else if (it.logicExpr.irType.ptrNum == 0 && firstExpr.irType.ptrNum == 0) {
                    convertOp.convertType convert;
                    if (it.logicExpr.irType.intLen > firstExpr.irType.intLen) convert = convertOp.convertType.SEXT;
                    else convert = convertOp.convertType.TRUNC;
                    currentBlock.push_back(new convertOp(rd, it.rd, convert, it.logicExpr.irType, firstExpr.irType));
                } else {
                    currentBlock.push_back(new bitcast(rd, (register) it.rd, it.logicExpr.irType, firstExpr.irType));
                }
                Store = new store(rd, it.logicExpr.idReg, it.logicExpr.irType);
            } else Store = new store(it.rd, it.logicExpr.idReg, it.logicExpr.irType);
            currentBlock.push_back(Store);
        } else {
            // 此时 logicExpr 表示等号右边的 expr
            it.logicExpr.accept(this);
            it.rd = it.logicExpr.rd;
            it.idReg = it.logicExpr.idReg;
            it.irType = it.logicExpr.irType;
        }
    }

    @Override
    public void visit(logicOrExpressionNode it) {
        int len = it.exprList.size();

        if (len == 1) {
            // 实际上没有 || 运算符，直接传递数据即可
            expressionNode firstExpr = it.exprList.get(0);
            firstExpr.accept(this);
            it.rd = firstExpr.rd;
            it.idReg = firstExpr.idReg;
            it.irType = firstExpr.irType;
        } else {
            // 多个 || 运算，分成多个 block
            block exitBlk = new block(loopDepth);   // 原本的 block
            exitBlk.jump = true;
            register res = new register();
            phi Phi = new phi(res, i1);
            exitBlk.push_back(Phi);

            boolean constTrueFlag = false;
            // If there is a TRUE const. 短路求值
            expressionNode curExpr = it.exprList.get(0);
            for (int i = 0; i < len - 1; ++i) {
                curExpr.accept(this);
                block curExprBlk = new block(loopDepth);
                curExprBlk.jump = true;
                if (curExpr.rd instanceof constant cons) {
                    if (cons.getBoolValue()) {
                        // 短路求值, 直接跳转出当前 OR块
                        constTrueFlag = true;
                        Phi.push_back(new entityBlockPair(new constant(true), currentBlock));
                        currentBlock.push_back(new br(null, exitBlk, null));
                        break;
                    } else {
                        // 正常退出当前 expr
                        currentBlock.push_back(new br(null, curExprBlk, null));
                    }
                } else {
                    if (curExpr.irType.intLen == 8) {
                        register rd = new register();
                        currentBlock.push_back(new convertOp(rd, curExpr.rd, convertOp.convertType.TRUNC, i1, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    } else if (curExpr.irType.intLen != 1) {
                        // 大于 0 就认为是真
                        register rd = new register();
                        currentBlock.push_back(new icmp(rd, curExpr.rd, constZero, icmp.cmpOpType.SGT, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    }
                    // 短路求值2，如果已经计算出 true，直接返回即可
                    // 否则继续计算表达式
                    currentBlock.push_back(new br((register) curExpr.rd, exitBlk, curExprBlk));
                }
                Phi.push_back(new entityBlockPair(new constant(true), currentBlock));
                currentBlock = curExprBlk;
                curExpr = it.exprList.get(i + 1);
            }

            // calculate the last expr in exprList
            if (!constTrueFlag) {
                curExpr.accept(this);
                if (curExpr.rd instanceof constant cons) {
                    if (cons.getBoolValue()) {
                        Phi.push_back(new entityBlockPair(constUnit, currentBlock));
                    } else {
                        Phi.push_back(new entityBlockPair(constZero, currentBlock));
                    }
                } else {
                    if (curExpr.irType.intLen == 8) {
                        register rd = new register();
                        currentBlock.push_back(new convertOp(rd, curExpr.rd, convertOp.convertType.TRUNC, i1, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    } else if (curExpr.irType.intLen != 1) {
                        register rd = new register();
                        currentBlock.push_back(new icmp(rd, curExpr.rd, constZero, icmp.cmpOpType.SGT, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    }

                    Phi.push_back(new entityBlockPair(curExpr.rd, currentBlock));
                }
            }
            currentBlock.push_back(new br(null, exitBlk, null));
            currentBlock = exitBlk;
            it.rd = res;
            it.idReg = curExpr.idReg;
            it.irType = curExpr.irType;
        }
    }

    @Override
    public void visit(logicAndExpressionNode it) {
        int len = it.exprList.size();
        if (len == 1) {
            // 同理不存在 && 运算
            expressionNode firstExpr = it.exprList.get(0);
            firstExpr.accept(this);
            it.rd = firstExpr.rd;
            it.idReg = firstExpr.idReg;
            it.irType = firstExpr.irType;
        } else {
            block exitBlk = new block(loopDepth);
            exitBlk.jump = true;
            register res = new register();
            phi Phi = new phi(res, i1);
            exitBlk.push_back(Phi);

            boolean constFalseFlag = false;
            // If there is a False const. 短路求值
            expressionNode curExpr = it.exprList.get(0);
            for (int i = 0; i < len - 1; ++i) {
                curExpr.accept(this);
                block curExprBlk = new block(loopDepth);
                curExprBlk.jump = true;
                if (curExpr.rd instanceof constant cons) {
                    if (!cons.getBoolValue()) {
                        // 短路求值，如果出现过 FALSE，直接退出
                        constFalseFlag = true;
                        Phi.push_back(new entityBlockPair(new constant(false), currentBlock));
                        currentBlock.push_back(new br(null, exitBlk, null));
                        break;
                    } else {
                        currentBlock.push_back(new br(null, curExprBlk, null));
                    }
                } else {
                    if (curExpr.irType.intLen == 8) {
                        register rd = new register();
                        currentBlock.push_back(new convertOp(rd, curExpr.rd, convertOp.convertType.TRUNC, i1, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    } else if (curExpr.irType.intLen != 1){
                        register rd = new register();
                        currentBlock.push_back(new icmp(rd, curExpr.rd, constZero, icmp.cmpOpType.SGT, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    }
                    // 短路求值2, 结果为 FALSE 则直接退出
                    currentBlock.push_back(new br((register) curExpr.rd, curExprBlk, exitBlk));
                }
                Phi.push_back(new entityBlockPair(new constant(false), currentBlock));
                currentBlock = curExprBlk;
                curExpr = it.exprList.get(i + 1);
            }

            // calculate the last expr in exprList
            if (!constFalseFlag) {
                curExpr.accept(this);
                if (curExpr.rd instanceof constant cons) {
                    if (cons.getBoolValue()) {
                        Phi.push_back(new entityBlockPair(constUnit, currentBlock));
                    } else {
                        Phi.push_back(new entityBlockPair(constZero, currentBlock));
                    }
                } else {
                    if (curExpr.irType.intLen == 8) {
                        register rd = new register();
                        currentBlock.push_back(new convertOp(rd, curExpr.rd, convertOp.convertType.TRUNC, i1, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    } else if (curExpr.irType.intLen != 1) {
                        register rd = new register();
                        currentBlock.push_back(new icmp(rd, curExpr.rd, constZero, icmp.cmpOpType.SGT, curExpr.irType));
                        curExpr.rd = rd;
                        curExpr.irType = i1;
                    }
                    Phi.push_back(new entityBlockPair(curExpr.rd, currentBlock));
                }
            }
            currentBlock.push_back(new br(null, exitBlk, null));
            currentBlock = exitBlk;
            it.rd = res;
            it.idReg = curExpr.idReg;
            it.irType = curExpr.irType;
        }
    }

    @Override
    public void visit(inclusiveOrExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        for (int i = 1; i < len; ++i) {
            expressionNode curExpr = it.exprList.get(i);
            curExpr.accept(this);
            register rd = new register();   // a temp register to store the result
            currentBlock.push_back(new binary(binary.opType.OR, firstExpr.irType, rd, res, curExpr.rd));
            res = rd;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(exclusiveOrExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        for (int i = 1; i < len; ++i) {
            expressionNode curExpr = it.exprList.get(i);
            curExpr.accept(this);
            register rd = new register();
            currentBlock.push_back(new binary(binary.opType.XOR, firstExpr.irType, rd, res, curExpr.rd));
            res = rd;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(andExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        for (int i = 1; i < len; ++i) {
            expressionNode curExpr = it.exprList.get(i);
            curExpr.accept(this);
            register rd = new register();
            currentBlock.push_back(new binary(binary.opType.AND, firstExpr.irType, rd, res, curExpr.rd));
            res = rd;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(equalityExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        if (len > 1 && firstExpr.irType.isString) {
            // no cDef means it's const string
            if (firstExpr.irType.cDef == null) res = constStringToString(firstExpr);
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                icmp.cmpOpType op = it.opList.get(i - 1).equals("==") ? icmp.cmpOpType.EQ : icmp.cmpOpType.NEQ;
                register rd = new register(), rdCmp = new register();
                register curRs = curExpr.irType.cDef == null ? constStringToString(curExpr) : (register) curExpr.rd;
                // 需要调用内置函数 strcmp
                currentFunc.directCall.add(builtInFunc);
                call Call = new call(rdCmp, i32, "_string_getStrcmp", builtInFunc); //todo: i32/i1 ?
                Call.push_back(new entityTypePair(res, stringStar));
                Call.push_back(new entityTypePair(curRs, stringStar));
                currentBlock.push_back(Call);
                // real compare
                currentBlock.push_back(new icmp(rd, rdCmp, constZero, op, i32));
                res = rd;
            }
        } else {
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                icmp.cmpOpType op = it.opList.get(i - 1).equals("==") ? icmp.cmpOpType.EQ : icmp.cmpOpType.NEQ;
                register rd = new register();
                currentBlock.push_back(new icmp(rd, res, curExpr.rd, op, firstExpr.irType));
                res = rd;
            }
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = len > 1 ? i1 : firstExpr.irType;
    }

    @Override
    public void visit(relationalExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        if (len > 1 && firstExpr.irType.isString) {
            // no cDef means it's const string
            if (firstExpr.irType.cDef == null) res = constStringToString(firstExpr);
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                icmp.cmpOpType op;
                switch (it.opList.get(i - 1)) {
                    case "<" -> op = icmp.cmpOpType.SLT;
                    case ">" -> op = icmp.cmpOpType.SGT;
                    case "<=" -> op = icmp.cmpOpType.SLE;
                    case ">=" -> op = icmp.cmpOpType.SGE;
                    default -> throw new RuntimeException("unexpected op in relation expression!");
                }
                register rd = new register(), rdCmp = new register();
                register curRs = curExpr.irType.cDef == null ? constStringToString(curExpr) : (register) curExpr.rd;
                // 需要调用内置函数 strcmp
                currentFunc.directCall.add(builtInFunc);
                call Call = new call(rdCmp, i32, "_string_getStrcmp", builtInFunc); //todo: i32/i1 ?
                Call.push_back(new entityTypePair(res, stringStar));
                Call.push_back(new entityTypePair(curRs, stringStar));
                currentBlock.push_back(Call);
                // real compare
                currentBlock.push_back(new icmp(rd, rdCmp, constZero, op, i32));
                res = rd;
            }
        } else {
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                icmp.cmpOpType op;
                switch (it.opList.get(i - 1)) {
                    case "<" -> op = icmp.cmpOpType.SLT;
                    case ">" -> op = icmp.cmpOpType.SGT;
                    case "<=" -> op = icmp.cmpOpType.SLE;
                    case ">=" -> op = icmp.cmpOpType.SGE;
                    default -> throw new RuntimeException("unexpected op in relation expression!");
                }
                register rd = new register();
                currentBlock.push_back(new icmp(rd, res, curExpr.rd, op, firstExpr.irType));
                res = rd;
            }
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = len > 1 ? i1 : firstExpr.irType;
    }

    @Override
    public void visit(shiftExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        for (int i = 1; i < len; ++i) {
            expressionNode curExpr = it.exprList.get(i);
            curExpr.accept(this);
            binary.opType op = it.opList.get(i - 1).equals(">>") ? binary.opType.ASHR : binary.opType.SHL;
            register rd = new register();
            currentBlock.push_back(new binary(op, firstExpr.irType, rd, res, curExpr.rd));
            res = rd;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(additiveExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        IRType resType;
        if (len > 1 && firstExpr.irType.isString) {
            // 只可能是 string '+'
            if (firstExpr.irType.cDef == null) res = constStringToString(firstExpr);
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                register rd = new register();
                register curRs = curExpr.irType.cDef == null ? constStringToString(curExpr) : (register) curExpr.rd;
                // 调用 stringAppend 函数
                currentFunc.directCall.add(builtInFunc);
                call Call = new call(rd, stringStar, "_string_stringAppend", builtInFunc);
                Call.push_back(new entityTypePair(res, stringStar));
                Call.push_back(new entityTypePair(curRs, stringStar));
                currentBlock.push_back(Call);
                res = rd;
            }
            resType = stringStar;
        } else {
            for (int i = 1; i < len; ++i) {
                expressionNode curExpr = it.exprList.get(i);
                curExpr.accept(this);
                binary.opType op = it.opList.get(i - 1).equals("+") ? binary.opType.ADD : binary.opType.SUB;
                register rd = new register();
                currentBlock.push_back(new binary(op, firstExpr.irType, rd, res, curExpr.rd));
                res = rd;
            }
            resType = firstExpr.irType;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = resType;
    }

    @Override
    public void visit(multiplicativeExpressionNode it) {
        int len = it.exprList.size();
        expressionNode firstExpr = it.exprList.get(0);
        firstExpr.accept(this);
        entity res = firstExpr.rd;
        for (int i = 1; i < len; ++i) {
            expressionNode curExpr = it.exprList.get(i);
            curExpr.accept(this);
            binary.opType op;
            switch (it.opList.get(i - 1)) {
                case "*" -> op = binary.opType.MUL;
                case "/" -> op = binary.opType.SDIV;
                case "%" -> op = binary.opType.MOD;
                default -> throw new RuntimeException("unexpected op in multiplicative expression");
            }
            register rd = new register();
            currentBlock.push_back(new binary(op, i32, rd, res, curExpr.rd));
            res = rd;
        }
        it.rd = res;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(unaryExpressionNode it) {
        if (it.postfixExpr != null) {
            it.postfixExpr.accept(this);
            it.rd = it.postfixExpr.rd;
            it.idReg = it.postfixExpr.idReg;
            it.irType = it.postfixExpr.irType;
        } else if (it.newExpr != null) {
            it.newExpr.accept(this);
            it.rd = it.newExpr.rd;
            it.idReg = it.newExpr.idReg;
            it.irType = it.newExpr.irType;
        } else {
            it.unaryExpr.accept(this);
            it.rd = it.unaryExpr.rd;
            it.idReg = it.unaryExpr.idReg;
            it.irType = it.unaryExpr.irType;
            switch (it.op) {
                case "++" -> {  // 前缀的++，要修改原本的值
                    register rd = new register();
                    currentBlock.push_back(new binary(binary.opType.ADD, i32, rd, it.unaryExpr.rd, constUnit));
                    currentBlock.push_back(new store(rd, it.idReg, i32));
                    it.rd = rd;
                }
                case "--" -> {
                    register rd = new register();
                    currentBlock.push_back(new binary(binary.opType.SUB, i32, rd, it.unaryExpr.rd, constUnit));
                    currentBlock.push_back(new store(rd, it.idReg, i32));
                    it.rd = rd;
                }
                case "!" -> {
                    // 返回值必定是 bool
                    if (it.rd instanceof constant con) {
                        // 不会对 int 进行 ! 操作, 所以只能是 bool
                        con.setBoolValue(!con.getBoolValue());
                    } else {
                        register rd = new register(), rs;
                        if (it.irType.intLen != 1) {    // TRUNC -> i1
                            rs = new register();
                            currentBlock.push_back(new convertOp(rs, it.rd, convertOp.convertType.TRUNC, i1, it.irType));
                        } else rs = (register) it.rd;
                        currentBlock.push_back(new binary(binary.opType.XOR, i1, rd, rs, constUnit));
                        it.rd = rd;
                        it.irType = i1;
                    }
                }
                case "~" -> {
                    register rd = new register();
                    currentBlock.push_back(new binary(binary.opType.XOR, i32, rd, it.unaryExpr.rd, constFull));
                    it.rd = rd;
                }
                case "-" -> {
                    register rd = new register();
                    currentBlock.push_back(new binary(binary.opType.SUB, i32, rd, constZero, it.unaryExpr.rd));
                    it.rd = rd;
                }
                case "*" -> {
                }  //there is not (*a) operation in Mx
            }
        }
    }

    @Override
    public void visit(newExpressionNode it) {
        if (it.newArray != null) {
            it.type = new Type(globalScope.queryType(it.newArray.type, it.pos));
            it.type.dimension = it.newArray.emptyBracketPair;
            if (it.type.kind == Type.Types.CLASS_TYPE) {
                it.irType = new IRType(idToClsDef.get(it.type.name), it.type.dimension + 1, 0);
            } else it.irType = new IRType(it.type);
            it.idReg = null;
            it.rd = recursiveNew(it.newArray.lengths, 0, it.irType);
        } else {
            it.type = new Type(globalScope.queryType(it.typeName, it.pos));
            if (it.type.kind == Type.Types.CLASS_TYPE) {
                it.irType = new IRType(idToClsDef.get(it.typeName), 1, 0);
            } else it.irType = new IRType(it.type);

            int size = it.irType.cDef.getSize();
            register receive_ptr = new register();
            currentFunc.directCall.add(builtInFunc);
            currentFunc.directCall.add(builtInFunc);    // todo: wrong?
            call Call = new call(receive_ptr, i8Star, "myNew", builtInFunc);
            Call.push_back(new entityTypePair(new constant(size), i32));
            currentBlock.push_back(Call);
            it.rd = new register();
            it.idReg = null;
            currentBlock.push_back(new bitcast((register) it.rd, receive_ptr, it.irType, i8Star));
            // 调用 class 的构造函数
            String constructorName = "_" + it.type.name + "_" + it.type.name;
            if (idToFuncDef.containsKey(constructorName)) {
                currentFunc.directCall.add(idToFuncDef.get(constructorName));
                call constructor = new call(null, voidType, constructorName, idToFuncDef.get(constructorName));
                constructor.parameters.push(new entityTypePair(it.rd, it.irType));
                currentBlock.push_back(constructor);
            }
        }
    }

    @Override
    public void visit(postfixExpressionNode it) {
        if (it.isPlusPlus || it.isMinusMinus) {
            // 后缀 ++/--
            it.postfixExpr.accept(this);
            it.rd = it.postfixExpr.rd;  // 正常值传递
            it.idReg = it.postfixExpr.idReg;
            it.irType = it.postfixExpr.irType;
            binary.opType op = it.isPlusPlus ? binary.opType.ADD : binary.opType.SUB;
            register rd = new register();
            currentBlock.push_back(new binary(op, it.irType, rd, it.rd, constUnit));
            currentBlock.push_back(new store(rd, it.idReg, it.irType));
            // 和前缀的区别：没有对 it.rd 进行 ++/--
        } else if (it.isDot) {
            it.postfixExpr.accept(this);
            IRType irType = it.postfixExpr.irType;
            if (irType.cDef == null || irType.ptrNum > 1) {
                // use array size function (a.size() )
                register ptrMiddle = new register(), i32Ptr = new register();
                it.rd = new register();
                it.irType = i32;
                it.idReg = null;
                currentBlock.push_back(new bitcast(ptrMiddle, (register) it.postfixExpr.rd, i32Star, it.postfixExpr.irType)); // cast to arrayType
                currentBlock.push_back(new getelementptr(i32Ptr, ptrMiddle, i32Star, constZero, constZero)); //get first member in array (i.e. size function)
                currentBlock.push_back(new load((register) it.rd, i32Ptr, i32Star));
                lastCallId = null;
            } else if (irType.cDef.memberType.containsKey(((idExpressionNode) it.expr).content)) {
                // visit class member
                IRTypeWithCounter tmp = irType.cDef.memberType.get(((idExpressionNode) it.expr).content);
                register ptrReg = new register();
                currentBlock.push_back(new getelementptr(ptrReg, (register) it.postfixExpr.rd, it.postfixExpr.irType, constZero, new constant(tmp.counter)));
                it.rd = new register();
                it.idReg = ptrReg;
                it.irType = tmp.irType.reducePtr();
                currentBlock.push_back(new load((register) it.rd, ptrReg, tmp.irType));
            } else {
                //visit class method
                ClassType structTmp = (ClassType) globalScope.queryType(irType.cDef.name, it.postfixExpr.pos);
                lastCallFunc = structTmp.method.get(((idExpressionNode) it.expr).content);
                lastCallId = "_" + structTmp.name + "_" + lastCallFunc.name;
                lastFuncCaller = (register) it.postfixExpr.rd;
                lastFuncCallerIRType = it.postfixExpr.irType;
            }
        } else if (it.isParen) {
            // call function
            it.postfixExpr.accept(this);
            register rd = null;
            IRType ir;
            String currentCallId = lastCallId;
            FuncType currentCallFunc = lastCallFunc;
            register currentFuncCaller = lastFuncCaller;
            IRType currentFuncCallerIRType = lastFuncCallerIRType;
            lastCallId = null;
            lastCallFunc = null;
            lastFuncCaller = null;
            lastFuncCallerIRType = null;

            if (currentCallId != null) {
                funcDef funcTmp = idToFuncDef.get(currentCallId);
                ir = funcTmp.returnType;
                if (!ir.isVoid) rd = new register();
                currentFunc.directCall.add(idToFuncDef.get(currentCallId));
                call Call = new call(rd, ir, currentCallId, idToFuncDef.get(currentCallId));
                int counter = 0;

                if (Objects.equals(currentCallId, "println") || Objects.equals(currentCallId, "print")) {
                    expressionNode expr = it.expr.exprList.get(0);
                    expr.accept(this);
                    IRType irType = expr.irType;
                    register tmpReg = new register();
                    if (irType.cDef == null) { // const string
                        currentBlock.push_back(new bitcast(tmpReg, (register) expr.rd, i8Star, irType));
                    } else { // string type
                        register rdPtr = new register();
                        currentBlock.push_back(new getelementptr(rdPtr, (register) expr.rd, irType, constZero, constUnit));
                        currentBlock.push_back(new load(tmpReg, rdPtr, i8Star.getPtr()));
                    }
                    Call.push_back(new entityTypePair(tmpReg, i8Star));
                } else {
                    if (currentFuncCaller != null) {    // 把调用者作为第一个参数
                        counter++;
                        Call.push_back(new entityTypePair(currentFuncCaller, currentFuncCallerIRType));
                    }
                    if (it.expr != null) {
                        for (expressionNode expr : it.expr.exprList) {
                            expr.accept(this);
                            IRType tmpIRType = funcTmp.parameters.get(counter);
                            entity en;
                            if (tmpIRType.equal(expr.irType) || expr.irType.isVoid) en = expr.rd;
                            else {
                                if (tmpIRType.isString) { // const string
                                    en = constStringToString(expr);
                                } else {
                                    register tmpReg = new register();
                                    if (tmpIRType.ptrNum > 0 || tmpIRType.cDef != null || expr.irType.ptrNum > 0 || expr.irType.cDef != null)
                                        currentBlock.push_back(new bitcast(tmpReg, (register) expr.rd, tmpIRType, expr.irType));
                                    else {
                                        convertOp.convertType op = (tmpIRType.intLen > expr.irType.intLen) ? convertOp.convertType.ZEXT : convertOp.convertType.TRUNC;
                                        currentBlock.push_back(new convertOp(tmpReg, expr.rd, op, tmpIRType, expr.irType));
                                    }
                                    en = tmpReg;
                                }
                            }
                            Call.push_back(new entityTypePair(en, tmpIRType));
                            counter++;
                        }
                    }
                }
                currentBlock.push_back(Call);
            } else {
                // array size function
                ir = it.postfixExpr.irType;
                rd = (register) it.postfixExpr.rd;
            }
            it.rd = rd;
            it.idReg = null;
            it.irType = ir;
        } else if (it.isBracket) {
            // get array[k]'s address & value
            it.postfixExpr.accept(this);
            it.expr.accept(this);
            it.rd = new register();
            register ptrReg = new register(), i8ptr = new register(), middlePtr = new register(), locate = new register(), locateMiddle = new register();
            IRType ptrIRType = it.postfixExpr.irType;
            // calculate address
            currentBlock.push_back(new binary(binary.opType.MUL, i32, locateMiddle, it.expr.rd, new constant(ptrIRType.reducePtr().getSize())));
            currentBlock.push_back(new binary(binary.opType.ADD, i32, locate, locateMiddle, new constant(4)));
            if (!ptrIRType.equal(i8Star))
                currentBlock.push_back(new bitcast(i8ptr, (register) it.postfixExpr.rd, i8Star, ptrIRType));
            else i8ptr = (register) it.postfixExpr.rd;
            // get address
            currentBlock.push_back(new getelementptr(middlePtr, i8ptr, i8Star, locate, constZero));
            currentBlock.push_back(new bitcast(ptrReg, middlePtr, ptrIRType, i8Star));
            //  进入下一层数组
            it.irType = new IRType(ptrIRType);
            it.irType.ptrNum--;
            if (it.irType.ptrNum == 1 && it.irType.cDef != null && it.irType.cDef.name.equals("string"))
                it.irType.isString = true;
            currentBlock.push_back(new load((register) it.rd, ptrReg, ptrIRType));
            it.idReg = ptrReg;
        } else {
            it.primaryExpr.accept(this);
            it.rd = it.primaryExpr.rd;
            it.idReg = it.primaryExpr.idReg;
            it.irType = it.primaryExpr.irType;
        }
    }

    @Override
    public void visit(primaryExpressionNode it) {
        if (it.isThis) {
            regTypePair regType = currentScope.getEntity("this", true);
            it.rd = new register();
            it.idReg = regType.reg;
            currentBlock.push_back(new load((register) it.rd, it.idReg, regType.irType));
            it.irType = regType.irType.reducePtr();
        } else if (it.isExpr || it.isIdExpr) {
            it.expr.accept(this);
            it.rd = it.expr.rd;
            it.idReg = it.expr.idReg;
            it.irType = it.expr.irType;
        } else if (it.isLiteral) {
            it.expr.accept(this);
            it.rd = it.expr.rd;
//            it.idReg = it.expr.idReg;
            it.irType = it.expr.irType;
        } else {
            // it's lambda, don't need to work
        }
    }

    @Override
    public void visit(expressionNode it) {
        it.exprList.forEach(expr -> expr.accept(this));
        expressionNode firstExpr = it.exprList.get(0);
        it.type = firstExpr.type;
        it.rd = firstExpr.rd;
        it.idReg = firstExpr.idReg;
        it.irType = firstExpr.irType;
    }

    @Override
    public void visit(idExpressionNode it) {
        regTypePair regType;
        if (currentScope.containVar(it.content, false)) {
            regType = currentScope.getEntity(it.content, true);
        } else if (currentStruct != null && currentStruct.member.containsKey(it.content)) {
            // struct 成员
            regType = currentScope.getEntity("this", true);
            register thisReg = new register(), ptrReg = new register();
            currentBlock.push_back(new load(thisReg, regType.reg, regType.irType));
            it.rd = new register();
            IRTypeWithCounter tmp = regType.irType.cDef.memberType.get(it.content);
            //getelementptr's result is still pointer
            currentBlock.push_back(new getelementptr(ptrReg, thisReg, regType.irType.reducePtr(), new constant(0), new constant(tmp.counter)));
            it.idReg = ptrReg;
            it.irType = tmp.irType.reducePtr();
            currentBlock.push_back(new load((register) it.rd, ptrReg, tmp.irType));
            return;
        } else if (currentScope.containVar(it.content, true)) {
            regType = currentScope.getEntity(it.content, true);
        } else if (currentStruct != null && currentStruct.method.containsKey(it.content)) {
            // 成员函数
            lastCallFunc = currentStruct.method.get(it.content);
            lastCallId = "_" + currentStruct.name + "_" + lastCallFunc.name;
            regType = currentScope.getEntity("this", true);
            register tmpReg = new register();
            currentBlock.push_back(new load(tmpReg, regType.reg, regType.irType));
            lastFuncCaller = tmpReg;
            lastFuncCallerIRType = regType.irType.reducePtr();
            return;
        } else if (globalScope.funcTypes.containsKey(it.content)) {
            // global function
            lastCallFunc = globalScope.queryFuncType(it.content, it.pos);
            lastCallId = lastCallFunc.name;
            return;
        } else return;

        if (loopDepth > regType.reg.loopDepth) regType.reg.loopDepth = loopDepth;
        it.rd = new register();
        it.idReg = regType.reg;
        it.irType = regType.irType.reducePtr();
        currentBlock.push_back(new load((register) it.rd, it.idReg, regType.irType));
    }

    @Override
    public void visit(lambdaExpressionNode it) {

    }

    @Override
    public void visit(newArrayTypeNode it) {

    }

    @Override
    public void visit(arraySpecifierNode it) {

    }

    @Override
    public void visit(literalNode it) {
        if (it.isBool) {
            it.rd = new constant(it.content.equals("true"));
            it.irType = i1;
        } else if (it.isInt) {
            it.rd = new constant(Integer.parseInt(it.content));
            it.irType = i32;
        } else if (it.isString) {
            register rd = new register();
            it.rd = rd;
            StringBuilder sBuilder = new StringBuilder();
            int length = 0;
            for (int i = 0; i < it.content.length() - 1; ++i) {
                char c = it.content.charAt(i);
                length++;
                // 把转义字符转换成 16进制
                if (c == '\\') {
                    ++i;
                    char another = it.content.charAt(i);
                    if (another == 'n') {
                        sBuilder.append("\\0A");
                    } else if (another == '\\') {
                        sBuilder.append("\\5C");
                    } else if (another == '"') {
                        sBuilder.append("\\22");
                    }
                } else sBuilder.append(c);
            }
            sBuilder.append("\\00\"");  // '\0' -> '\00'
            String content = sBuilder.toString();
            IRType irType = new IRType(0, length, i8);
            irType.isString = true;
            int tmpCnt = program.strConst.size();
            rd.label = ".str" + ((tmpCnt > 0) ? "." + tmpCnt : "");
            program.push_back(new globalStringConst(content, it.content, tmpCnt, (register) it.rd, irType));
            it.irType = irType.getPtr();
            it.idReg = (register) it.rd;
        } else {
            it.rd = constVoid;
            it.irType = voidType;
        }
    }

    @Override
    public void visit(declaratorNode it) {

    }

    @Override
    public void visit(functionParameterDefNode it) {

    }

    //----------------------------------------------------------------------------------------------------------

    private register constStringToString(expressionNode firstExpr) {
        IRType firstIRType = firstExpr.irType;
        register receiveReg = new register(), afterCast = new register(), rdLen = new register(), rdCptr = new register(), i8Ptr = new register();
        currentFunc.directCall.add(builtInFunc);
        call Call = new call(receiveReg, i8Star, "myNew", builtInFunc);
        Call.push_back(new entityTypePair(new constant(12), i32));
        currentBlock.push_back(Call);
        currentBlock.push_back(new bitcast(afterCast, receiveReg, stringStar, i8Star));
        currentBlock.push_back(new getelementptr(rdLen, afterCast, stringStar, constZero, constZero));
        currentBlock.push_back(new store(new constant(firstIRType.arrayLen - 1), rdLen, i32));
        currentBlock.push_back(new bitcast(i8Ptr, (register) firstExpr.rd, i8Star, firstIRType));
        currentBlock.push_back(new getelementptr(rdCptr, afterCast, stringStar, constZero, constUnit));
        currentBlock.push_back(new store(i8Ptr, rdCptr, i8Star));
        return afterCast;
    }

    private register recursiveNew(ArrayList<expressionNode> exprList, int recursiveStep, IRType irType) {
        // 递归实现数组 new 操作
        ++loopDepth;
        register rd, receiveReg = new register(), i32ptr = new register();
        expressionNode curNode = exprList.get(recursiveStep);
        curNode.accept(this);
        IRType curIRType = new IRType(irType);
        curIRType.ptrNum--;
        currentFunc.directCall.add(builtInFunc);
        call Call = new call(receiveReg, i8Star, "myNew", builtInFunc);

        entity mallocSize, arrayLen = curNode.rd, rs;
        int irSize = curIRType.getSize();
        constant constIRSize = new constant(irSize);
        if (curNode.rd instanceof constant con) {
            mallocSize = new constant(con.getIntValue() * irSize + 4);
            rs = curNode.rd;
        } else {
            register middleReg = new register();
            mallocSize = new register();
            if (curNode.irType.intLen != 32) {
                rs = new register();
                currentBlock.push_back(new convertOp((register) rs, curNode.rd, convertOp.convertType.SEXT, i32, i32));
            } else rs = curNode.rd;
            currentBlock.push_back(new binary(binary.opType.MUL, i32, middleReg, rs, new constant(irSize)));
            currentBlock.push_back(new binary(binary.opType.ADD, i32, (register) mallocSize, middleReg, new constant(4)));
        }
        Call.push_back(new entityTypePair(mallocSize, i32));
        currentBlock.push_back(Call);
        currentBlock.push_back(new bitcast(i32ptr, receiveReg, i32Star, i8Star));
        currentBlock.push_back(new store(arrayLen, i32ptr, i32));

        if (irType.cDef == null && irType.intLen == 8) rd = receiveReg;
        else {
            rd = new register();
            currentBlock.push_back(new bitcast(rd, receiveReg, irType, i8Star));
        }
        if (recursiveStep + 1 != exprList.size()) {
            block body = new block(loopDepth), checkBlock = new block(loopDepth), exitBlock = new block(loopDepth);
            body.jump = checkBlock.jump = exitBlock.jump = true;
            body.comment = "loop created by new body " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            checkBlock.comment = "loop created by new check block " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            exitBlock.comment = "loop created by new exit block " + currentFunc.funcName + " loopDepth " + loopDepth + " iterCount " + iterCount;
            ++iterCount;
            register newLoopRd = new register(), iRd = new register(), cmpResult = new register(), addResult = new register();
            alloca newLoop = new alloca(newLoopRd, i32);
            newLoop.comments = "for new loop";
            //initialize
            currentFunc.push_back(newLoop);
            currentBlock.push_back(new store(new constant(4), newLoopRd, i32));
            currentBlock.push_back(new br(null, checkBlock, null));
            //checkBlock
            currentBlock = checkBlock;
            currentBlock.push_back(new load(iRd, newLoopRd, i32.getPtr()));
            currentBlock.push_back(new binary(binary.opType.ADD, i32, addResult, iRd, constIRSize));
            currentBlock.push_back(new store(addResult, newLoopRd, i32));
            currentBlock.push_back(new icmp(cmpResult, iRd, mallocSize, icmp.cmpOpType.SLT, i32));
            currentBlock.push_back(new br(cmpResult, body, exitBlock));
            //loop body
            currentBlock = body;
            register subPtr = new register(), subReceiver;
            currentBlock.push_back(new getelementptr(subPtr, receiveReg, i8Star, iRd, constZero));
            subReceiver = recursiveNew(exprList, recursiveStep + 1, curIRType);
            register afterBitCast;
            if (irType.cDef == null && irType.intLen == 8) afterBitCast = subPtr;
            else {
                afterBitCast = new register();
                currentBlock.push_back(new bitcast(afterBitCast, subPtr, irType, i8Star));
            }
            currentBlock.push_back(new store(subReceiver, afterBitCast, curIRType));
            currentBlock.push_back(new br(null, checkBlock, null));
            //exit block
            currentBlock = exitBlock;
        }
        --loopDepth;
        return rd;
    }
}
