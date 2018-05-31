package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科  2018/5/31
 */
public class LabelStmt extends Stmt {

    protected Label label;

    public LabelStmt(Location location, Label label) {
        super(location);
        this.label = label;
    }

    public Label label(){
        return label;
    }

    public <S, E> S accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("label", label);
    }
}
