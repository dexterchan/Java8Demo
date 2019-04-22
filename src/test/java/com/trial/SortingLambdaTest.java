package com.trial;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

public class SortingLambdaTest {

    ArrayList<Integer> numbers=new ArrayList<Integer>();

    @Before
    public void prepareCompare(){
        numbers.clear();
        numbers.add(100);
        numbers.add(200);
        numbers.add(120);
        numbers.add(1);
    }
    @Test
    public void sortAsending(){
        Comparator<Integer> compare = (a,b)->a-b;
        numbers.sort(compare);
        assert (numbers.get(0)==1);
    }
}
