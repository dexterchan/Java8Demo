package com.trial;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class TreeSetCompareTest {
    TreeSet<Integer> numbers=null;

    @Before
    public void prepareCompare(){
        numbers = new TreeSet<Integer>(
                (a,b)->b-a
        );

        numbers.clear();
        numbers.add(100);
        numbers.add(200);
        numbers.add(120);
        numbers.add(1);
    }
    @Test
    public void sortAsending(){
        Comparator<Integer> compare = (a, b)->a-b;


        for (Integer i : numbers){
            System.out.println(i);
        }
    }


}
