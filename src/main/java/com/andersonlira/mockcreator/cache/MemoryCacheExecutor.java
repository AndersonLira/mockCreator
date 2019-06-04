package com.andersonlira.mockcreator.cache;

import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.log.Logger;
import com.andersonlira.mockcreator.net.XmlHelper;

import java.util.concurrent.*;

public class MemoryCacheExecutor implements Executor,CacheManager{
    private Executor next;
    private static Config config = Config.getInstance();
    private static final ConcurrentMap<String,String> CACHE = new ConcurrentHashMap<>();


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
        String result = getFromCache(xml, methodName);
        return result;
    }

    private String getFromCache(String xml,String methodName) throws Exception{
        String body = XmlHelper.getBody(xml);
        String key = methodName + body.hashCode();
        
        String cached = CACHE.get(key);
        if(cached != null ){
            Logger.info("Read from cache - key: " + key);
            return cached;
        }
        String content = this.next.get(xml);
        if(config.hasMemoryCache()){
            CACHE.put(key,content);
        }
        return content;
    }

    @Override
    public void manageCache(String methodName){
        try{
            for (String method : config.getClearCache(methodName)){
                for (ConcurrentMap.Entry<String, String> entry :  CACHE.entrySet()) {
                    if(entry.getKey().startsWith(method)){
                        Logger.info("Removing cache " + entry.getKey(),Logger.ANSI_BLUE);
                        CACHE.remove(entry.getKey() );
                    }
                }
            }
        }catch(Exception unexpectedException){
            unexpectedException.printStackTrace();
        }        
    }
}