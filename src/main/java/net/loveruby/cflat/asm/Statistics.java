package net.loveruby.cflat.asm;

import net.loveruby.cflat.ir.Mem;
import net.loveruby.cflat.ir.Return;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    protected Map<Register, Integer> registerUsage;
    protected Map<String, Integer> insnUsage;
    protected Map<Symbol, Integer> symbolUsage;

    static public Statistics collect(List<Assembly> assemblies){
        Statistics stats = new Statistics();
        for(Assembly assembly: assemblies){
            assembly.collectStatistics(stats);
        }
        return stats;
    }

    public Statistics() {
        registerUsage = new HashMap<Register, Integer>();
        insnUsage = new HashMap<String, Integer>();
        symbolUsage = new HashMap<Symbol, Integer>();
    }

    public boolean doesRegisterUsed(Register register){
        return numRegisterUsed(register) > 0;
    }

    public int numRegisterUsed(Register register){
        return fetchCount(registerUsage, register);
    }

    public void registerUsed(Register register){
        incrementCount(registerUsage, register);
    }

    public int numInstructionUsed(String insn){
        return fetchCount(insnUsage, insn);
    }

    public void instructionUsed(String insn){
        incrementCount(insnUsage, insn);
    }

    public boolean doesSymbolUsed(Label label){
        return numSymbolUsed(label.symbol()) > 0;
    }

    public boolean doesSymbolUsed(Symbol symbol){
        return numSymbolUsed(symbol) > 0;
    }

    public int numSymbolUsed(Symbol symbol){
        return fetchCount(symbolUsage, symbol);
    }

    public void symbolUsed(Symbol symbol){
        incrementCount(symbolUsage, symbol);
    }


    protected <K> int fetchCount(Map<K, Integer> map, K key){
        Integer n = map.get(key);
        if(n == null){
            return 0;
        }else {
            return n;
        }
    }

    protected <K> void incrementCount(Map<K, Integer> map, K key){
        map.put(key, fetchCount(map, key) + 1);
    }
}
