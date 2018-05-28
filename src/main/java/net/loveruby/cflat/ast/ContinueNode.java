package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class ContinueNode extends StmtNode {

    public ContinueNode(Location location) {
        super(location);
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {

    }
}
