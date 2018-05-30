package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumpable;
import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.type.ParamTypeRefs;
import net.loveruby.cflat.type.TypeRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/5/30
 */
public class Params extends ParamSlots<Parameter> implements Dumpable{

    // ???为什么只能是不可边长参数
    public Params(Location location, List<Parameter> paramDescriptors) {
        super(location, paramDescriptors, false);
    }

    public List<Parameter> parameters(){
        return paramDescriptors;
    }

    public ParamTypeRefs paramTypeRefs(){
        List<TypeRef> typeRefs = new ArrayList<TypeRef>();
        for (Parameter parameter: parameters()) {
            typeRefs.add(parameter.typeNode.typeRef());
        }
        return new ParamTypeRefs(location(), typeRefs, vararg);
    }

    public void dump(Dumper d) {
        d.printNodeList("parameters", parameters());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Params) &&
                (equals((Params) obj));
    }

    public boolean equals(Params other) {
        return (other.vararg == vararg) &&
                (other.parameters().equals(parameters()));
    }
}
