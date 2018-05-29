package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科 2018/5/29
 */
abstract public class Variable extends Entity {

    public Variable(boolean isPrivate, TypeNode typeNode, String name) {
        super(isPrivate, typeNode, name);
    }
}
