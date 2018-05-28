package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;
import net.loveruby.cflat.type.UserType;
import net.loveruby.cflat.type.UserTypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class TypedefNode extends TypeDefinition{

    protected TypeNode real;

    public TypedefNode(Location location, TypeRef typeRef, String name) {
        super(location, new UserTypeRef(name), name);
        this.real = new TypeNode(typeRef);
    }

    public boolean isUserType(){
        return true;
    }

    public TypeNode realTypeNode(){
        return real;
    }

    public Type realType(){
        return realTypeNode().type();
    }

    public TypeRef realTypeRef(){
        return realTypeNode().typeRef();
    }

    public Type definingType() {
        return new UserType(name(), realTypeNode(), location());
    }

    public <T> T accept(DeclarationVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("typeNode", typeNode);
    }
}
