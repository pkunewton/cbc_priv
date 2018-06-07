package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.DefinedFunction;
import net.loveruby.cflat.entity.DefinedVariable;
import net.loveruby.cflat.entity.Parameter;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.type.FunctionType;
import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘科  2018/6/7
 * 1、操作数的操作限制 如结构体加法， 指针乘法
 * 2、操作数的隐式类型转换
 */
public class TypeChecker extends Visitor {

    private final TypeTable typeTable;
    private final ErrorHandler handler;

    public TypeChecker(TypeTable typeTable, ErrorHandler handler) {
        this.typeTable = typeTable;
        this.handler = handler;
    }

    private void check(StmtNode node){
        visitStmt(node);
    }

    private void check(ExprNode node){
        visitExpr(node);
    }

    DefinedFunction currentFunction;

    public void check(AST ast) throws SemanticException {

        for (DefinedVariable variable: ast.definedVariables()){
            checkVariable(variable);
        }
        for (DefinedFunction function: ast.definedFunctions()){
            // 用于检测return 的值是否和 函数声明的返回类型一致
            currentFunction = function;
            checkReturnType(function);
            checkParamTypes(function);
            check(function.body());
        }

        if(handler.errorOccured()){
            throw new SemanticException("compile failed");
        }
    }

    // 函数返回值只能是 整数，指针
    private void checkReturnType(DefinedFunction f){
        if(isInvalidReturnType(f.returnType())){
            error(f.location(), "return invalid type: " + f.returnType());
        }
    }

    // 函数参数（形参和实参）只能是 整数，指针和数组
    private void checkParamTypes(DefinedFunction f){
        for (Parameter parameter: f.parameters()) {
            if(isInvalidParameterType(parameter.type())){
                error(parameter.location(), "invalid parameter type: " + parameter.type());
            }
        }
    }

    //
    //  Statements
    //

    public Void visit(BlockNode node){
        for (DefinedVariable variable: node.variables()){
            checkVariable(variable);
        }
//        visitStmts(node.stmts());
        for(StmtNode stmt: node.stmts()){
            check(stmt);
        }
        return null;
    }


    private void checkVariable(DefinedVariable var){
        // 变量不是 void
        if(isInvalidVariableType(var.type())){
            error(var.location(), "invalid variable type");
            return;
        }
        if(var.hasInitializer()){
            if(isInvalidLHSType(var.type())){
                error(var.location(), "invalid lhs type: " + var.type());
                return;
            }
            check(var.initializer());
            // 将变量初始化表达式类型 隐式转换成 变量声明的类型
            var.setInitializer(implicitCast(var.type(), var.initializer()));
        }
    }

    public Void visit(ExprStmtNode node){
        check(node);
        // 表达式语句的类型不能时 结构体或联合体
        if(isInvalidStatementType(node.expr().type())){
            error(node, "invalid statement type: " + node.expr().type());
            return null;
        }
        return null;
    }

    public Void visit(IfNode node){
        super.visit(node);
        checkCond(node.cond());
        return null;
    }

    public Void visit(WhileNode node){
        super.visit(node);
        checkCond(node.cond());
        return null;
    }

    public Void visit(ForNode node){
        super.visit(node);
        checkCond(node.cond());
        return null;
    }

    private void checkCond(ExprNode node){
        mustBeScalar(node, "condition expression");
    }


    public Void visit(SwitchNode node){
        super.visit(node);
        mustBeInteger(node.cond(), "condition expression");
        return null;
    }

    public Void visit(ReturnNode node){
        if(currentFunction.returnType().isVoid()){
            if(node.expr() != null){
                error(node, "return value from void function");
            }
        }else {
            if(node.expr() == null){
                error(node, "missing return value");
                return null;
            }
            if(node.expr().type().isVoid()){
                error(node, "returning void");
                return null;
            }
            // 把函数返回表达式隐式转换成函数返回类型
            node.setExpr(implicitCast(currentFunction.returnType(), node.expr()));
        }
        return null;
    }

    //
    // Assignment Expressions
    //

    public Void visit(AssignNode node){
        super.visit(node);
        if(!checkLHS(node.lhs())) return null;
        if(!checkRHS(node.rhs())) return null;
        node.setRHS(implicitCast(node.lhs().type(), node.rhs()));
        return null;
    }

