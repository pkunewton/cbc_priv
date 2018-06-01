package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
abstract public class MemoryReference extends Operand implements Comparable<MemoryReference> {

    @Override
    public boolean isMemoryReference() {
        return true;
    }

    abstract public void fixOffset(long diff);
    abstract protected int cmp(DirectMemoryReference mem);
    abstract protected int cmp(IndirectMemoryReference mem);
}
