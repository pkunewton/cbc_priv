package net.loveruby.cflat.ir;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.entity.*;

import java.util.List;

/**
 * @author 刘科 2018/02/12
 * */
public class IR {

    Location source;
    List<DefinedVariable> defvars;
    List<DefinedFunction> defuns;
    List<UndefinedVariable> vardecls;
    List<UndefinedFunction> funcdecls;
    ToplevelScope scope;
    ConstantTable constantTable;
    List<DefinedVariable> gvars;        // cache
    List<DefinedVariable> comms;        // cache

    public IR(Location source, List<DefinedVariable> defvars, List<DefinedFunction> defuns,
              List<UndefinedFunction> funcdecls, ToplevelScope scope, ConstantTable constantTable){
        super();
        this.source = source;
        this.defvars = defvars;
        this.defuns = defuns;
        this.funcdecls = funcdecls;
        this.constantTable = constantTable;
        this.scope = scope;
    }

}
