package com.andersonlira.mockcreator;

//TODO this class should read from configuration file and not
public class Config {

        public static String SERVICE_URL = "SERVICE_URL";
        public static String AUTH_STRING = "AUTH_STRING";
        public static String RETURN_DELAY = "RETURN_DELAY";
        public static String SERVER_CONTEXT = "SERVER_CONTEXT";

        private static Config INSTANCE;

        private String[] cacheEvict = {}; //{"getUser","selectPorfolioItem","setSelectedPortfolioItem"};
        private String[] delayMethods = {"selectPorfolioItem"};

        private Config(){

        }

        public static Config getInstance(){
                synchronized(Config.class){
                        if(INSTANCE == null){
                                INSTANCE = new Config();
                        }
                        return INSTANCE;
                }
        }

        public String[] getCacheEvict(){
                return this.cacheEvict;
        }

        public String[] getDelayMethods(){
                return this.delayMethods;
        }

}
