package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class SizeofExprNode extends ExprNode {

    protected ExprNode expr;
    protected TypeNode type;

    public SizeofExprNode(ExprNode expr, TypeRef typeRef){
        this.expr = expr;
        this.type = new TypeNode(typeRef);
    }

    public ExprNode expr() {
        return expr;
    }

    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

    public Type type() {
        return this.type.type();
    }

    public TypeNode typeNode() {
        return this.type;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return expr.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("expr", expr);
    }
}
