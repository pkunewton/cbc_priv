package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class IfNode extends StmtNode {

    protected ExprNode cond;
    protected StmtNode thenBody;
    protected StmtNode elseBody;

    public IfNode(Location location, ExprNode cond, StmtNode thenBody, StmtNode elseBody) {
        super(location);
        this.cond = cond;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    public ExprNode cond() {
        return cond;
    }

    public StmtNode thenBody() {
        return thenBody;
    }

    public StmtNode elseBody() {
        return elseBody;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printMember("thenBody", thenBody);
        d.printMember("elseBody", elseBody);
    }
}
