package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Operand;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科 2018/02/08
 * */
abstract public class Entity
            implements net.loveruby.cflat.ast.Dumpable {

    protected String name;
    protected boolean isPrivate;
    protected TypeNode typeNode;
    protected long nRefered;             // 变量引用次数，用于定义的变量判断是否被使用 LocalResolver 中检查
    protected MemoryReference memref;
    protected Operand address;

    public Entity(String name, boolean isPrivate, TypeNode typeNode){
        this.name = name;
        this.isPrivate = isPrivate;
        this.typeNode = typeNode;
        this.nRefered = 0;
    }

}
