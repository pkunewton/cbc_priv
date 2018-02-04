package net.loveruby.cflat.utils;

import net.loveruby.cflat.ast.Location;

import java.io.PrintStream;

/**
 * @author 刘科
 * @see net.loveruby.cflat.ast.Location
 * 结合 Location 类 打印编译错误的位置
 */
public class ErrorHandler {

    protected String programId;
    protected PrintStream stream;
    protected long nError;
    protected long nWarning;

    public ErrorHandler(String programId){
        this.programId =programId;
        this.stream = System.err;
    }

    public ErrorHandler(String programId, PrintStream stream){
        this.programId = programId;
        this.stream = new PrintStream(stream);
    }

    public void error(Location location, String msg){
        error(location.toString() + ": " + msg);
    }

    public void error(String msg){
        stream.println(programId + ": error: " + msg);
        nError++;
    }

    public void warn(Location location, String msg){
        warn(location.toString() + ": " + msg);
    }

    public void warn(String msg){
        stream.println(programId + ": warning: " + msg);
        nWarning++;
    }

    public boolean errorOccured(){
        return (nError > 0);
    }

}
