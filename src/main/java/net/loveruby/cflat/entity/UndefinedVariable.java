package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;

/**
 * Created by Administrator on 2018/2/6.
 */
public class UndefinedVariable extends Variable {

    public UndefinedVariable(String name, boolean isPrivate, TypeNode typeNode) {
        super(name, isPrivate, typeNode);
    }

    public boolean isDefined() {
        return false;
    }

    public boolean isInitialized() {
        return false;
    }

    public <T> T accept(EntityVisitor<T> visitor) {
        return null;
    }

    protected void _dump(Dumper d) {

    }
}
