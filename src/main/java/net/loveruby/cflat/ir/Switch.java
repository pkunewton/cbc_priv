package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.Location;

import java.util.List;

/**
 * @author 刘科  2018/5/31
 */
public class Switch extends Stmt {

    protected Expr cond;
    protected List<Case> cases;
    protected Label defaultLabel, endLabel;

    public Switch(Location location, Expr cond, List<Case> cases, Label defaultLabel, Label endLabel) {
        super(location);
        this.cond = cond;
        this.cases = cases;
        this.defaultLabel = defaultLabel;
        this.endLabel = endLabel;
    }

    public Expr cond() {
        return cond;
    }

    public List<Case> cases() {
        return cases;
    }

    public Label defaultLabel() {
        return defaultLabel;
    }

    public Label endLabel() {
        return endLabel;
    }

    public <S, E> S accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printMembers("cases", cases);
        d.printMember("defaultLabel", defaultLabel);
        d.printMember("endLabel", endLabel);
    }
}
