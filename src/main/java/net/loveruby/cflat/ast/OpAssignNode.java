package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/27
 */
public class OpAssignNode extends AbstractAssignNode {

    protected String operator;

    public OpAssignNode(ExprNode lhs, String op, ExprNode rhs){
        super(lhs, rhs);
        this.operator = op;
    }

    public String operator() {
        return operator;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }
}
