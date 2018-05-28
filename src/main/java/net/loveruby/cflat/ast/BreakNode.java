package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/27
 */
public class BreakNode extends StmtNode {

    public BreakNode(Location location) {
        super(location);
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {

    }
}
