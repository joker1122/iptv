package com.tosmart.tsresolve.resolveTs;

import java.util.Random;

/**
 * Created by PC-001 on 2018/3/20.
 */

public class RandomString {
    private static final char[] STANDARD ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    public static String random(){
        char[] buffer=new char[5];
        int i=5;
        while(i>0){
            i--;
            Random random=new Random();
            int index=random.nextInt(15);
            buffer[i]= STANDARD[index];
        }
        return new String(buffer);
    }
}
