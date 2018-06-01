package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
public class ImmediateValue extends Operand {

    protected Literal expr;

    public ImmediateValue(long n) {
        this.expr = new IntegerLiteral(n);
    }

    public ImmediateValue(Literal expr) {
        this.expr = expr;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ImmediateValue)) return false;
        ImmediateValue imm = (ImmediateValue)other;
        return expr.equals(imm.expr);
    }

    public String toSource(SymbolTable table) {
        return "$" + expr.toSource(table);
    }

    public String dump() {
        return "(ImmediateValue " + expr.dump() + ")";
    }

    public void collectStatistics(Statistics stats) {

    }
}
