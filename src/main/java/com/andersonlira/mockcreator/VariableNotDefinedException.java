package com.andersonlira.mockcreator;
public class VariableNotDefinedException extends Exception {
        public VariableNotDefinedException(String variable){
                super("Variable not defined: " + variable);
        }
}
