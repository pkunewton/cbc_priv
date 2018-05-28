package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class TypeNode extends Node {

    TypeRef typeRef;
    Type type;

    public TypeNode(TypeRef typeRef){
        super();
        this.typeRef = typeRef;
    }

    public TypeNode(Type type){
        super();
        this.type = type;
    }

    public Type type() {
        if(type == null){
            throw new Error("TypeNode is not resolved " + typeRef);
        }
        return type;
    }

    public void setType(Type type){
        if(type != null){
            throw new Error("TypeNode#setType called twice!");
        }
        this.type = type;
    }

    public TypeRef typeRef() {
        return typeRef;
    }

    public boolean isResolved(){
        return (type != null);
    }

    public Location location() {
        return (typeRef == null ? null : typeRef.location());
    }

    public void _dump(Dumper d) {
        d.printMember("typeRef", typeRef);
        d.printMember("type", type);
    }

    public TypeNode accept(ASTVisitor visitor){
        throw new Error("do not call TypeNode#accept");
    }
}
