package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 * 条件运算时 a?b:c
 */
public class CondExprNode extends ExprNode {

    protected ExprNode cond, thenExpr, elseExpr;

    public CondExprNode(ExprNode cond, ExprNode thenExpr, ExprNode elseExpr){
        this.cond = cond;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    public Type type() {
        return thenExpr.type();
    }

    public ExprNode cond() {
        return cond;
    }

    public ExprNode elseExpr() {
        return elseExpr;
    }

    public void setElseExpr(ExprNode elseExpr){
        this.elseExpr = elseExpr;
    }

    public ExprNode thenExpr() {
        return thenExpr;
    }

    public void setThenExpr(ExprNode thenExpr){
        this.thenExpr = thenExpr;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return cond.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printMember("thenExpr", thenExpr);
        d.printMember("elseExpr", elseExpr);
    }
}
