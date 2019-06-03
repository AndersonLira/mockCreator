package com.andersonlira.mockcreator;



import java.io.IOException;
import java.io.*;
import java.nio.file.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import com.andersonlira.mockcreator.cache.FileCacheExecutor;
import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.net.*;
import com.andersonlira.mockcreator.log.*;

public class MiniServer {

    private static final String DIR = "payloads/";
    private static final String EXT = ".xml";
    private static final Map<String,String> CACHE = new HashMap<>();
    private static Config config = Config.getInstance();
    private static Executor executor;
    
    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            if(args[0].equals("-s") || args[0].equals("--server")){
                execute();
            }else{
                Help.show();
            }
        }else{
            Help.show();
        }
    }

    private static void execute() throws Exception {
        prepareExecutor();
        String context = Sys.getVariable(Config.SERVER_CONTEXT);
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        server.createContext("/" + context, new MyHandler());
        server.setExecutor(null); // creates a default executor
        Logger.info("Server started on port " + config.getPort());
        server.start();

    }

    private static void prepareExecutor(){

        executor = new FileCacheExecutor();
        executor.setNext(new WsdlExecutor());

    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try{
                Logger.info("Request");
                String id = new Date().toString();
                String response = writeRequest(t);
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }catch(Exception ex){
                ex.printStackTrace();
                throw ex;
            }
        }
    }



    public static String writeRequest(HttpExchange t) throws IOException{
        InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        
        // From now on, the right way of moving from bytes to utf-8 characters:
        
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        
        
        String request = buf.toString();
        String methodName = XmlHelper.getMethodName(request);
        if(executor != null) {
            try{
                return executor.get(request);
            }catch(Exception ex){

            }
        }

        String body = XmlHelper.getBody(request);
        String key = methodName + body.hashCode();

        String filename = DIR + key + EXT;
        Logger.info("Lookin for: " + filename);
        
        String cached = CACHE.get(key);
        

        //If working as proxy or method is in cache evict list all calls will be on origin server
        if(config.workingAsProxy() || config.getCacheEvict().stream().anyMatch(methodName::equals)){
            cached = readFromServer(request,methodName,key);
        }else{
            if(cached == null || !config.hasMemoryCache()) {
                cached = "";
                try{
                    String staticFile = config.getStaticReturn(key,methodName);
                    if(staticFile != null) {
                        filename = staticFile;
                    }
                    cached = readFile(filename);
                    Logger.info("Read from file");
                    CACHE.put(key,cached);
                }catch(Exception e){
                    cached = readFromServer(request,methodName,key);
                }
            }else{
                Logger.info("Read from cache");
            }
            br.close();
            isr.close();
            try{
                if(config.getDelayMethods().stream().anyMatch(methodName::equals)){
                    Logger.info(methodName + " sleeping " + config.getReturnDelay(),Logger.ANSI_GREEN);
                    Thread.sleep(config.getReturnDelay());
                }
            }catch(Exception ie){        
            }        
        }
        cacheManager(methodName);
        return cached;
    }

    private static  String readFromServer(String request,String methodName,String key){
        String response = "";
        try{
            response = Wsdl.post(request,methodName);
            CACHE.put(key,response);
            Logger.info("Read from server " + methodName,Logger.ANSI_YELLOW);
            try (PrintWriter out = new PrintWriter(DIR + key + EXT)) {
                out.println(response);
            }
        }catch(VariableNotDefinedException ex){
            ex.printStackTrace();
            System.exit(1);
        }catch(ServerFaultException ex){
            if(config.showErrorServer()){
                Logger.error("Soap In");
                Logger.info(ex.getInXml(),Logger.ANSI_PURPLE);
                Logger.error("Soap Out");
                Logger.info(ex.getOutXml(),Logger.ANSI_PURPLE);
            }
            response = ex.getOutXml();
        }catch(Exception ex){
            Logger.error(request);
            ex.printStackTrace();
        }
        return response;        
    }

    static String readFile(String path)  throws IOException 
        {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String result = new String(encoded);
            return  result;
        }  

    private  static void removeFile(String path)  {
        try{
            File file = new File(path);
            file.delete();
        }catch(Exception ioEx){
            ioEx.printStackTrace();
        }
    }
    
    private static void cacheManager(String methodName){
        //TODO implement java 8 stream feature
        try{
            for (String method : config.getClearCache(methodName)){
                for (Map.Entry<String, String> entry :  CACHE.entrySet()) {
                    if(entry.getKey().startsWith(method)){
                        Logger.info("Removing cache " + entry.getKey(),Logger.ANSI_BLUE);
                        CACHE.remove(entry.getKey() );
                        String fileName =DIR + entry.getKey() + EXT;
                        Logger.info("Removing file " + fileName,Logger.ANSI_BLUE);
                        removeFile(fileName);
                    }
                }
            }
        }catch(Exception unexpectedException){
            unexpectedException.printStackTrace();
        }
    }        


}