package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
abstract public class TypeDefinition extends Node {

    protected TypeNode typeNode;
    protected String name;
    protected Location location;

    public TypeDefinition(Location location, TypeRef typeRef, String name){
        this.location = location;
        this.typeNode = new TypeNode(typeRef);
        this.name = name;
    }

    public String name(){
        return name;
    }

    public TypeNode typeNode() {
        return typeNode;
    }

    public Type type(){
        return typeNode().type();
    }

    public TypeRef typeRef(){
        return typeNode().typeRef();
    }

    public Location location() {
        return location;
    }

    abstract public Type definingType();
    abstract public <T> T accept(DeclarationVisitor<T> visitor);
}
