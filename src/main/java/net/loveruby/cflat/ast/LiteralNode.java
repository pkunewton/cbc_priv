package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 * 字面量 也是一个 terminal 节点
 */
abstract public class LiteralNode extends ExprNode {

    protected Location location;
    protected TypeNode typeNode;

    public LiteralNode(Location location, TypeRef typeRef){
        super();
        this.location = location;
        this.typeNode = new TypeNode(typeRef);
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public Type type() {
        return typeNode.type();
    }

    public TypeNode typeNode() {
        return typeNode;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
