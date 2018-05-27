package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/27
 */
abstract public class StmtNode extends Node {

    protected Location location;

    public StmtNode(Location location){
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }

    abstract public <S,E> S accept(ASTVisitor<S,E> visitor);
}
