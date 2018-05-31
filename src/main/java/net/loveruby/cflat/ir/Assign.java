package net.loveruby.cflat.ir;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科  2018/5/31
 */
public class Assign extends Stmt {

    protected Expr lhs, rhs;

    public Assign(Location location, Expr lhs, Expr rhs) {
        super(location);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expr lhs() {
        return lhs;
    }

    public Expr rhs() {
        return rhs;
    }

    public <S, E> S accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("lhs", lhs);
        d.printMember("rhs", rhs);
    }
}
