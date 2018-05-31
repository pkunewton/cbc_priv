package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科  2018/5/31
 */
public class Jump extends Stmt {

    protected Label label;

    public Jump(Location location, Label label) {
        super(location);
        this.label = label;
    }

    public Label label() {
        return label;
    }

    @Override
    public <S, E> S accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected void _dump(Dumper d) {
        d.printMember("label", label);
    }
}