    public Void visit(OpAssignNode node){
        super.visit(node);
        // 检查左右表达式类型
        if(!checkLHS(node.lhs())) return null;
        if(!checkRHS(node.rhs())) return null;
        if(node.operator().equals("+") || node.operator().equals("-")){
            // a += 1; a是指针时 右值只能是整数
            if(node.lhs().isPointer()){
                mustBeInteger(node.rhs(), node.operator());
                node.setRHS(implicitCast(node.lhs().type(), node.rhs()));
                return null;
            }
        }
        // 除了+= -+外，其余的复合赋值运算符不支持指针操作，（*= |= <<=）
        if(!mustBeInteger(node.lhs(), node.operator())) return null;
        if(!mustBeInteger(node.rhs(), node.operator())) return null;
        // 将比 signed int小的类型提升到signed int
        Type left = integerPromotion(node.lhs().type());
        Type right = integerPromotion(node.rhs().type());
        // 推断 结果类型
        Type opType = usualArithmticConversion(left, right);
        // 将结果类型转换成右值类型时，如果类型转换不安全
        if(!opType.isCompatible(left) && !isSafeIntegerCast(node.lhs(), opType)){
            warn(node, "incompatible implicit from " + opType + " to " + left);
        }
        if(!right.isSameType(opType)){
            // 将右值表达式隐式的转换成 运算结果 的类型
            node.setRHS(new CastNode(opType, node.rhs()));
        }
        return null;
    }

    /** allow safe implicit cast from integer literal like:
     *      char c = 0;
     *  "0" has a type integer, but we can cast (int)0 to (char)0 safely.
     *  只有同为整数类型，且当前类型的数值 应该 在目标类型的范围内，否则会丢失数据
     */
    private boolean isSafeIntegerCast(Node node, Type type){
        if(!type.isInteger()){
            return false;
        }
        if(!(node instanceof IntegerLiteralNode)){
            return false;
        }
        IntegerLiteralNode integer = (IntegerLiteralNode)node;
        return type.getIntegerType().isInDomain(integer.value());
    }

    private boolean checkLHS(ExprNode lhs){
        if(lhs.isParameter()){
            // 函数参数总是可以赋值
            return true;
        } else if(isInvalidRHSType(lhs.type())){
            error(lhs, "invalid lhs expression type: " + lhs.type());
            return false;
        }
        return true;
    }

    //
    // Expressions
    //

    // 表达式结果必须同为整数，指针和数组
    public Void visit(CondExprNode node){
        super.visit(node);
        checkCond(node.cond());
        Type then = node.thenExpr().type();
        Type els = node.elseExpr().type();
        if(then.isSameType(els)){
            return null;
        }else if(then.isCompatible(els)){
            node.setThenExpr(new CastNode(els, node.thenExpr()));
        }else if(els.isCompatible(then)){
            node.setElseExpr(new CastNode(then, node.elseExpr()));
        }else {
            invalidCastError(node.thenExpr(), els, then);
        }
        return null;
    }

    public Void visit(BinaryOpNode node){
        super.visit(node);
        if(node.operator().equals("+")||node.operator().equals("-")){
            expectsSameIntegerOrPointerDiff(node);
        }else if(node.operator().equals("*")
                || node.operator().equals("/")
                || node.operator().equals("%")
                || node.operator().equals("&")
                || node.operator().equals("|")
                || node.operator().equals("^")
                || node.operator().equals("<<")
                || node.operator().equals(">>")){
            expectsSameInteger(node);
        }else if(node.operator().equals("==")
                || node.operator().equals("!=")
                || node.operator().equals("<")
                || node.operator().equals("<=")
                || node.operator().equals(">")
                || node.operator().equals(">=")){
            expectsComparableScalar(node);
        }else {
            throw new Error("unknown binary operator: " + node.operator());
        }
        return null;
    }

    public Void visit(LogicalAndNode node){
        super.visit(node);
        expectsComparableScalar(node);
        return null;
    }

    public Void visit(LogicalOrNode node){
        super.visit(node);
        expectsComparableScalar(node);
        return null;
    }

    /**
     * For + and -, only following types of expression are valid:
     *
     *   * integer + integer
     *   * pointer + integer
     *   * integer + pointer
     *   * integer - integer
     *   * pointer - integer
     *   * pointer - pointer
     */
    private void expectsSameIntegerOrPointerDiff(BinaryOpNode node){
        if(node.left().isPointer() && node.right().isPointer()){
            if(node.operator().equals("+")){
                error(node, "invalid operation: pointer + pointer");
                return;
            }
            node.setType(typeTable.ptrDiffType());
        }else if(node.left().isPointer()){
            mustBeInteger(node.right(), node.operator());
            // promote integer for pointer calculation
            node.setRight(integralPromotedExpr(node.right()));
            node.setType(node.left().type());
        }else if(node.right().isPointer()){
            if(node.operator().equals("-")){
                error(node, "invalid operation: integer - pointer");
                mustBeInteger(node.left(), node.operator());
                node.setLeft(integralPromotedExpr(node.left()));
                node.setType(node.right().type());
            }
        }else {
            expectsSameInteger(node);
        }
    }

