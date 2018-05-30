package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.TypeNode;

import java.util.List;

/**
 * @author 刘科  2018/5/30
 */
public class UndefinedFunction extends Function {

    protected Params params;
    public UndefinedFunction(TypeNode typeNode, String name, Params params) {
        super(false, typeNode, name);
        this.params = params;
    }

    public boolean isDefined() {
        return false;
    }

    public List<Parameter> parameters() {
        return params.parameters();
    }

    // 源代码没有重写这个方法，函数没有初始化这个概念，只有是否定义
//    public boolean isInitialized() {
//        return false;
//    }

    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("isPrivate", isPrivate);
        d.printMember("typeNode", typeNode);
        d.printMember("params", params);
    }
}
