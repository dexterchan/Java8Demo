package com.trial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.function.Function;

public class FunctionChain {

    public static void main(String [] args){
        Function<String, String> f1 = l -> {
            Stack<Character> chlist = new Stack<>();
            StringBuffer buf = new StringBuffer();
            for(int i=0;i<l.length();i++){
                chlist.push(l.charAt(i));
            }
            while(!chlist.empty()){
                buf.append(chlist.pop());
            }
            return buf.toString();
        };
        Function<String, String> f2 = l -> l.substring(0,9);


        String sampleText = "Hello, how are you?";
        System.out.println(f1.apply(sampleText));
        System.out.println(f1.andThen(f2).apply(sampleText));
        System.out.println(f1.compose(f2).apply(sampleText));
    }
}
