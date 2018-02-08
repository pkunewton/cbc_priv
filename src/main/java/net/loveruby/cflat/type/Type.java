package net.loveruby.cflat.type;

public abstract class Type {

    static final public long sizeUnknown = -1;

    abstract public long size();
    // 分配的存储空间大小
    public long allocSize() { return size(); }
    // 校准后的存储空间大小
    public long alignment() { return allocSize(); }
}
