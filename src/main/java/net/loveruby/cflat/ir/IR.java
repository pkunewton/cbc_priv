package net.loveruby.cflat.ir;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.entity.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/5/31
 * */
public class IR {

    Location source;
    List<DefinedVariable> defvars;
    List<DefinedFunction> defuns;
    List<UndefinedVariable> vardecls;
    List<UndefinedFunction> funcdecls;
    ToplevelScope scope;
    ConstantTable constantTable;
    List<DefinedVariable> gvars;        // 有初值
    List<DefinedVariable> comms;        // 无初值

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

    public String fileName(){
        return source.sourceName();
    }

    public Location location() {
        return source;
    }

    public List<DefinedVariable> definedVariables() {
        return defvars;
    }

    public boolean isFunctionDefined(){
        return !defuns.isEmpty();
    }

    public List<DefinedFunction> definedFunctions() {
        return defuns;
    }

    public ToplevelScope scope() {
        return scope;
    }

    public List<Function> allFunctions(){
        List<Function> result = new ArrayList<Function>();
        result.addAll(defuns);
        result.addAll(funcdecls);
        return result;
    }

    // a list of all defined/declared global-scope variables
    // 所有全局变量（包含 静态变量）
    public List<Variable> allGlobalVarialbes(){
        return scope.allGlobalVariables();
    }

    public boolean isGlobalVariableDefined(){
        return !definedGlobalVariables().isEmpty();
    }

    /** Returns the list of global variables.
     *  A global variable is a variable which has
     *  global scope and is initialized.
     *  全局变量 并在定义时初始化
     *  */
    public List<DefinedVariable> definedGlobalVariables(){
        if(gvars == null){
            initVariables();
        }
        return gvars;
    }

    public boolean isCommonSymbolDefined(){
        return ! definedCommonSymbols().isEmpty();
    }

    /** Returns the list of common symbols.
     *  A common symbol is a variable which has
     *  global scope and is not initialized.
     *  全局变量，但是没有初始化
     *  */
    public List<DefinedVariable> definedCommonSymbols(){
        if(comms == null){
            initVariables();
        }
        return comms;
    }

    private void initVariables(){
        gvars = new ArrayList<DefinedVariable>();
        comms = new ArrayList<DefinedVariable>();
        for(DefinedVariable variable: scope.definedGlobalScopeVariables()){
            (variable.hasInitializer() ? gvars : comms).add(variable);
        }
    }

    public ConstantTable constantTable() {
        return constantTable;
    }

    public boolean isStringLiteralDefined(){
        return !constantTable().isEmpty();
    }

    public void dump(){
        dump(System.out);
    }

    public void dump(PrintStream stream){
        Dumper d = new Dumper(stream);
        d.printClass(this, source);
        d.printVars("variables", defvars);
        d.printFuncs("functions", defuns);
    }
}
