package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.BlockNode;
import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;

/**
 * Created by Administrator on 2018/2/6.
 */
public class DefinedFunction extends Function {

    protected BlockNode body;

    public DefinedFunction(String name, boolean isPrivate, TypeNode typeNode) {
        super(name, isPrivate, typeNode);
    }

    public BlockNode body() {
        return body;
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
