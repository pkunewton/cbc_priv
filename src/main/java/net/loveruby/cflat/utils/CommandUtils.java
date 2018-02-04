package net.loveruby.cflat.utils;

import net.loveruby.cflat.exception.IPCException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author 刘科 2018/02/04
 * 调用OS命令的工具类
 * */
abstract public class CommandUtils {

    static public void invoke(List<String> args, ErrorHandler errorHandler, boolean verbose)
            throws IPCException {
        if(verbose){
            dumpCommand(args);
        }
        try {
            String[] cmd = args.toArray(new String[]{});
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            passThrough(process.getInputStream());
            passThrough(process.getErrorStream());
            if(process.exitValue() != 0){
                errorHandler.error(cmd[0] + " failed." +
                        " ( status " + process.exitValue() + ")");
                throw new IPCException("compile error");
            }
        }catch (InterruptedException ex){
            errorHandler.error("external command interrupted: "
                    + args.get(0) + ": " + ex.getMessage());
            throw new IPCException("compile error");
        }catch (IOException ex){
            errorHandler.error("IO error in external command: " + ex.getMessage());
            throw new IPCException("compile error");
        }
    }

    static private void dumpCommand(List<String> args){
        String seq = "";
        for (String arg : args) {
            System.out.print(seq);
            seq = " ";
            System.out.print(arg);
        }
        System.out.println("");
    }

    static private void passThrough(InputStream stream) throws IOException{
        //在windows中文版平台
        // BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "gb2312"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null){
            System.err.println(line);
        }
    }
}
