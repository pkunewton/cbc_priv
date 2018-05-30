package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.BlockNode;
import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;
import net.loveruby.cflat.ir.Stmt;

import java.util.List;

/**
 * @author 刘科  2018/5/30
 */
public class DefinedFunction extends Function {

    protected Params params;
    protected BlockNode body;
    protected LocalScope localScope;
    protected List<Stmt> ir;

    public DefinedFunction(boolean isPrivate, TypeNode typeNode, String name, Params params, BlockNode body) {
        super(isPrivate, typeNode, name);
        this.params = params;
        this.body = body;
    }

    public boolean isDefined() {
        return true;
    }

    public BlockNode body() {
        return body;
    }

    public List<Parameter> parameters() {
        return params.parameters();
    }

    public List<Stmt> ir(){
        return ir;
    }

    public void setLocalScope(LocalScope localScope) {
        this.localScope = localScope;
    }

    public void setIr(List<Stmt> ir) {
        this.ir = ir;
    }

    public LocalScope lvarScope() {
        return body().scope();
    }

    /**
     * Returns function local variables.
     * Does NOT include paramters.
     * Does NOT include static local variables.
     * 函数作用域内的本地变量，不包括函数参数和静态变量
     */
    public List<DefinedVariable> localVariables(){
        return localScope.allLocalVariables();
    }

    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("isPrivate", isPrivate);
        d.printMember("params", params);
        d.printMember("body", body);
    }
}
