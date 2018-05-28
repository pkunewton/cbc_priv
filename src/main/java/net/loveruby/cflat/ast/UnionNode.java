package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;
import net.loveruby.cflat.type.UnionType;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class UnionNode extends CompositeTypeDefinition {

    public UnionNode(Location location, TypeRef typeRef, String name, List<Slot> members) {
        super(location, typeRef, name, members);
    }

    public String kind() {
        return "union";
    }

    public boolean isUnion(){
        return true;
    }

    public Type definingType() {
        return new UnionType(name(), members(), location());
    }

    public <T> T accept(DeclarationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
