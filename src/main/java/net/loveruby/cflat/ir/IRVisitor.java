package net.loveruby.cflat.ir;

/**
 * @author 刘科  2018/5/31
 */
public interface IRVisitor<S,E> {

    public S visit(ExprStmt s);
    public S visit(Assign s);
    public S visit(Jump s);
    public S visit(CJump s);
    public S visit(Switch s);
    public S visit(LabelStmt s);
    public S visit(Return s);

    public E visit(Uni e);
    public E visit(Bin e);
    public E visit(Addr e);
    public E visit(Mem e);
    public E visit(Var e);
    public E visit(Str e);
    public E visit(Int e);
    public E visit(Call e);
}
