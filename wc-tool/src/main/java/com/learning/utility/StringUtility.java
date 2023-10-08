package com.learning.utility;

import java.nio.charset.Charset;

public class StringUtility {

    public static byte[] encodeString(String str, Charset standardCharsets) {
        return str.getBytes(standardCharsets);
    }

    public static String decodeString(byte[] bytes, Charset standardCharsets) {
        return new String(bytes, standardCharsets);
    }

    public static int numberOfLines(String str) {
        int count = 0;
        for (int i = 0; i< str.length(); i++){
            if(str.charAt(i)=='\n'){
                count++;
            }
        }
        return count;
    }
}
