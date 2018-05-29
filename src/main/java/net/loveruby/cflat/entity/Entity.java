package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.ImmediateValue;
import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Operand;
import net.loveruby.cflat.ast.ExprNode;
import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.TypeNode;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/02/08
 * */
abstract public class Entity
            implements net.loveruby.cflat.ast.Dumpable {

    protected String name;
    protected boolean isPrivate;
    protected TypeNode typeNode;
    // 变量引用次数
    protected long nRefered;
    protected MemoryReference memref;
    protected Operand address;

    public Entity(boolean priv, TypeNode typeNode, String name) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.typeNode = typeNode;
        this.nRefered = 0;
    }

    public String name() {
        return name;
    }

    public String symbolString() {
        return name();
    }

    abstract public boolean isDefined();
    abstract public boolean isInitialized();

    public boolean isConstant() { return  false; }
    public boolean isParameter() { return  false; }
    public boolean isPrivate() { return isPrivate; }

    public ExprNode value() {
        throw new Error("Entity#value");
    }

    public TypeNode typeNode() {
        return typeNode;
    }

    public Type type() {
        return typeNode.type();
    }

    public long allocSize() {
        return type().allocSize();
    }

    public long alignment() {
        return type().alignment();
    }

    /**
     * 增加引用次数
     * @see net.loveruby.cflat.compiler.LocalResolver
     * */
    public void refered() {
        ++nRefered;
    }

    /**
     * 变量引用次数，用于定义的变量判断是否被使用
     * @see net.loveruby.cflat.entity.LocalScope 中检查（未使用的变量 回报 warnning）
     * @see net.loveruby.cflat.entity.ToplevelScope
     * */
    public boolean isRefered() {
        return (nRefered > 0);
    }

    public MemoryReference memref() {
        checkAddress();
        return memref;
    }

    public void setMemref(MemoryReference memref) {
        this.memref = memref;
    }

    public Operand address() {
        checkAddress();
        return address;
    }

    public void setAddress(MemoryReference memref) {
        this.address = memref;
    }

    public void setAddress(ImmediateValue imm){
        this.address = imm;
    }

    public void checkAddress() {
        if (memref == null && address == null){
            throw new Error("address did not resolved: " + name);
        }
    }

    public Location location(){
        return typeNode().location();
    }

    abstract public <T> T accept(EntityVisitor<T> visitor);

    public void dump(net.loveruby.cflat.ast.Dumper d) {
        d.printClass(this, location());
        _dump(d);
    }

    abstract protected void _dump(net.loveruby.cflat.ast.Dumper d);
}