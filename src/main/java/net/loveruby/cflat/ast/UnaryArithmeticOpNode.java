package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class UnaryArithmeticOpNode extends UnaryOpNode {

    /**
     * @see net.loveruby.cflat.compiler.TypeChecker.expectsScalarLHS
     * 整数变量没有影响
     * 指针变量用于保存 底层 类型的大小
     */
    protected long amount;

    public UnaryArithmeticOpNode(String operator, ExprNode expr) {
        super(operator, expr);
        amount = 1;
    }

    @Override
    public void setExpr(ExprNode expr) {
        super.setExpr(expr);
    }

    public long amount(){
        return this.amount;
    }

    /**
     * @see net.loveruby.cflat.compiler.TypeChecker.expectsScalarLHS
     * @param amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }
}
