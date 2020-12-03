package com.trial;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class testAnimal {


    public static void lionRow(){
        Animal lion = new Lion();
        String lionWord = lion.say();
        log.debug(lionWord);
    }

    public static void main(String [] args){
        lionRow();
    }
}
