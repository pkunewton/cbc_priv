package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科 2018/5/29
 */
public class UndefinedVariable extends Variable {

    public UndefinedVariable(TypeNode typeNode, String name) {
        super(false, typeNode, name);
    }

    public boolean isDefined() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    public boolean isInitialized() {
        return false;
    }

    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("isPrivate", isPrivate());
        d.printMember("typeNode", typeNode);
    }
}
