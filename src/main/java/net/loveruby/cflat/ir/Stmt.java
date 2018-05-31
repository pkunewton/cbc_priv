package net.loveruby.cflat.ir;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科  2018/5/31
 */
abstract public class Stmt implements Dumpable {

    protected Location location;

    public Stmt(Location location) {
        this.location = location;
    }

    public Location location() {
        return location;
    }

    abstract public <S,E> S accept(IRVisitor<S,E> visitor);

    public void dump(Dumper d){
        d.printClass(this, location);
        _dump(d);
    }

    abstract protected void _dump(Dumper d);
}
