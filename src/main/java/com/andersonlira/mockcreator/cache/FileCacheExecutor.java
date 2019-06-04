package com.andersonlira.mockcreator.cache;

import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.*;
import com.andersonlira.mockcreator.log.Logger;
import com.andersonlira.mockcreator.net.Wsdl;
import com.andersonlira.mockcreator.net.XmlHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileCacheExecutor implements Executor{
    private static final String DIR = "payloads/";
    private static final String EXT = ".xml";
    private Executor next;
    private static Config config = Config.getInstance();


    public void setNext(Executor next){
        this.next = next;
    }

    public String get(String xml) throws Exception{
        String methodName = XmlHelper.getMethodName(xml);
        String result = getFromFile(xml,methodName);
        manageFiles(methodName);
        return result;
    }

    private String getFromFile(String xml,String methodName) throws Exception {
        String body = XmlHelper.getBody(xml);
        String key = methodName + body.hashCode();
        String filename = DIR + key + EXT;
        try{
            String fromFile = readFile(filename);
            Logger.info("Read from file: "+ filename);
            return fromFile;
        }catch(IOException ex){}
        
        String content = this.next.get(xml);
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(content);
        }
        return content;
    }

    private void manageFiles(String methodName) {
        try{
            for (String method : config.getClearCache(methodName)){
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
                    Paths.get(DIR), method + "*" + EXT)) {
                    dirStream.forEach(path -> removeFile(path.toString()));
                }
            }
        }catch(Exception unexpectedException){
            unexpectedException.printStackTrace();
        }        
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String result = new String(encoded);
        return  result;
    } 

    private  void removeFile(String path)  {
        try{
            File file = new File(path);
            file.delete();
        }catch(Exception ioEx){
            ioEx.printStackTrace();
        }
    }



}