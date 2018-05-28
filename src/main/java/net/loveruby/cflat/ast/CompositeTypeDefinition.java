package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.TypeRef;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
abstract public class CompositeTypeDefinition extends TypeDefinition {

    protected List<Slot> members;

    public CompositeTypeDefinition(Location location, TypeRef typeRef, String name, List<Slot> members) {
        super(location, typeRef, name);
        this.members = members;
    }

    public boolean isompositeType(){
        return true;
    }

    abstract public String kind();

    public List<Slot> members() {
        return members;
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printNodeList("members", members);
    }
}
