package net.loveruby.cflat.ast;

import net.loveruby.cflat.exception.SemanticError;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.type.FunctionType;
import net.loveruby.cflat.type.Type;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class FuncallNode extends ExprNode {

    protected ExprNode expr;
    protected List<ExprNode> args;

    public FuncallNode(ExprNode expr, List<ExprNode> args){
        this.expr = expr;
        this.args = args;
    }

    public ExprNode expr() {
        return expr;
    }

    /**
     * 函数返回值类型
     * Returns a type of return value of the function which is refered
     * by expr.  This method expects expr.type().isCallable() is true.
     */
    public Type type() {
        try {
            return funtionType().returnType();
        }catch (ClassCastException e) {
            throw new SemanticError(e.getMessage());
        }
    }

    /**
     * 函数类型
     * Returns a type of function which is refered by expr.
     * This method expects expr.type().isCallable() is true.
     */
    public FunctionType funtionType(){
        return expr.type().getPointerType().baseType().getFunctionType();
    }

    public long numArgs(){
        return args.size();
    }

    public List<ExprNode> args() {
        return args;
    }

    /**
     * @see net.loveruby.cflat.compiler.TypeChecker
     */
    public void replaceArgs(List<ExprNode> args){
        this.args = args;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return expr.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("expr", expr);
        d.printNodeList("args", args);
    }
}
