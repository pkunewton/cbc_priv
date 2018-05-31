package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.entity.DefinedFunction;
import net.loveruby.cflat.entity.DefinedVariable;

import java.io.PrintStream;
import java.util.List;

/**
 * @author 刘科  2018/5/31
 */
public class Dumper {

    PrintStream stream;
    private int numIndent;

    public Dumper(PrintStream stream) {
        this.stream = stream;
        this.numIndent = 0;
    }

    public void printClass(Object object){
        printIndent();
        stream.println("<<" + object.getClass().getSimpleName() + ">>");
    }

    public void printClass(Object object, Location location){
        printIndent();
        stream.println("<<" + object.getClass().getSimpleName() + ">>(" + location + ")");
    }

    public void printMember(String name, int memb){
        printPair(name, "" + memb);
    }

    public void printMember(String name, long memb){
        printPair(name, "" + memb);
    }

    public void printMember(String name, boolean memb){
        printPair(name, "" + memb);
    }

    public void printMember(String name, String memb){
        printPair(name, memb);
    }

    public void printMember(String name, Label memb){
        printPair(name, Integer.toHexString(memb.hashCode()));
    }

    public void printMember(String name, net.loveruby.cflat.asm.Type memb){
        printPair(name, memb.toString());
    }

    public void printMember(String name, net.loveruby.cflat.type.Type memb){
        printPair(name, memb.toString());
    }

    private void printPair(String name, String value){
        printIndent();
        stream.println(name + ": " + value);
    }

    public void printMember(String name, Dumpable memb){
        printIndent();
        if(memb == null){
            stream.println(name + ": null");
        }else {
            stream.println(name + ": ");
            indent();
            memb.dump(this);
            unindent();
        }
    }

    public void printMembers(String name, List<? extends Dumpable> membs){
        printIndent();
        stream.println(name + ": ");
        indent();
        for(Dumpable d: membs){
            d.dump(this);
        }
        unindent();
    }

    // 打印变量
    public void printVars(String name, List<DefinedVariable> variables){
        printIndent();
        stream.println(name + ":");
        indent();
        for(DefinedVariable variable: variables){
            printClass(variable, variable.location());
            printMember("name", variable.name());
            printMember("isPrivate", variable.isPrivate());
            printMember("type", variable.type());
            printMember("initializer", variable.ir());
        }
        unindent();
    }

    // 打印函数
    public void printFuncs(String name, List<DefinedFunction> functions){
        printIndent();
        stream.println(name + ":");
        indent();
        for (DefinedFunction function: functions) {
            printClass(function, function.location());
            printMember("name", function.name());
            printMember("isPrivate", function.isPrivate());
            printMember("type", function.type());
            printMembers("body", function.ir());
        }
        unindent();
    }


    // 缩进管理
    private void indent(){
        numIndent++;
    }

    private void unindent(){
        numIndent--;
    }

    private static final String indentString = "    ";

    private void printIndent(){
        int n = numIndent;
        for( ; n > 0; n-- ){
            stream.print(indentString);
        }
    }
}
