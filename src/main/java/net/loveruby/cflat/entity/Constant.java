package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Dumper;
import net.loveruby.cflat.ast.ExprNode;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科 on 2018/2/13.
 */
public class Constant extends Entity {

    private TypeNode type;
    private ExprNode value;
    private String name;

    public Constant(String name, ExprNode value, TypeNode typeNode) {
        super(name, true, typeNode);
        this.value = value;
    }

    @Override
    public ExprNode value() {
        return value;
    }

    public boolean isAssinable() { return false; }

    @Override
    public boolean isDefined() {
        return true;
    }
    @Override
    public boolean isInitialized() {
        return true;
    }
    @Override
    public boolean isConstant() { return true; }

    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("type", type);
        d.printMember("value", value);
    }
}
