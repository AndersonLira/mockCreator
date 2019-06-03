package com.andersonlira.mockcreator.cache;

import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import java.util.*;

public class MemoryCacheExecutor implements Executor{
    private Executor executor;
    private static Config config = Config.getInstance();
    private static final Map<String,String> CACHE = new HashMap<>();


    public void setNext(Executor executor){
        this.executor = executor;
    }

    public String get(String xml){
        return null;
    }


}