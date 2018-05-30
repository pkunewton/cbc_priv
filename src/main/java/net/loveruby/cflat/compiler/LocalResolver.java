package net.loveruby.cflat.compiler;

import net.loveruby.cflat.entity.ConstantTable;
import net.loveruby.cflat.entity.Scope;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.LinkedList;

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
}
