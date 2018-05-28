package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class ReturnNode extends StmtNode {

    protected ExprNode expr;

    public ReturnNode(Location location, ExprNode expr) {
        super(location);
        this.expr = expr;
    }

    public ExprNode expr() {
        return expr;
    }

    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("expr", expr);
    }
}
