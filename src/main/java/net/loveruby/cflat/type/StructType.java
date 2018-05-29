package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.Slot;
import net.loveruby.cflat.utils.AsmUtils;

import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
public class StructType extends CompositeType {

    public StructType(String name, List<Slot> members, Location location) {
        super(name, members, location);
    }

    public boolean isStruct(){
        return true;
    }

    public boolean isSameType(Type other){
        if(!other.isStruct()){
            return false;
        }
        return equals(other.getStructType());
    }

    protected void computeOffset() {

        long offset = 0;
        long maxAlign = 1;
        for(Slot slot : members()){
            // 原则一：结构体中元素是按照定义顺序一个一个放到内存中去的，但并不是紧密排列的。
            // 从结构体存储的首地址开始，每一个元素放置到内存中时，它都会认为内存是以它自己的大小来划分的，
            // 因此元素放置的位置一定会在自己宽度的整数倍上开始（以结构体变量首地址为0计算）。
            offset = AsmUtils.align(offset, slot.allocSize());
            slot.setOffset(offset);
            offset += slot.allocSize();
            maxAlign = Math.max(maxAlign, slot.alignment());
        }
        // 原则二：在经过第一原则分析后，检查计算出的存储单元是否为所有元素中最宽的元素的长度的整数倍，是，则结束；若不是，则补齐为它的整数倍
        cacheSize = AsmUtils.align(offset, maxAlign);
        cacheAlign = maxAlign;
    }

    @Override
    public String toString() {
        return "struct" + name;
    }
}
