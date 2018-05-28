package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class DoWhileNode extends StmtNode {

    ExprNode cond;
    StmtNode body;

    public DoWhileNode(Location location, StmtNode body, ExprNode cond) {
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
        d.printMember("body", body);
        d.printMember("cond", cond);
    }
}
