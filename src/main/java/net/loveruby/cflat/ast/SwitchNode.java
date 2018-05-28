package net.loveruby.cflat.ast;

import java.util.List;

/**
 * @author 刘科 2018/5/28.
 */
public class SwitchNode extends StmtNode {

    protected ExprNode cond;
    protected List<CaseNode> cases;

    public SwitchNode(Location location, ExprNode cond, List<CaseNode> cases) {
        super(location);
        this.cond = cond;
        this.cases = cases;
    }

    public ExprNode cond() {
        return cond;
    }

    public List<CaseNode> cases() {
        return cases;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("cond", cond);
        d.printNodeList("cases", cases);
    }
}
