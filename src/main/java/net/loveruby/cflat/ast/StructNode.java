package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.StructType;
import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class StructNode extends CompositeTypeDefinition {

    public StructNode(Location location, TypeRef typeRef, String name, List<Slot> members) {
        super(location, typeRef, name, members);
    }

    public String kind() {
        return "struct";
    }

    public boolean isStruct(){
        return true;
    }

    public Type definingType() {
        return new StructType(name(), members(),location());
    }

    public <T> T accept(DeclarationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
