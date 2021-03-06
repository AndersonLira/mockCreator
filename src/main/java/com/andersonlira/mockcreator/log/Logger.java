package com.andersonlira.mockcreator.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
        private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        public static void info(Object message){
                System.out.println(format.format(new Date()) + " - " + message);
        }
        public static void info(Object message,Color color){
                System.out.print(color.toString());
                Logger.info(message);
                System.out.print(Color.ANSI_RESET.toString());
        }

        public static void error(Object message){
                info(message,Color.ANSI_RED);
        }

        


}
