package com.trial;

import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeMapCompareTest {
    TreeMap<Integer,String> numbers=null;

    @Before
    public void prepareCompare(){
        numbers = new TreeMap<Integer,String>(
                (a,b)->(b>a?1:a>b?-1:0)*-1
        );

        numbers.clear();
        numbers.put(100,"egg");
        numbers.put(200,"bread");
        numbers.put(120,"chicken");
        numbers.put(1,"rice");
    }
    @Test
    public void sortAsending(){


        for ( Map.Entry<Integer,String> i : numbers.entrySet()){
            System.out.println(i.getKey()+","+i.getValue());
        }
    }
}
