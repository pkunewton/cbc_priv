package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.*;
import net.loveruby.cflat.ir.IR;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科 2018/02/12
 * */
public class AST extends Node {

    protected Location source;
    protected ToplevelScope scope;
    protected Declarations declarations;
    protected ConstantTable constantTable;

    public AST(Location source, Declarations declarations){
        super();
        this.source = source;
        this.declarations = declarations;
    }

    public Location location() {
        return source;
    }

    // 获取源码中定义的类型
    public List<TypeDefinition> types(){
        List<TypeDefinition> result = new ArrayList<TypeDefinition>();
        result.addAll(declarations.defstructs);
        result.addAll(declarations.defunions);
        result.addAll(declarations.typedefs);
        return result;
    }

    // 获取定义和声明的实体
    public List<Entity> entities(){
        List<Entity> result = new ArrayList<Entity>();
        result.addAll(declarations.defvars);
        result.addAll(declarations.defuns);
        result.addAll(declarations.vardecls);
        result.addAll(declarations.funcdecls);
        result.addAll(declarations.constants);
        return result;
    }
    // 声明的实体
    public List<Entity> declarations(){
        List<Entity> result = new ArrayList<Entity>();
        result.addAll(declarations.vardecls);
        result.addAll(declarations.funcdecls);
        return result;
    }
    // 定义的实体
    public List<Entity> definitions(){
        List<Entity> result = new ArrayList<Entity>();
        result.addAll(declarations.vardecls);
        result.addAll(declarations.funcdecls);
        result.addAll(declarations.constants);
        return result;
    }

    public List<Constant> constants(){
        return declarations.constants();
    }
    public List<DefinedVariable> definedVariables(){
        return declarations.defvars();
    }
    public List<DefinedFunction> definedFunctions(){
        return declarations.defuns();
    }

    /**
     * @see net.loveruby.cflat.compiler.LocalResolver
     * */
    public void setScope(ToplevelScope scope){
        if(this.scope != null){
            throw new Error("must not happen: ToplevelScope set twice");
        }
        this.scope = scope;
    }
    public ToplevelScope scope() {
        if(this.scope == null){
            throw new Error("must not happen: AST.scope is null");
        }
        return scope;
    }
    public void setConstantTable(ConstantTable constantTable){
        if(this.scope != null){
            throw new Error("must not happen: ConstantTable set twice");
        }
        this.constantTable = constantTable;
    }
    public ConstantTable constantTable() {
        if(this.constantTable == null){
            throw new Error("must not happen: AST.constantTable is null");
        }
        return constantTable;
    }

    public IR ir(){
        return new IR(source, declarations.defvars(), declarations.defuns(),
                declarations.funcdecls(), scope, constantTable);
    }

    protected void _dump(Dumper d) {
        d.printNodeList("variables", declarations.defvars());
        d.printNodeList("functions", declarations.defuns());
    }

    public void dumpTokens(PrintStream stream){
        for(CflatToken token : source.token()){
            printPair(token.kindName(), token.dumpImage(), stream);
        }
    }
    static final private int NUM_LEFT_COLUMNS = 24;
    private void printPair(String key, String value, PrintStream stream){
        stream.print(key);
        for (int n = NUM_LEFT_COLUMNS  - key.length(); n > 0; --n){
            stream.print(" ");
        }
        stream.println(value);
    }

    /**
     * @see net.loveruby.cflat.compiler.Compiler
     * 查找 main 函数中是否有可执行语句
     * */
    public ExprNode getSingleMainExpr(){
        StmtNode stmt = getSingleMainStmt();
        if(stmt == null){
            return null;
        }
        else if (stmt instanceof ExprStmtNode){
            return ((ExprStmtNode)stmt).expr();
        }
        else if (stmt instanceof ReturnNode){
            return ((ReturnNode)stmt).expr();
        }
        return null;

    }

    public StmtNode getSingleMainStmt(){
        for(DefinedFunction function : definedFunctions()){
            if(function.name().equals("main")){
                if(function.body().stmts().isEmpty()){
                    return null;
                }
                return function.body().stmts().get(0);
            }
        }
        return null;
    }
}
