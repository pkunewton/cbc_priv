package net.loveruby.cflat.ast;

import net.loveruby.cflat.asm.Label;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class CaseNode extends StmtNode {

    protected Label label;
    protected List<ExprNode> values;
    protected BlockNode body;

    public CaseNode(Location location, List<ExprNode> values, BlockNode body) {
        super(location);
        this.values = values;
        this.body = body;
        this.label = new Label();
    }

    public List<ExprNode> values() {
        return values;
    }

    /**
     * Parser.jj 955行， default 行为时， 构造函数中 values 没有元素
     */
    public boolean isDefault(){
        return values.isEmpty();
    }

    public BlockNode body() {
        return body;
    }

    public Label label() {
        return label;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printNodeList("values", values);
        d.printMember("body", body);
    }
}
