package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class GotoNode extends StmtNode {

    protected String target;

    public GotoNode(Location location, String target) {
        super(location);
        this.target = target;
    }

    public String target() {
        return target;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("target", target);
    }
}
