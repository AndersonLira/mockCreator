package com.andersonlira.mockcreator.config;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Config {

        public static String SERVICE_URL = "SERVICE_URL";
        public static String AUTH_STRING = "AUTH_STRING";
        public static String SERVER_CONTEXT = "SERVER_CONTEXT";
        private final Integer DEFAULT_PORT = 8088;

        private static Config INSTANCE;
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
                String configurationFile = null;
                InputStream in = null;
                try{
                        configurationFile = Sys.getVariable("MC_CONF_FILE");
                        File file = new File(configurationFile);
                        in = new FileInputStream(file);
                }catch(Exception ex){
                        configurationFile = "/config.json";
                        in =  getClass().getResourceAsStream(configurationFile);
                }

                try{
                        configuration = mapper.readValue(in,new TypeReference<Map<String, Object>>() {});
                }catch(Exception ex){
                        ex.printStackTrace();
                }finally{
                        if(Objects.nonNull(in)){
                                try{
                                        in.close();
                                }catch(Exception eClose){}
                        }
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
        
        public String getStaticReturn(String key,String methodName){
                try{
                        Map<String,String> map = (Map<String,String>) configuration.get("staticReturn");
                        String result = map.get(methodName);
                        if(result == null){
                                result = map.get(key);
                        }
                        return result;
                }catch(Exception ex){
                        return null;
                }
        }

        public Boolean showErrorServer(){
                try{
                        return (Boolean) configuration.get("showErrorServer");
                }catch(Exception ex){
                        return false;
                }       
        }

        public Boolean workingAsProxy(){
                try{
                        return (Boolean) configuration.get("workingAsProxy");
                }catch(Exception ex){
                        return false;
                }       
        }
        public Boolean hasMemoryCache(){
                try{
                        Boolean hasMemoryCache =  (Boolean) configuration.get("hasMemoryCache");
                        return hasMemoryCache == null ? true : hasMemoryCache;
                }catch(Exception ex){
                        return true;
                }       
        }


        public Integer getPort(){
                try{
                        Integer port = (Integer) configuration.get("port");
                        return port == null ? DEFAULT_PORT : port;
                }catch(Exception ex){
                        return DEFAULT_PORT;
                }       
        }

        public Boolean logRequestBody(){
                try{
                        Boolean logRequestBody =  (Boolean) configuration.get("logRequestBody");
                        return logRequestBody == null ? false : logRequestBody;
                }catch(Exception ex){
                        return false;
                }       
        }

        public Boolean logResponseBody(){
                try{
                        Boolean logResponseBody =  (Boolean) configuration.get("logResponseBody");
                        return logResponseBody == null ? false : logResponseBody;
                }catch(Exception ex){
                        return false;
                }       
        }

}