    private ExprNode integralPromotedExpr(ExprNode node){
        Type type = integerPromotion(node.type());
        if(type.isSameType(node.type())){
            return node;
        }
        return new CastNode(type, node);
    }

    // +, -, *, /, %, &, |, ^, <<, >>
    private void expectsSameInteger(BinaryOpNode node){
        if(!mustBeInteger(node.left(), node.operator())) return;
        if(!mustBeInteger(node.right(), node.operator())) return;
        arithmeticImplicitCast(node);
    }

    // ==, !=, >, >=, <, <=, &&, ||
    private void expectsComparableScalar(BinaryOpNode node){
        if(!mustBeScalar(node.left(), node.operator())) return;
        if(!mustBeScalar(node.right(), node.operator())) return;
        if(node.left().isPointer()){
            ExprNode right = forcePointerType(node.left(), node.right());
            node.setRight(right);
            node.setType(node.left().type());
            return;
        }
        if(node.right().isPointer()){
            ExprNode left = forcePointerType(node.right(), node.left());
            node.setLeft(left);
            node.setType(node.right().type());
            return;
        }
        arithmeticImplicitCast(node);
    }

    // cast slave node to master node.
    private ExprNode forcePointerType(ExprNode master, ExprNode slave){
        if(master.type().isCompatible(slave.type())){
            // 不需要转换
            return slave;
        }else {
            warn(slave, "incompatible implicit cast from " +
                    slave.type() + " to " + master.type());
            return new CastNode(master.type(), slave);
        }
    }

    // Processes usual arithmetic conversion for binary operations.
    private void arithmeticImplicitCast(BinaryOpNode node){
        Type left = integerPromotion(node.left().type());
        Type right = integerPromotion(node.right().type());
        Type target = usualArithmticConversion(left, right);
        if(!left.isSameType(target)){
            node.setLeft(new CastNode(target, node.left()));
        }
        if(!right.isSameType(target)){
            node.setRight(new CastNode(target, node.right()));
        }
        node.setType(target);
    }

    // +, -, !, ~
    public Void visit(UnaryOpNode node){
        super.visit(node);
        if(node.operator().equals("!")){
            mustBeScalar(node.expr(), node.operator());
        }else {
            mustBeInteger(node.expr(), node.operator());
        }
        return null;
    }

    // ++x, --x
    public Void visit(PrefixOpNode node){
        super.visit(node);
        expectsScalarLHS(node);
        return null;
    }

    // x++, x--
    public Void visit(SuffixOpNode node){
        super.visit(node);
        expectsScalarLHS(node);
        return null;
    }

    private void expectsScalarLHS(UnaryArithmeticOpNode node){
        if(node.expr().isParameter()){
            // parameter is always a scalar.
        }else if(node.expr().type().isArray()){
            // We cannot modify non-parameter array.
            wrongTypeError(node.expr(), node.operator());
            return;
        }else {
            mustBeScalar(node.expr(), node.operator());
        }

        if(node.expr().type().isInteger()){
            Type opType = integerPromotion(node.expr().type());
            if(!node.expr().type().isSameType(opType)){
                node.setOpType(opType);
            }
            node.setAmount(1);
        }else if(node.expr().type().isPointer()){
            if(node.expr().type().baseType().isVoid()){
                // We cannot increment/decrement void*
                wrongTypeError(node.expr(), node.operator());
                return;
            }
            node.setAmount(node.expr().type().baseType().size());
        }else {
            throw new Error("must not happen");
        }
    }


    /**
     * For EXPR(ARG), checks:
     *
     *   * The number of argument matches function prototype.
     *   * ARG matches function prototype.
     *   * ARG is neither a struct nor an union.
     */
    public Void visit(FuncallNode node){
        super.visit(node);
        FunctionType type = node.funtionType();
        if(!type.acceptArgc(node.numArgs())){
            error(node, "wrong number of argments: " + node.numArgs());
            return null;
        }
        Iterator<ExprNode> args = node.args().iterator();
        List<ExprNode> newArgs = new ArrayList<ExprNode>();
        for(Type param: type.paramTypes()){
            ExprNode arg = args.next();
            newArgs.add(checkRHS(arg) ? implicitCast(param, arg): arg);
        }
        // 参数是可变长参数
        while (args.hasNext()){
            ExprNode arg = args.next();
            newArgs.add(checkRHS(arg) ? castOptionalArg(arg) : arg);
        }
        node.replaceArgs(newArgs);
        return null;
    }

