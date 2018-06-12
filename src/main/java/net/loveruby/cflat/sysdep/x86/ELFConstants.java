package net.loveruby.cflat.sysdep.x86;

/**
 * @author 刘科  2018/6/12
 */
public interface ELFConstants {

    // flag
    public static final String SectionFlag_allocatable = "a"; // 映射到内存
    String SectionFlag_writable = "w";
    String SectionFlag_executable = "x";
    String SectionFlag_sectiongroup = "G";      // 该节归属到节组
    String SectionFlag_strings = "S";
    String SectionFlag_treadlocalstorage = "T";

    // argument of "G" flag
    String Linkage_linkonce = "comdat";         // 如果相同节重复出现，则只输出最后一个到目标文件

    // types
    String SectionType_bits = "@progbits";
    String SectionType_nobits = "@nobits";
    String SectionType_note = "@note";

    String SymbolType_function = "@function";
}
