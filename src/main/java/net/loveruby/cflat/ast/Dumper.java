package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;
import net.loveruby.cflat.utils.TextUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * @author 刘科 2018/02/07
 */
public class Dumper {

    protected int nIndent; // 缩进长度
    protected PrintStream stream;

    public Dumper(PrintStream s){
        this.stream = s;
        this.nIndent = 0;
    }

    static final String indent = "  ";
    /**
     * 打印缩进
     * */
    protected void printIndent(){
        int n = nIndent;
        while (n >0){
            stream.print(indent);
            --n;
        }
    }

    // 增减缩进
    protected void indent() { ++nIndent; }
    protected void unindent() { --nIndent; }

    public void printPair(String name, String value){
        printIndent();
        stream.println(name + ": " +value);
    }

    public void printClass(Object object, Location location){
        printIndent();
        stream.println("<<" + object.getClass().getSimpleName() + ">>(" + location + ")");
    }

    public void printMember(String name, Dumpable dumpable){
        printIndent();
        if(dumpable == null){
            stream.println(name + ": null");
        }else {
            stream.println(name + ":");
            indent();
            dumpable.dump(this);
            unindent();
        }
    }

    /**
     * List<? extends Dumpable> 不能添加数据，因为不能确定 List 元素类型，可能是Dumpable的任何子类；
     *                          主要用于读取数据，读取的数据可以向下转型到 Dumpable
     * List<? super Dumpable> 读取数据是只能保证读取的是Object，不能确定类型；可以添加 Dumpable 及其子类
     * */
    public void printNodeList(String name, List<? extends Dumpable> nodes){
        printIndent();
        stream.println(name + ":");
        indent();
        for (Dumpable node : nodes) {
            node.dump(this);
        }
        unindent();
    }

    public void printMember(String name, int n){
        printPair(name, "" + n);
    }

    public void printMember(String name, long n){
        printPair(name, "" + n);
    }

    public void printMember(String name, boolean b){
        printPair(name, "" + b);
    }

    public void printMember(String namem, TypeRef typeRef){
        printPair(namem, typeRef.toString());
    }

    public void printMember(String name, Type type){
        printPair(name, (type == null ? "null" : type.toString()));
    }

    public void printMember(String name, String s, boolean isResolved){
        printIndent();
        stream.println(name + ": " + TextUtils.dumpString(s)
                + (isResolved ? " (resolved)" : ""));
    }

    public void printMember(String name, String s){
        printMember(name, s, false);
    }

    public void printMember(String name, TypeNode typeNode){
        printIndent();
        stream.println(name + ": " + typeNode.typeRef()
                + (typeNode.isResolved() ? " resolved" : ""));
    }
}
