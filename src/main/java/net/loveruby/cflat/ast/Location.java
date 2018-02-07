package net.loveruby.cflat.ast;

import net.loveruby.cflat.parser.Token;

/**
 * @author 刘科 2018/02/07
 */
public class Location {

    protected String sourceName;
    protected CflatToken token;

    public Location(String sourceName, Token token){
        this(sourceName, new CflatToken(token));
    }

    public Location(String sourceName, CflatToken token){
        this.sourceName = sourceName;
        this.token = token;
    }

    public String sourceName(){
        return sourceName;
    }

    public CflatToken token(){
        return token;
    }

    public int lineno(){
        return token.lineno();
    }

    public int column(){
        return token.column();
    }

    public String line(){
        return token.includedLine();
    }

    public String numberedLine(){
        return "line " + lineno() + ": " + line();
    }

    public String toString(){
        return sourceName + ": " +lineno();
    }
}
