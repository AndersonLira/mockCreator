package com.andersonlira.mockcreator.config;

public class Help{
    public static void show(){
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Usage:");
        System.out.println("");
        System.out.println("    -s or --server execute server");
        System.out.println("");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Configuration");
        System.out.println("");
        System.out.println("    Path");
        System.out.println("");
        System.out.println("        payloads folder is necessary in execution dir");
        System.out.println("");
        System.out.println("    Variables");
        System.out.println("");
        System.out.println("        " + Config.SERVICE_URL + "   Origin service url");
        System.out.println("        " + Config.AUTH_STRING + " Base64 authorization encode user:password example user:1234");
        System.out.println("        " + Config.SERVER_CONTEXT + " context of service example mockservice");
        System.out.println("");
        System.out.println("----------------------------------------------------------------------------------------");

    }    

}