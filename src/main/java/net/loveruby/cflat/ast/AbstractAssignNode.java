package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/27
 */
abstract public class AbstractAssignNode extends ExprNode {

    ExprNode lhs, rhs;

    public AbstractAssignNode(ExprNode lhs, ExprNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Type type() {
        return lhs.type();
    }

    public ExprNode lhs() {
        return lhs;
    }

    public ExprNode rhs() {
        return rhs;
    }

    public void setLHS(ExprNode expr) {
        this.lhs = expr;
    }

    public void setRHS(ExprNode expr) {
        this.rhs = expr;
    }

    @Override
    public Location location() {
        return lhs.location();
    }

    public void _dump(Dumper d){
        d.printMember("lhs", lhs);
        d.printMember("rhs", rhs);
    }
}
