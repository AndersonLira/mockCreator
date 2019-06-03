package com.andersonlira.mockcreator.cache;

import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.log.Logger;
import com.andersonlira.mockcreator.net.XmlHelper;

import java.util.*;

public class MemoryCacheExecutor implements Executor{
    private Executor next;
    private static Config config = Config.getInstance();
    private static final Map<String,String> CACHE = new HashMap<>();


    private MemoryCacheExecutor(){

    }

    public static  MemoryCacheExecutor create(Executor next){
        if(next == null){
            throw new RuntimeException("Next should not be null");
        }
        MemoryCacheExecutor executor = new MemoryCacheExecutor();
        executor.setNext(next);
        return executor;
    }


    public void setNext(Executor next){
        this.next = next;
    }

    public String get(String xml) throws Exception{
        String methodName = XmlHelper.getMethodName(xml);
        String body = XmlHelper.getBody(xml);
        String key = methodName + body.hashCode();
        
        String cached = CACHE.get(key);
        if(cached != null ){
            Logger.info("Read from cache - key: " + key);
            return cached;
        }
        String content = this.next.get(xml);
        CACHE.put(key,content);
        return content;
    }


}