package com.andersonlira.mockcreator.config;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

//TODO this class should read from configuration file and not
public class Config {

        public static String SERVICE_URL = "SERVICE_URL";
        public static String AUTH_STRING = "AUTH_STRING";
        public static String SERVER_CONTEXT = "SERVER_CONTEXT";

        private static Config INSTANCE;

        private String[] cacheEvict = {}; //{"getUser","selectPorfolioItem","setSelectedPortfolioItem"};
        private String[] delayMethods = {"selectPorfolioItem"};

        private Map<String,Object> configuration;

        private Config(){
                this.initializeConfig();
        }

        public static Config getInstance(){
                synchronized(Config.class){
                        if(INSTANCE == null){
                                INSTANCE = new Config();
                        }
                        return INSTANCE;
                }
        }

        private void initializeConfig(){
                ObjectMapper mapper = new ObjectMapper();
                try{
                        InputStream in = getClass().getResourceAsStream("/config.json"); 
                        configuration = mapper.readValue(in,new TypeReference<Map<String, Object>>() {});
                }catch(Exception ex){
                        ex.printStackTrace();
                }
        }

        public List<String> getCacheEvict(){
                try{
                        return ((List<String>) configuration.get("cacheEvict"));
                }catch(Exception ex){
                        return new ArrayList<String>();
                }
        }

        public List<String> getDelayMethods(){
                try{
                        return ((List<String>) configuration.get("delayMethods"));
                }catch(Exception ex){
                        return new ArrayList<String>();
                }
        }

        public Long getReturnDelay(){
                try{
                        return ((Integer) configuration.get("returnDelay")).longValue();
                }catch(Exception ex){
                        ex.printStackTrace();
                        return 0l;
                }
        }

        public List<String> getClearCache(String methodName){
                try{
                        Map<String,List<String>> map = (Map<String,List<String>>) configuration.get("clearCache");
                        List<String> list = map.get(methodName);
                        return list != null ? list : new ArrayList<String>();
                }catch(Exception ex){
                        return new ArrayList<String>();
                }

        }        

}
