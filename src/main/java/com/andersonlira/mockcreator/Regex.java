package com.andersonlira.mockcreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
   public static String  getMethodName( String xml ) {
      // String to be scanned to find the pattern.
      String line = xml;
      String pattern = "ns2:(.*) xmlns:ns2";

      // Create a Pattern object
      Pattern r = Pattern.compile(pattern);

      // Now create matcher object.
      Matcher m = r.matcher(line);

      if (m.find( )) {
         return m.group(1);
      } else {
         String patternLocal = "prl:(.*)>";

         // Create a Pattern object
         Pattern rLocal = Pattern.compile(patternLocal);
         Matcher mLocal = rLocal.matcher(line);
         if(mLocal.find()){
            return mLocal.group(1);
         }else{
            return "MethodUnknown";
         }
      }
   }


   public static String  getBody( String xml ) {
      // String to be scanned to find the pattern.
      String line = xml;
      String pattern = "<S:Body>(.*)</S:Body>";

      // Create a Pattern object
      Pattern r = Pattern.compile(pattern);

      // Now create matcher object.
      Matcher m = r.matcher(line);

      if (m.find( )) {
         return m.group(1);
      } else {
         return "Body is not present";
      }
   }
}
