package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.ConstantEntry;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class StringLiteralNode extends LiteralNode {

    protected String value;
    protected ConstantEntry entry;

    public StringLiteralNode(Location location, TypeRef typeRef, String value) {
        super(location, typeRef);
        this.value = value;
    }

    public String value(){
        return value;
    }

    public ConstantEntry entry() {
        return entry;
    }

    public void setEntry(ConstantEntry entry) {
        this.entry = entry;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("value", value);
    }
}
