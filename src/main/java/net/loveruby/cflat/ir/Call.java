package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.entity.Entity;
import net.loveruby.cflat.entity.Function;

import java.util.List;

/**
 * @author 刘科  2018/5/31
 */
public class Call extends Expr {

    protected Expr expr;
    protected List<Expr> args;

    public Call(Type type, Expr expr, List<Expr> args) {
        super(type);
        this.expr = expr;
        this.args = args;
    }

    public Expr expr() {
        return expr;
    }

    public List<Expr> args() {
        return args;
    }

    public long numArgs(){
        return args.size();
    }

    // Returns true if this funcall is NOT a function pointer call.
    // 是否是函数指针
    public boolean isStaticCall(){
        return (expr.getEntityForce() instanceof Function);
    }

    /**
     * Returns a function object which is refered by expression.
     * This method expects this is static function call (isStaticCall()).
     */
    public Function function(){
        Entity func = expr.getEntityForce();
        if(func == null){
            throw new Error("not a static call");
        }
        return (Function)func;
    }

    public <S, E> E accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("expr", expr);
        d.printMembers("args", args);
    }
}
