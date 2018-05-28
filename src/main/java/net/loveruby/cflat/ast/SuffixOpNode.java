package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class SuffixOpNode extends UnaryArithmeticOpNode {

    public SuffixOpNode(String operator, ExprNode expr) {
        super(operator, expr);
    }

    public <S,E> E accept(ASTVisitor<S,E> visitor){
        return visitor.visit(this);
    }
}
