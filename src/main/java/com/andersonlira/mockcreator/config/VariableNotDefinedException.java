package com.andersonlira.mockcreator.config;

public class VariableNotDefinedException extends Exception {
        public VariableNotDefinedException(String variable){
                super("Variable not defined: " + variable);
        }
}
