package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class ForNode extends StmtNode {

    protected StmtNode init, incr, body;
    protected ExprNode cond;

    public ForNode(Location location, StmtNode init, ExprNode cond, StmtNode incr, StmtNode body) {
        super(location);
        this.init = init;
        this.cond = cond;
        this.incr = incr;
        this.body = body;
    }


    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public StmtNode init() {
        return init;
    }

    public StmtNode incr() {
        return incr;
    }

    public StmtNode body() {
        return body;
    }

    public ExprNode cond() {
        return cond;
    }

    protected void _dump(Dumper d) {
        d.printMember("init", init);
        d.printMember("cond", cond);
        d.printMember("incr", incr);
        d.printMember("body", body);
    }
}
