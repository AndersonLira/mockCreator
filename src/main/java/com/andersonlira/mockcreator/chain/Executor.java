package com.andersonlira.mockcreator.chain;

public interface Executor {
    void setNext(Executor next);
    String get(String xml) throws Exception;
}

