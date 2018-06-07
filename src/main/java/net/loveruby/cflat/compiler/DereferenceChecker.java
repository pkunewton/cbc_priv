package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.DefinedFunction;
import net.loveruby.cflat.entity.DefinedVariable;
import net.loveruby.cflat.exception.SemanticError;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.type.CompositeType;
import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

/**
 * @author 刘科  2018/6/6
 * 1、未无法赋值的表达式赋值  1=1+1
 * 2、使用非法的函数名调用函数  sxas(1)
 * 3、操作数非法的数组引用    1[2]
 * 4、操作数非法的成员引用    a.b
 * 5、操作数非法的指针间接引用  a->b
 * 6、对非指针对象取值     *p
 * 7、对非左值的表达式取地址  $a
 */
public class DereferenceChecker extends Visitor{

    private final TypeTable typeTable;
    private final ErrorHandler errorHandler;

    public DereferenceChecker(TypeTable typeTable, ErrorHandler errorHandler) {
        this.typeTable = typeTable;
        this.errorHandler = errorHandler;
    }

    private void check(StmtNode node){
        visitStmt(node);
    }

    private void check(ExprNode node){
        visitExpr(node);
    }


    public void check(AST ast) throws SemanticException {

        for(DefinedVariable variable: ast.definedVariables()){
            checkToplevelVariable(variable);
        }

        for (DefinedFunction function: ast.definedFunctions()){
            check(function.body());
        }

        if(errorHandler.errorOccured()){
            throw new SemanticException("compile failed");
        }
    }

    private void checkToplevelVariable(DefinedVariable variable){
        checkVariable(variable);
        if(variable.hasInitializer()){
            checkConstant(variable.initializer());
        }
    }

    private void checkConstant(ExprNode expr){
        if(!expr.isConstant()){
            errorHandler.error(expr.location(), "toplevel variable is not a constant");
        }
    }

    //
    // Statements
    //

    public Void visit(BlockNode node){
        for (DefinedVariable variable: node.variables()){
            checkVariable(variable);
        }
        for(StmtNode stmt: node.stmts()){
            try {
                check(stmt);
            } catch (SemanticError e) {
                ;
            }
        }
        return null;
    }

    private void checkVariable(DefinedVariable variable){
        if(variable.hasInitializer()){
            try {
                check(variable.initializer());
            } catch (SemanticError e) {
                ;
            }
        }
    }


    //
    //  Assignment Expression 赋值表达式
    //

    public Void visit(AssignNode node){
        super.visit(node);
        checkAssignment(node);
        return null;
    }

    public Void visit(OpAssignNode node){
        super.visit(node);
        checkAssignment(node);
        return null;
    }


    private void checkAssignment(AbstractAssignNode node){
        // 必须是左值且不是数组或者函数指针
        if(!node.lhs().isAssignable()){
            semanticError(node, "invalid lhs expression");
        }
    }

    //
    //  Expression
    //

    public Void visit(PrefixOpNode node){
        super.visit(node);
        if(!node.expr().isAssignable()){
            semanticError(node.expr(), "can not increment/decrement");
        }
        return null;
    }

    public Void visit(SuffixOpNode node){
        super.visit(node);
        if(!node.expr().isAssignable()){
            semanticError(node.expr(), "can not increment/decrement");
        }
        return null;
    }

    @Override
    public Void visit(FuncallNode node) {
        super.visit(node);
        if (!node.isCallable()){
            semanticError(node, "calling object is not a function");
        }
        return null;
    }

    public Void visit(ArefNode node){
        super.visit(node);
        if (!node.expr().isPointer()){
            semanticError(node, "indexing non-array/pointer expression");
        }
        handleImplicitAddress(node);
        return null;
    }

    public Void visit(MemberNode node){
        super.visit(node);
        checkMemberRef(node.location(), node.baseType(), node.member());
        handleImplicitAddress(node);
        return null;
    }

    public Void visit(PtrMemberNode node){
        super.visit(node);
        if(!node.expr().isPointer()){
            undereferableError(node.location());
        }
        checkMemberRef(node.location(), node.dereferedType(), node.member());
        handleImplicitAddress(node);
        return null;
    }

    private void checkMemberRef(Location location, Type type, String member){
        if(!type.isCompositeType()){
            semanticError(location, "accessing member `" + member +
                     "` for non-struct/union: " + type);
        }
        CompositeType ct = type.getCompositeType();
        if (!ct.hasMember(member)){
            semanticError(location, ct.toString() + " does not have member " + member);
        }
    }

    public Void visit(DereferenceNode node){
        super.visit(node);
        if (!node.expr().isPointer()){
            undereferableError(node.location());
        }
        handleImplicitAddress(node);
        return null;
    }

    public Void visit(AddressNode node){
        super.visit(node);
        if(!node.expr().isLvalue()){
            semanticError(node, "invalid expression for &");
        }
        Type base = node.expr().type();
        if(!node.expr().isLoadable()){
            node.setType(base);
        }else {
            node.setType(typeTable.pointerTo(base));
        }

        return null;
    }

    public Void visit(VariableNode node){
        super.visit(node);
        if(node.entity().isConstant()){
            checkConstant(node.entity().value());
        }
        handleImplicitAddress(node);
        return null;
    }

    public Void visit(CastNode node){
        super.visit(node);
        if(node.type().isArray()){
            semanticError(node, "cast specifies array type");
        }
        return null;
    }


    //
    // Utilities
    //


    // 数组元素 和 函数类型 作为左值时特殊处理
    private void handleImplicitAddress(LHSNode node){

        if(!node.isLoadable()){
            Type type = node.type();
            if(type.isArray()){
                // int[] 类型  转换成  int*
                node.setType(typeTable.pointerTo(type.baseType()));
            }else if (type.isFunction()){
                node.setType(typeTable.pointerTo(type));
            }
        }
    }

    private void undereferableError(Location location){
        semanticError(location, "deferencing non-pointer expression");
    }

    private void semanticError(Node node, String msg){
        semanticError(node.location(), msg);
    }

    private void semanticError(Location location, String msg){
        errorHandler.error(location, msg);
        throw new SemanticError("invalid expr");
    }
}
