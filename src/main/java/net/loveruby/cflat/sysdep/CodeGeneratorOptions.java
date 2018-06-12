package net.loveruby.cflat.sysdep;

/**
 * @author 刘科  2018/6/11
 */
public class CodeGeneratorOptions {

    protected int optimizeLevel;
    protected boolean generatePIC;
    protected boolean generatePIE;
    protected boolean verboseAsm;

    public CodeGeneratorOptions() {
        this.optimizeLevel = 0;
        this.generatePIC = false;
        this.generatePIE = false;
        this.verboseAsm = false;
    }

    public int optimizeLevel() {
        return optimizeLevel;
    }

    public boolean isPICRequired() {
        return generatePIC;
    }

    public boolean isPIERequired() {
        return generatePIE;
    }

    public boolean isVerboseAsm() {
        return verboseAsm;
    }

    public boolean isPositionIndependent(){
        return generatePIC || generatePIE ;
    }

    public void setOptimizeLevel(int optimizeLevel) {
        this.optimizeLevel = optimizeLevel;
    }

    public void generatePIC(boolean generatePIC) {
        this.generatePIC = generatePIC;
    }

    public void generatePIE(boolean generatePIE) {
        this.generatePIE = generatePIE;
    }

    public void generateVerboseAsm(boolean verboseAsm) {
        this.verboseAsm = verboseAsm;
    }
}
