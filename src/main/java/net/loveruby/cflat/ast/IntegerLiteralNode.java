package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class IntegerLiteralNode extends LiteralNode {

    protected long value;

    public IntegerLiteralNode(Location location, TypeRef typeRef, long value){
        super(location, typeRef);
        this.value = value;
    }

    public long value(){
        return value;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("typeNode", typeNode);
        d.printMember("value", value);
    }
}
