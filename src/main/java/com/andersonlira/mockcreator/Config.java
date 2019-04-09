package com.andersonlira.mockcreator;

//TODO this class should read from configuration file and not
public class Config {

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
