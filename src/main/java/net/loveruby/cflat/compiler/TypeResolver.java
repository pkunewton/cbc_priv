package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.DeclarationVisitor;
import net.loveruby.cflat.ast.StructNode;
import net.loveruby.cflat.ast.TypedefNode;
import net.loveruby.cflat.ast.UnionNode;
import net.loveruby.cflat.entity.*;

public class TypeResolver extends Visitor implements EntityVisitor<Void>, DeclarationVisitor<Void>{


    public Void visit(StructNode struct) {
        return null;
    }

    public Void visit(UnionNode union) {
        return null;
    }

    public Void visit(TypedefNode typedef) {
        return null;
    }

    public Void visit(DefinedVariable var) {
        return null;
    }

    public Void visit(UndefinedVariable var) {
        return null;
    }

    public Void visit(DefinedFunction func) {
        return null;
    }

    public Void visit(UndefinedFunction func) {
        return null;
    }

    public Void visit(Constant constant) {
        return null;
    }
}
