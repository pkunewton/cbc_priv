package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class Slot extends Node {

    protected TypeNode typeNode;
    protected String name;
    protected long offset;

    public Slot(TypeNode typeNode, String name){
        this.typeNode = typeNode;
        this.name = name;
        this.offset = Type.sizeUnknown;
    }

    public TypeNode typeNode() {
        return typeNode;
    }

    public TypeRef typeRef(){
        return typeNode.typeRef();
    }

    public Type type(){
        return typeNode.type();
    }

    public String name(){
        return name;
    }

    public long size(){
        return type().size();
    }

    public long allocSize(){
        return type().allocSize();
    }

    public long alignment(){
        return type().alignment();
    }

    public long offset(){
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public Location location() {
        return typeNode.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("typeNode", typeNode);
    }
}
