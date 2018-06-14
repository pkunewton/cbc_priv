package net.loveruby.cflat.sysdep;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/6/14
 */
public class AssemblerOption {

    public boolean verbose = false;
    List<String> args = new ArrayList<String>();

    public void addArg(String arg){
        args.add(arg);
    }
}
