package net.loveruby.cflat.ast;

/**
 * Created by Administrator on 2018/2/9.
 */
public class ReturnNode extends StmtNode {

    protected ExprNode expr;

    public ExprNode expr() {
        return expr;
    }
}
