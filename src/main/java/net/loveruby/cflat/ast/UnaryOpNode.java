package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
public class UnaryOpNode extends ExprNode {

    protected String operator;
    protected ExprNode expr;
    // 计算后的类型  short a = 1 ; a++ opType 是 int
    protected Type opType;

    public UnaryOpNode(String operator, ExprNode expr){
        this.expr = expr;
        this.operator = operator;
    }

    public String operator() {
        return operator;
    }

    @Override
    public Type type() {
        return expr.type();
    }

    public void setOpType(Type opType) {
        this.opType = opType;
    }

    public Type opType() {
        return opType;
    }

    public ExprNode expr() {
        return expr;
    }

    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

    @Override
    public Location location() {
        return expr.location();
    }

    @Override
    protected void _dump(Dumper d) {
        d.printMember("operator", operator);
        d.printMember("expr", expr);
    }

    public <S,E> E accept(ASTVisitor<S,E> visitor){
        return visitor.visit(this);
    }
}
