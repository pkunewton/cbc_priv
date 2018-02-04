package net.loveruby.cflat.exception;

public class IPCException extends CompileException {

    // IPC inter-process communication 进程间通信
    public IPCException(String msg){
        super(msg);
    }
}
