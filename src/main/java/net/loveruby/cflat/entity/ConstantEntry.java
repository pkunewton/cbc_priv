package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.ImmediateValue;
import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Symbol;

/**
 * @author 刘科 2018/02/13
 * */
public class ConstantEntry {

    protected String value;
    protected Symbol symbol;
    protected MemoryReference memref;
    protected ImmediateValue address;

    public ConstantEntry(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setMemref(MemoryReference memref) {
        this.memref = memref;
    }

    public void setAddress(ImmediateValue address) {
        this.address = address;
    }

    public Symbol symbol() {
        if(symbol == null){
            throw new Error("must not happen: symbol == null");
        }
        return symbol;
    }

    public MemoryReference memref() {
        if(memref == null){
            throw new Error("must not happen: memref == null");
        }
        return memref;
    }

    public ImmediateValue address() {
        if(address == null){
            throw new Error("must not happen: address == null");
        }
        return address;
    }
}