    private ExprNode castOptionalArg(ExprNode arg){
        if(!arg.type().isInteger()){
            return arg;
        }
        Type type = arg.type().isSigned() ? typeTable.signedStackType()
                : typeTable.unsignedStackType();
        return arg.type().size() < type.size() ? implicitCast(type, arg) : arg;
    }

    public Void visit(ArefNode node){
        super.visit(node);
        mustBeInteger(node.index(), "[]");
        return null;
    }

    public Void visit(CastNode node){
        super.visit(node);
        if(!node.expr().type().isCastableTo(node.type())){
            invalidCastError(node, node.expr().type(), node.type());
        }
        return null;
    }


    //
    // Utilities
    //

    private boolean checkRHS(ExprNode rhs){
        if(isInvalidRHSType(rhs.type())){
            error(rhs, "invalid RHS expression type: " + rhs.type());
            return false;
        }
        return true;
    }

    // Processes forced-implicit-cast.
    // Applied To: return expr, assignment RHS, funcall argument
    private ExprNode implicitCast(Type target, ExprNode node){
        if(node.type().isSameType(target)){
            return node;
        }else if(node.type().isCastableTo(target)){
            // 当前类型的数值 应该在 目标类型的表示范围内，否则会丢失数据
            if(!node.type().isCompatible(target) &&
                    !isSafeIntegerCast(node, target)){
                warn(node, "incompatible implicit cast from "
                        + node.type() + " to " + target);
            }
            return new CastNode(target, node);
        }else {
            invalidCastError(node, node.type(), target);
            return node;
        }
    }

    // Process integral promotion (integers only).
    // 提升整数类型，如 short -> int
    private Type integerPromotion(Type type){
        if(!type.isInteger()){
            throw new Error("integerPromotion for " + type);
        }
        Type intType = typeTable.signedInt();
        if(type.size() <= intType.size()){
            return intType;
        }
        return type;
    }

    // Usual arithmetic conversion for ILP32 platform (integers only).
    // Size of l, r >= sizeof(int).
    // ILP32平台， 常用算数转换, 根据左右值的类型，推断结果类型
    private Type usualArithmticConversion(Type left, Type right){
        Type s_int = typeTable.signedInt();
        Type u_int = typeTable.unsignedInt();
        Type s_long = typeTable.signedLong();
        Type u_long = typeTable.unsignedLong();
        if((left.isSameType(u_int) && right.isSameType(s_long))
                || (right.isSameType(u_int) && left.isSameType(s_long)) ){
            return u_long;
        }else if(left.isSameType(u_long) || right.isSameType(u_long)){
            return u_long;
        }else if(left.isSameType(s_long) || right.isSameType(s_long)){
            return s_long;
        }else if(left.isSameType(u_int) || right.isSameType(u_int)){
            return u_int;
        }
        return s_int;
    }

    private boolean isInvalidStatementType(Type type){
        return type.isStruct() || type.isUnion();
    }

    // 函数返回值只能是 整数，指针
    private boolean isInvalidReturnType(Type type){
        return type.isStruct() || type.isUnion() || type.isArray();
    }

    // 函数参数（形参和实参）只能是 整数，指针和数组
    private boolean isInvalidParameterType(Type type){
        return type.isStruct() || type.isUnion() || type.isIncompleteArray()
                || type.isVoid();
    }

    // 变量不是 void
    private boolean isInvalidVariableType(Type type){
        return type.isVoid() || (type.isArray() || !type.isAllocateArray());
    }

    // 左值 只能是 整数和指针
    private boolean isInvalidLHSType(Type type){
        // Array is OK if it is declared as a type of parameter.
        // 函数参数可以是数组
        return type.isStruct() || type.isUnion() || type.isVoid() || type.isArray();
    }

    private boolean isInvalidRHSType(Type type){
        return type.isStruct() || type.isUnion() || type.isVoid();
    }



    private boolean mustBeInteger(ExprNode expr, String op){
        if(!expr.type().isInteger()){
            wrongTypeError(expr, op);
            return false;
        }
        return true;
    }

    // 是否是整数和指针
    private boolean mustBeScalar(ExprNode expr, String op){
        if(!expr.type().isScalar()){
            wrongTypeError(expr, op);
            return false;
        }
        return true;
    }


    private void invalidCastError(Node node, Type left, Type right){
        error(node, "invalid cast from " + left + " to " + right);
    }

    private void wrongTypeError(ExprNode node, String op){
        error(node, "wrong operand type for " + op + ": " + node.type());
    }

    private void warn(Node node, String msg){
        handler.warn(node.location(), msg);
    }

    private void error(Node node, String msg){
        error(node.location(), msg);
    }

    private void error(Location location, String msg){
        handler.error(location, msg);
    }
}
