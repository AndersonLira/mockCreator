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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.andersonlira.mockcreator.cache.CacheManager;
import com.andersonlira.mockcreator.cache.FileCacheExecutor;
import com.andersonlira.mockcreator.cache.MemoryCacheExecutor;
import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.net.*;
import com.andersonlira.mockcreator.log.*;

public class MiniServer {

    private static final String DIR = "payloads/";
    private static final String EXT = ".xml";
    private static final Map<String, String> CACHE = new HashMap<>();
    private static Config config = Config.getInstance();
    private static Executor executor;
    private static Executor proxyExecutor;
    private static List<CacheManager> caches = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (args[0].equals("-s") || args[0].equals("--server")) {
                execute();
            } else {
                Help.show();
            }
        } else {
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

    private static void prepareExecutor() {
        proxyExecutor = new WsdlExecutor();
        Executor fileExecutor = new FileCacheExecutor();
        fileExecutor.setNext(proxyExecutor);
        Executor memoryExecutor = MemoryCacheExecutor.create(fileExecutor);
        executor = memoryExecutor;
        caches.add((CacheManager)memoryExecutor);
        caches.add((CacheManager)fileExecutor);

    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                String id = new Date().toString();
                String response = writeRequest(t);
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public static String writeRequest(HttpExchange t) throws IOException {
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);

        // From now on, the right way of moving from bytes to utf-8 characters:

        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }

        String request = buf.toString();
        String methodName = XmlHelper.getMethodName(request);
        sleepIfNecessary(methodName);
        Executor threadExecutor = config.workingAsProxy() || config.getCacheEvict().stream().anyMatch(methodName::equals) ? proxyExecutor : executor;
        if (executor != null) {
            try {
                String result = threadExecutor.get(request);
                caches.stream().forEach(c -> c.manageCache(methodName));
                return result;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }else{
            throw new RuntimeException("Server is not configured.");
        }
    }

    private static void sleepIfNecessary(String methodName) {
        if (config.getDelayMethods().stream().anyMatch(methodName::equals)) {
            Logger.info(methodName + " sleeping " + config.getReturnDelay(), Color.ANSI_GREEN);
            try {
                Thread.sleep(config.getReturnDelay());
            } catch (InterruptedException e) {}
        }        

    }
}