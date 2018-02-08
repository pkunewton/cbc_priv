package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 刘科 2018/02/08
 */
public class Declarations {

    Set<DefinedFunction> defuns = new LinkedHashSet<DefinedFunction>();
    Set<UndefinedFunction> funcdecls = new LinkedHashSet<UndefinedFunction>();
    Set<DefinedVariable> defvars = new LinkedHashSet<DefinedVariable>();
    Set<UndefinedVariable> vardecls = new LinkedHashSet<UndefinedVariable>();
    Set<Constant> constants = new LinkedHashSet<Constant>();
    Set<StructNode> defstructs = new LinkedHashSet<StructNode>();
    Set<UnionNode> defunions = new LinkedHashSet<UnionNode>();
    Set<TypedefNode> typedefs = new LinkedHashSet<TypedefNode>();

    public void add(Declarations declarations){
        defuns.addAll(declarations.defuns);
        funcdecls.addAll(declarations.funcdecls);
        defvars.addAll(declarations.defvars);
        vardecls.addAll(declarations.vardecls);
        constants.addAll(declarations.constants);
        defstructs.addAll(declarations.defstructs);
        defunions.addAll(declarations.defunions);
        typedefs.addAll(declarations.typedefs);
    }

    public void addDefvar(DefinedVariable var){
        defvars.add(var);
    }

    public void addDefvars(List<DefinedVariable> vars){
        defvars.addAll(vars);
    }

    public List<DefinedVariable> defvars(){
        return new ArrayList<DefinedVariable>(defvars);
    }

    public void addVardecl(UndefinedVariable var){
        vardecls.add(var);
    }

    public List<UndefinedVariable> vardecls(){
        return new ArrayList<UndefinedVariable>(vardecls);
    }

    public void addDefun(DefinedFunction function){
        defuns.add(function);
    }

    public List<DefinedFunction> defuns(){
        return new ArrayList<DefinedFunction>(defuns);
    }

    public void addFuncdecl(UndefinedFunction function){
        funcdecls.add(function);
    }

    public List<UndefinedFunction> funcdecls(){
        return new ArrayList<UndefinedFunction>(funcdecls);
    }

    public void addConstant(Constant constant){
        constants.add(constant);
    }

    public List<Constant> constants(){
        return new ArrayList<Constant>(constants);
    }

    public void addDefstruct(StructNode node){
        defstructs.add(node);
    }

    public List<StructNode> defstructs(){
        return new ArrayList<StructNode>(defstructs);
    }

    public void addDefunion(UnionNode node){
        defunions.add(node);
    }

    public List<UnionNode> defunions(){
        return new ArrayList<UnionNode>(defunions);
    }

    public void addTypedef(TypedefNode node){
        typedefs.add(node);
    }

    public List<TypedefNode> typedefs(){
        return new ArrayList<TypedefNode>(typedefs);
    }

}
