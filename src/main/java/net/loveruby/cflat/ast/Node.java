package net.loveruby.cflat.ast;

import java.io.PrintStream;

/**
 * @author 刘科 2018/02/07
 */
abstract public class Node implements Dumpable {

    public Node(){}

    abstract public Location location();

    abstract protected void  _dump(Dumper d);

    public void dump(){
        dump(System.out);
    }

    public void dump(PrintStream stream){
        dump(new Dumper(stream));
    }

    public void dump(Dumper d) {
        d.printClass(this, location());
        _dump(d);
    }

}
