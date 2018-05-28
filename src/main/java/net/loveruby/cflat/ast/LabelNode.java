package net.loveruby.cflat.ast;

/**
 * @author 刘科 2018/5/28
 */
public class LabelNode extends StmtNode {

    protected String name;
    protected StmtNode stmt;

    public LabelNode(Location location, String name, StmtNode stmt) {
        super(location);
        this.name = name;
        this.stmt = stmt;
    }

    public String name() {
        return name;
    }

    public StmtNode stmt() {
        return stmt;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("name", name);
        d.printMember("stmt", stmt);
    }
}
