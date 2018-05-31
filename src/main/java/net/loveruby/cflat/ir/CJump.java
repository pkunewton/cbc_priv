package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科  2018/5/31
 */
public class CJump extends Stmt {

    protected Expr cond;
    protected Label thenLabel, elseLabel;

    public CJump(Location location, Expr cond, Label thenLabel, Label elseLabel) {
        super(location);
        this.cond = cond;
        this.thenLabel = thenLabel;
        this.elseLabel = elseLabel;
    }

    public Expr cond() {
        return cond;
    }

    public Label thenLabel() {
        return thenLabel;
    }

    public Label elseLabel() {
        return elseLabel;
    }

    public <S, E> S accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printMember("thenLabel", thenLabel);
        d.printMember("elseLabel", elseLabel);
    }
}
