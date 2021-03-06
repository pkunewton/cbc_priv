package net.loveruby.cflat.ir;

/**
 * @author 刘科  2018/5/31
 */
public enum Op {
    // 一元运算符
    ADD,  // plus and
    SUB,  // subtraction minus
    MUL,  // times mutiply
    S_DIV,// divided
    U_DIV,
    S_MOD,
    U_MOD,
    BIT_AND,
    BIT_OR,
    BIT_XOR,
    BIT_LSHIFT,    // 逻辑左移， 无符号 <<
    BIT_RSHIFT,    // 逻辑右移， 无符号
    ARITH_RSHIFT,  // 算数右移,  高位符号扩展

    EQ,
    NEQ,
    S_GT, // great than
    S_GTEQ,
    S_LT,
    S_LTEQ,
    U_GT,
    U_GTEQ,
    U_LT,
    U_LTEQ,

    UMINUS,   // 取反 （-）
    BIT_NOT,  // 按位取反 （~）
    NOT,

    S_CAST,
    U_CAST;

    static public Op internBinary(String op, boolean isSigned){
        if (op.equals("+")) {
            return Op.ADD;
        }
        else if (op.equals("-")) {
            return Op.SUB;
        }
        else if (op.equals("*")) {
            return Op.MUL;
        }
        else if (op.equals("/")) {
            return isSigned ? Op.S_DIV : Op.U_DIV;
        }
        else if (op.equals("%")) {
            return isSigned ? Op.S_MOD : Op.U_MOD;
        }
        else if (op.equals("&")) {
            return Op.BIT_AND;
        }
        else if (op.equals("|")) {
            return Op.BIT_OR;
        }
        else if (op.equals("^")) {
            return Op.BIT_XOR;
        }
        else if (op.equals("<<")) {
            return Op.BIT_LSHIFT;
        }
        else if (op.equals(">>")) {
            return isSigned ? Op.ARITH_RSHIFT : Op.BIT_RSHIFT;
        }
        else if (op.equals("==")) {
            return Op.EQ;
        }
        else if (op.equals("!=")) {
            return Op.NEQ;
        }
        else if (op.equals("<")) {
            return isSigned ? Op.S_LT : Op.U_LT;
        }
        else if (op.equals("<=")) {
            return isSigned ? Op.S_LTEQ : Op.U_LTEQ;
        }
        else if (op.equals(">")) {
            return isSigned ? Op.S_GT : Op.U_GT;
        }
        else if (op.equals(">=")) {
            return isSigned ? Op.S_GTEQ : Op.U_GTEQ;
        }
        else {
            throw new Error("unknown binary op: " + op);
        }
    }

    static public Op internUnary(String op) {
        if (op.equals("+")) {
            throw new Error("unary+ should not be in IR");
        }
        else if (op.equals("-")) {
            return Op.UMINUS;
        }
        else if (op.equals("~")) {
            return Op.BIT_NOT;
        }
        else if (op.equals("!")) {
            return Op.NOT;
        }
        else {
            throw new Error("unknown unary op: " + op);
        }
    }
}
