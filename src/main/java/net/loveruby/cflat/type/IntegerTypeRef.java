package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 2018/5/29
 */
public class IntegerTypeRef extends TypeRef {

    static public IntegerTypeRef charRef(Location location){
        return new IntegerTypeRef("char", location);
    }

    static public IntegerTypeRef charRef(){
        return new IntegerTypeRef("char");
    }

    static public IntegerTypeRef shortRef(Location location){
        return new IntegerTypeRef("short", location);
    }

    static public IntegerTypeRef shortRef(){
        return new IntegerTypeRef("short");
    }

    static public IntegerTypeRef intRef(Location location){
        return new IntegerTypeRef("int", location);
    }

    static public IntegerTypeRef intRef(){
        return new IntegerTypeRef("int");
    }

    static public IntegerTypeRef longRef(Location location){
        return new IntegerTypeRef("long", location);
    }

    static public IntegerTypeRef longRef(){
        return new IntegerTypeRef("long");
    }

    static public IntegerTypeRef ucharRef(Location location){
        return new IntegerTypeRef("unsigned char", location);
    }

    static public IntegerTypeRef ucharRef(){
        return new IntegerTypeRef("unsigned char");
    }

    static public IntegerTypeRef ushortRef(Location location){
        return new IntegerTypeRef("unsigned short", location);
    }

    static public IntegerTypeRef ushortRef(){
        return new IntegerTypeRef("unsigned short");
    }

    static public IntegerTypeRef uintRef(Location location){
        return new IntegerTypeRef("unsigned int", location);
    }

    static public IntegerTypeRef uintRef(){
        return new IntegerTypeRef("unsigned int");
    }

    static public IntegerTypeRef ulongRef(Location location){
        return new IntegerTypeRef("unsigned long", location);
    }

    static public IntegerTypeRef ulongRef(){
        return new IntegerTypeRef("unsigned long");
    }

    protected String name;

    public IntegerTypeRef(String name) {
        this(name, null);
    }

    public IntegerTypeRef(String name, Location location) {
        super(location);
        this.name = name;
    }

    public String name(){
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IntegerTypeRef)){
            return false;
        }
        return name.equals(((IntegerTypeRef) obj).name);
    }

    @Override
    public String toString() {
        return name;
    }
}
