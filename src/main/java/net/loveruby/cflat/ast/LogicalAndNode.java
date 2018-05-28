package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class LogicalAndNode extends BinaryOpNode {

    public LogicalAndNode(ExprNode lhs, ExprNode rhs){
        super(lhs, "&&", rhs);
    }

    public <S,E> E accept(ASTVisitor<S,E> visitor){
        return visitor.visit(this);
    }
}
