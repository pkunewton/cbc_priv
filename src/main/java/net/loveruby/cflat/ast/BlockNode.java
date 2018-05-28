package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.DefinedVariable;
import net.loveruby.cflat.entity.LocalScope;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class BlockNode extends StmtNode {

    protected List<DefinedVariable> variables;
    protected List<StmtNode> stmts;
    protected LocalScope scope;

    public BlockNode(Location location, List<DefinedVariable> variables, List<StmtNode> stmts) {
        super(location);
        this.variables = variables;
        this.stmts = stmts;
    }

    public List<DefinedVariable> variables() {
        return variables;
    }

    public List<StmtNode> stmts(){
        return stmts;
    }

    public StmtNode tailStmtNode(){
        if(stmts == null){
            return null;
        }
        return stmts.get(stmts.size() - 1);
    }

    public LocalScope scope() {
        return scope;
    }

    public void setScope(LocalScope scope) {
        this.scope = scope;
    }

    public <S, E> S accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printNodeList("variables", variables);
        d.printNodeList("stmts", stmts);
    }
}
