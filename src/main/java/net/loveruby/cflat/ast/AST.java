package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.ConstantTable;
import net.loveruby.cflat.entity.ToplevelScope;

/**
 * @author 刘科 2018/02/07
 * */
public class AST extends Node {

    protected Location source;
    protected ToplevelScope scope;
    protected Declarations declarations;
    protected ConstantTable constantTable;


    public Location location() {
        return null;
    }

    public void _dump(Dumper d) {

    }
}
