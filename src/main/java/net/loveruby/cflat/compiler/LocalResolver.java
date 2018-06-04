package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.*;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 刘科  2018/5/30
 * 解析 变量引用
 * 如 变量 i 究竟是指向全局变量 i， 还是静态变量 i，还是局部变量 i
 * 解析完成后，生成 根节点为 ToplevelScope 的 Scope 树，并将 所有的 变量VariableNode 节点 与 定义 Entity 联系起来
 * AST 中保存 ToplevelScope， DefinedFunction 中保存 LocalScope
 */
public class LocalResolver extends Visitor {

    private final LinkedList<Scope> scopeStacks;
    private final ConstantTable constantTable;
    private final ErrorHandler errorHandler;

    public LocalResolver(ErrorHandler errorHandler) {
        this.scopeStacks = new LinkedList<Scope>();
        this.constantTable = new ConstantTable();
        this.errorHandler = errorHandler;
    }

    private void resolve(ExprNode expr){
        expr.accept(this);
    }


    private void resolve(StmtNode stmt){
        stmt.accept(this);
    }

    public void resolve(AST ast) throws SemanticException {
        ToplevelScope toplevel = new ToplevelScope();
        scopeStacks.add(toplevel);
        for(Entity decl: ast.declarations()){
            toplevel.declareEntity(decl);
        }
        for(Entity entity: ast.definitions()){
            toplevel.declareEntity(entity);
        }
        resolveGvarInitializers(ast.definedVariables());
        resolveConstantValues(ast.constants());
        resolveFunctions(ast.definedFunctions());

        toplevel.checkReferences(errorHandler);
        if(errorHandler.errorOccured()){
            throw new SemanticException("compile failed");
        }

        ast.setScope(toplevel);
        ast.setConstantTable(constantTable);
    }

    private void resolveGvarInitializers(List<DefinedVariable> gvars){
        for(DefinedVariable variable: gvars){
            if(variable.hasInitializer()){
                resolve(variable.initializer());
            }
        }
    }

    private void resolveConstantValues(List<Constant> constants){
        for(Constant constant: constants){
            resolve(constant.value());
        }
    }

    private void resolveFunctions(List<DefinedFunction> functions){
        for(DefinedFunction function: functions){
            pushScope(function.parameters());
            resolve(function.body());
            popScope();
        }

    }

    public Void visit(BlockNode node){
        pushScope(node.variables());
        super.visit(node);
        node.setScope(popScope());
        return null;
    }

    private void pushScope(List<? extends DefinedVariable> variables){
        LocalScope scope = new LocalScope(currentScope());
        for(DefinedVariable variable: variables){
            if (scope.isDefinedLocally(variable.name())){
                error(variable.location(),
                        "duplicated variable in scope: " + variable.name());
            }else {
                scope.defineVariable(variable);
            }
        }
        scopeStacks.addLast(scope);
    }

    private LocalScope popScope(){
        return (LocalScope) scopeStacks.removeLast();
    }

    private Scope currentScope(){
        return scopeStacks.getLast();
    }

    public Void visit(StringLiteralNode node){
        node.setEntry(constantTable.intern(node.value()));
        return null;
    }

    public Void visit(VariableNode node) {
        try {
            Entity entity = currentScope().get(node.name());
            entity.refered();
            node.setEntity(entity);
        } catch (SemanticException e) {
            error(node, e.getMessage());
        }
        return null;
    }

    private void error(Node node, String message){
        errorHandler.error(node.location(), message);
    }

    private void error(Location location, String message){
        errorHandler.error(location, message);
    }

}
