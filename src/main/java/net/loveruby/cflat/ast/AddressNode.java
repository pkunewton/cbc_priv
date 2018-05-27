package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/27
 */
public class AddressNode extends ExprNode{
    final ExprNode expr;
    Type type;

    public AddressNode(ExprNode expr){
        this.expr = expr;
    }

    public ExprNode expr() {
        return expr;
    }

    public Type type() {
        if(type == null){
            throw new Error("type is null");
        }
        return type;
    }

    /**
     * 设置节点的类型
     * 在 DereferenceChecker 表达式有效性检查中 设置
     * @see net.loveruby.cflat.compiler.DereferenceChecker
     */
    public void setType(Type type){
        if(type != null){
            throw new Error("type set twice");
        }
        this.type = type;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {

        return expr.location();
    }

    protected void _dump(Dumper d) {
        if (type != null) {
            d.printMember("type", type);
        }
        d.printMember("expr", expr);
    }
}
