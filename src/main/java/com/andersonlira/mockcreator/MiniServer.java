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

import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.net.*;

public class MiniServer {

    private static final String DIR = "payloads/";
    private static final String EXT = ".xml";
    private static final Map<String,String> CACHE = new HashMap<>();
    private static Config config = Config.getInstance();
    
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

        String context = Sys.getVariable(Config.SERVER_CONTEXT);
        HttpServer server = HttpServer.create(new InetSocketAddress(8088), 0);
        server.createContext("/" + context, new MyHandler());
        server.setExecutor(null); // creates a default executor
        Logger.info("Servidor iniciado");
        server.start();

    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            Logger.info("Request");
            String id = new Date().toString();
            String response = writeRequest(t);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
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
        String methodName = Regex.getMethodName(request);

        String body = Regex.getBody(request);
        String key = methodName + body.hashCode();

        String filename = DIR + key + EXT;
        Logger.info("Lookin for: " + filename);

        String cached = CACHE.get(key);

        

        if(config.getCacheEvict().stream().anyMatch(methodName::equals)){
            cached = readFromServer(request,methodName,key);
        }else{
            if(cached == null) {
                cached = "";
                try{
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
                    Logger.colorInfo(methodName + " sleeping " + config.getReturnDelay(),Logger.ANSI_GREEN);
                    Thread.sleep(config.getReturnDelay());
                }
            }catch(Exception ie){        
            }        
        }
        return cached;
    }

    private static  String readFromServer(String request,String methodName,String key){
        String response = "";
        try{
            response = Wsdl.post(request,methodName);
            CACHE.put(key,response);
            Logger.colorInfo("Read from server " + methodName,Logger.ANSI_YELLOW);
            try (PrintWriter out = new PrintWriter(DIR + key + EXT)) {
                out.println(response);
            }
        }catch(Exception ex){
            if(ex instanceof VariableNotDefinedException){
                ex.printStackTrace();
                System.exit(1);
            }else{
                ex.printStackTrace();
            }
        }
        return response;        
    }

    static String readFile(String path)  throws IOException 
        {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String result = new String(encoded);
            return  result;
        }    


}