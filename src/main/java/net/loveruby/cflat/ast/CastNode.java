package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
public class CastNode extends ExprNode {

    protected ExprNode expr;
    protected TypeNode typeNode;

    public CastNode(TypeNode typeNode, ExprNode expr){
        this.expr = expr;
        this.typeNode = typeNode;
    }

    public CastNode(Type type, ExprNode expr){
        this(new TypeNode(type), expr);
    }

    public Type type() {
        return typeNode.type;
    }

    public TypeNode typeNode() {
        return typeNode;
    }

    public ExprNode expr() {
        return expr;
    }

    @Override
    public boolean isLvalue() {
        return expr.isLvalue();
    }

    @Override
    public boolean isAssignable() {
        return expr.isAssignable();
    }

    /**
     * @see net.loveruby.cflat.compiler.IRGenerator
     */
    public boolean isEffectiveCast(){
        return type().size() > expr.type().size();
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return typeNode.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("typeNode", typeNode);
        d.printMember("expr", expr);
    }
}
