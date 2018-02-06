package net.loveruby.cflat.parser;

import net.loveruby.cflat.ast.Declarations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LibraryLoader {

    protected List<String> loadPath;
    protected LinkedList<String> loadingLibraries;
    protected Map<String, Declarations> loadedLibraries;
}
