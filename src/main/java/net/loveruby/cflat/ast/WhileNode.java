package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class WhileNode extends StmtNode {

    protected StmtNode body;
    protected ExprNode cond;

    public WhileNode(Location location, ExprNode cond, StmtNode body) {
        super(location);
        this.cond = cond;
        this.body = body;
    }

    public ExprNode cond() {
        return cond;
    }

    public StmtNode body() {
        return body;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printMember("body", body);
    }
}
