package net.loveruby.cflat.ast;


public interface ASTVisitor<S, E> {
    // statements
    public S visit(BlockNode node);

    // expressions
    public E visit(AssignNode node);
}
