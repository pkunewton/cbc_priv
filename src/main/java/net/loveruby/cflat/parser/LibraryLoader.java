package net.loveruby.cflat.parser;

import net.loveruby.cflat.ast.Declarations;
import net.loveruby.cflat.exception.CompileException;
import net.loveruby.cflat.exception.FileException;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.utils.ErrorHandler;

import java.io.File;
import java.util.*;

public class LibraryLoader {

    protected List<String> loadPath;
    protected LinkedList<String> loadingLibraries;
    protected Map<String, Declarations> loadedLibraries;

    public LibraryLoader(){
        this(defaultPath());
    }

    public LibraryLoader(List<String> loadPath){
        this.loadPath = loadPath;
        this.loadingLibraries = new LinkedList<String>();
        this.loadedLibraries = new HashMap<String, Declarations>();
    }

    public static List<String> defaultPath(){
        List<String> paths = new ArrayList<String>();
        paths.add(".");
        return paths;
    }

    /**
     * @see net.loveruby.cflat.compiler.Options 引入头文件路径的参数 -I
     * */
    public void addLoadPath(String path){
        loadPath.add(path);
    }

    /**
     * 1、载入头文件
     * 2、判断头文件是否循环引用
     * 3、已经解析的头文件不在解析
     * */
    public Declarations loadLibrary(String libid, ErrorHandler errorHandler)
            throws CompileException {
        // 递归解析头文件时，判断是否循环引入了头文件
        if (loadingLibraries.contains(libid)){
            throw new SemanticException("");
        }
        loadingLibraries.addLast(libid);
        Declarations declarations = loadedLibraries.get(libid);
        // 已经解析过的头文件不在重复解析，直接返回
        if (declarations != null){
            return declarations;
        }
        // 解析头文件，该方法内部继续调用 loadLibrary 递归解析头文件
        declarations = Parser.parseDeclFile(searchLibrary(libid), this, errorHandler);
        loadedLibraries.put(libid, declarations);
        loadingLibraries.removeLast();
        return declarations;
    }

    public File searchLibrary(String libid) throws FileException {
        try {
            for(String path : loadPath){
                File file = new File(path + "/" + libPath(libid) + ".hb");
                if(file.exists()){
                    return file;
                }
            }
            throw new FileException("not such library header file: " + libid);
        } catch (SecurityException ex){
            throw new FileException(ex.getMessage());
        }
    }

    protected String libPath(String libid){
        return libid.replace('.', '/');
    }

}
