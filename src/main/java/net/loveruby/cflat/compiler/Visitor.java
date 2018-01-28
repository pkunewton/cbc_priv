package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.ASTVisitor;
import net.loveruby.cflat.ast.AssignNode;
import net.loveruby.cflat.ast.BlockNode;

abstract public class Visitor implements ASTVisitor<Void, Void> {

    public Visitor(){

    }

    public Void visit(BlockNode node) {
        return null;
    }

    public Void visit(AssignNode node) {
        return null;
    }
}
