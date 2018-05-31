package net.loveruby.cflat.entity;

import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 刘科  2018/5/30
 */
public class LocalScope extends Scope {

    protected Scope parent;
    protected Map<String, DefinedVariable> variables;

    public LocalScope(Scope parent) {
        super();
        this.parent = parent;
        parent.addChild(this);
        variables = new LinkedHashMap<String, DefinedVariable>();
    }


    public boolean isToplevel() {
        return false;
    }

    public ToplevelScope toplevel() {
        return parent.toplevel();
    }

    public Scope parent() {
        return parent;
    }

    public List<LocalScope> children(){
        return children;
    }

    public boolean isDefinedLocally(String name){
        return variables.containsKey(name);
    }

    // Define variable in this scope.
    public void defineVariable(DefinedVariable variable) {
        if(variables.containsKey(variable.name())){
            throw new Error("duplicated variable: " + variable.name());
        }
        variables.put(variable.name(), variable);
    }

    // 临时变量
    public DefinedVariable allocateTmp(Type type){
        DefinedVariable variable = DefinedVariable.tmp(type);
        defineVariable(variable);
        return variable;
    }

    public Entity get(String name) throws SemanticException {
        Variable variable = variables.get(name);
        if(variable != null){
            return variable;
        }else {
            return parent().get(name);
        }
    }

    /**
     * Returns all local variables in this scope.            本定义域的所有变量
     * The result DOES includes all nested local variables,  包括嵌套域中的变量
     * while it does NOT include static local variables.     不含 静态变量
     */
    public List<DefinedVariable> allLocalVariables(){
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for(LocalScope scope: allLocalScopes()){
            result.addAll(scope.localVariables());
        }
        return result;
    }

    /**
     * Returns local variables defined in this scope. 本定义域定义的变量
     * Does NOT includes children's local variables.  不含子定义域的变量
     * Does NOT include static local variables.       不含静态变量
     */
    public List<DefinedVariable> localVariables(){
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for (DefinedVariable variable : variables.values()) {
            if (!variable.isPrivate()) {
                result.add(variable);
            }
        }
        return result;
    }

    /**
     * Returns all static local variables defined in this scope.
     */
    public List<DefinedVariable> staticLocalVariables(){
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for(LocalScope scope: allLocalScopes()){
            for(DefinedVariable variable: scope.variables.values()){
                if(variable.isPrivate()){
                    result.add(variable);
                }
            }
        }
        return result;
    }

    // Returns a list of all child scopes including this scope.
    protected List<LocalScope> allLocalScopes(){
        List<LocalScope> buffer = new ArrayList<LocalScope>();
        collectScope(buffer);
        return buffer;
    }

    protected void collectScope(List<LocalScope> buffer){
        buffer.add(this);
        for(LocalScope scope: children){
            collectScope(buffer);
        }
    }


    public void checkReferences(ErrorHandler h){
        for(DefinedVariable variable: variables.values()){
            if(!variable.isRefered()){
                h.warn(variable.location(), "unused variable: " + variable.name());
            }
        }
        for(LocalScope scope: children){
            checkReferences(h);
        }
    }
}
