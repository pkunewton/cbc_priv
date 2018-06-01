package net.loveruby.cflat.asm;

import net.loveruby.cflat.utils.TextUtils;

/**
 * @author 刘科  2018/6/1
 */
public class Instruction extends Assembly {

    protected String mnemonic; // 助记符
    protected String suffix;   // 后缀 l 32位  w 16位   b 8位
    protected Operand[] operands;  // 操作数
    protected boolean needRelocation;  // 是否需要重定向

    public Instruction(String mnemonic) {
        this(mnemonic, "", new Operand[0], false);
    }

    public Instruction(String mnemonic, String suffix, Operand a1) {
        this(mnemonic, suffix, new Operand[]{a1}, false);
    }

    public Instruction(String mnemonic, String suffix, Operand a1, Operand a2) {
        this(mnemonic, suffix, new Operand[]{a1, a2}, false);
    }

    public Instruction(String mnemonic, String suffix, Operand a1, Operand a2,boolean needRelocation) {
        this(mnemonic, suffix, new Operand[]{a1, a2}, needRelocation);
    }

    public Instruction(String mnemonic, String suffix, Operand[] operands, boolean needRelocation) {
        this.mnemonic = mnemonic;
        this.suffix = suffix;
        this.operands = operands;
        this.needRelocation = needRelocation;
    }

    public Instruction build(String mnemonic, Operand o1){
        return new Instruction(mnemonic, this.suffix, new Operand[]{o1}, needRelocation);
    }

    public Instruction build(String mnemonic, Operand o1, Operand o2){
        return new Instruction(mnemonic, this.suffix, new Operand[]{o1, o2}, needRelocation);
    }


    @Override
    public boolean isInstruction() {
        return true;
    }

    public String mnemonic() {
        return mnemonic;
    }

    public boolean isJumpInstruction(){
        return mnemonic.equals("jmp") ||
                mnemonic.equals("jz") ||
                mnemonic.equals("jnz") ||
                mnemonic.equals("je") ||
                mnemonic.equals("jne");
    }

    // 操作数数量
    public int numOperand(){
        return this.operands.length;
    }

    public Operand operand1() {
        return this.operands[0];
    }

    public Operand operand2() {
        return this.operands[1];
    }

    /**
     * Extract jump destination label from operands.
     * 从操作数中提取跳转目的标签
     */
    public Symbol jumpDetination(){
        DirectMemoryReference ref = (DirectMemoryReference) operand1();
        return (Symbol)ref.value();
    }

    @Override
    public void collectStatistics(Statistics stats) {
        stats.instructionUsed(mnemonic);
        for (int i = 0; i < numOperand(); i++){
            operands[i].collectStatistics(stats);
        }
    }

    public String toSource(SymbolTable table) {
        StringBuilder buf = new StringBuilder();
        buf.append("\t");
        buf.append(mnemonic + suffix);
        String seq = "\t";
        for (int i = 0; i < numOperand(); i++){
            buf.append(seq);
            seq = ",";
            buf.append(operands[i].toSource(table));
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "#<insn " + mnemonic + ">";
    }

    public String dump() {
        StringBuilder buf = new StringBuilder();
        buf.append("(Instruction ");
        buf.append(TextUtils.dumpString(mnemonic));
        buf.append(" ");
        buf.append(TextUtils.dumpString(suffix));
        for(Operand operand: operands){
            buf.append(" ").append(operand.dump());
        }
        buf.append(")");
        return buf.toString();
    }
}
