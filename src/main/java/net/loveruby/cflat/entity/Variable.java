package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.TypeNode;

abstract public class Variable extends Entity {

    public Variable(String name, boolean isPrivate, TypeNode typeNode) {
        super(name, isPrivate, typeNode);
    }
}
