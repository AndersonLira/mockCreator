package com.andersonlira.mockcreator;

public class Sys{
        public static String  getVariable(String variable) throws VariableNotDefinedException{
                String value = System.getenv(variable);
                if(value == null || value == ""){
                                throw new VariableNotDefinedException(variable);
                }
                return value;
        }

}
