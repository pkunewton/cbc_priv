package net.loveruby.cflat.parser;

/**
 * Created by Administrator on 2018/2/6.
 */
abstract public class Token {

    abstract public int lineno();
    abstract public int column();
    abstract public int includedLine();
}
