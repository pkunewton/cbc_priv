package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科  2018/5/30
 */
public class Parameter extends DefinedVariable {

    public Parameter(TypeNode typeNode, String name) {
        super(false, typeNode, name, null);
    }

    @Override
    public boolean isParameter() {
        return true;
    }

    public void _dump(Dumper d){
        d.printMember("name", name);
        d.printMember("typeNode", typeNode);
    }
}
