package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.Symbol;
import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.ExprNode;
import net.loveruby.cflat.ast.TypeNode;
import net.loveruby.cflat.ir.Expr;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科  2018/5/30
 */
public class DefinedVariable extends Variable {

    protected ExprNode initializer;
    protected Expr ir;
    /**
     * @see net.loveruby.cflat.entity.ToplevelScope staticLocalVariables方法
     * 标记 名字相同 静态变量 的声明次序
     */
    protected long sequence;
    protected Symbol symbol;

    public DefinedVariable(boolean isPrivate, TypeNode typeNode, String name, ExprNode initializer) {
        super(isPrivate, typeNode, name);
        this.initializer = initializer;
        sequence = -1;
    }

    static private long tmpSeq = 0;

    static public DefinedVariable tmp(Type type){
        return new DefinedVariable(false, new TypeNode(type), "@tmp" + tmpSeq++, null);
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public String symbolString() {
        return (sequence < 0 ? name : name + "." + sequence);
    }

    public boolean isDefined() {
        return true;
    }

    public boolean hasInitializer(){
        return (initializer != null);
    }
    public boolean isInitialized() {
        return hasInitializer();
    }

    public ExprNode initializer() {
        return initializer;
    }

    public void setInitializer(ExprNode initializer) {
        this.initializer = initializer;
    }

    public Expr ir() {
        return ir;
    }

    public void setIr(Expr ir) {
        this.ir = ir;
    }

    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("isPrivate", isPrivate);
        d.printMember("typeNode", typeNode);
        d.printMember("initializer", initializer);
    }
}
