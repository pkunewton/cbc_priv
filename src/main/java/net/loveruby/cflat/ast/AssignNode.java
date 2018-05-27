package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/27
 */
public class AssignNode extends AbstractAssignNode {

    public AssignNode(ExprNode lhs, ExprNode rhs){
        super(lhs, rhs);
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }
}
