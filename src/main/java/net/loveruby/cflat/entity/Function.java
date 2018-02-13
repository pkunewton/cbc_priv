package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.asm.Symbol;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科  2018/02/13
 * */
abstract public class Function extends Entity {

    protected Symbol callingSymbol;
    protected Label label;

    public Function(String name, boolean isPrivate, TypeNode typeNode) {
        super(name, isPrivate, typeNode);
    }
}
