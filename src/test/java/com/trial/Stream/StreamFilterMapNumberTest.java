package com.trial.Stream;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class StreamFilterMapNumberTest {
    List<Integer> numbers=null;

    @Before
    public void prepareNumber(){
        numbers = new ArrayList<Integer>();
        for(int i=10;i<20;i++) {
            numbers.add(i);
        }

    }
    @Test
    public void iterateStream(){
        Stream<Integer> sInteger=numbers.stream().filter(x->x%2==0);
        sInteger.forEach(
                s->System.out.println(s)
        );

    }

    @Test
    public void runFilter(){
        List<Integer> evenNumber = numbers.stream().filter(x->x%2==0).collect(Collectors.toList());

        System.out.println("Filter testing");
        System.out.println(evenNumber);
        long countEven = numbers.stream().filter(x->x%2==0).count();
        System.out.println("Number of even number:"+countEven);

    }
    @Test
    public void runFilterSorted(){
        List<Integer> evenNumber = numbers.stream().filter(x->x%2==0).sorted((a,b)->b.compareTo(a)).collect(Collectors.toList());

        System.out.println("Reversed sorted Filter testing");
        System.out.println(evenNumber);


    }

    @Test
    public void runFilterMin(){
        int minevenNumber = numbers.stream().filter(x->x%2==0).min((a,b)->a.compareTo(b)).get();
        System.out.println("Min Filter testing="+minevenNumber);
    }
    @Test
    public void runFilterMax(){
        int maxevenNumber = numbers.stream().filter(x->x%2==0).max((a,b)->a.compareTo(b)).get();
        System.out.println("Max Filter testing="+maxevenNumber);
    }
    @Test
    public void voidMap(){
        List<Integer> doubleNumber = numbers.stream().map(x->x*2).collect(Collectors.toList());

        System.out.println("Double testing");
        System.out.println(doubleNumber);
    }
    class Accum{
        public int value=0;
    }
    @Test
    public void runFilterForEachAccumulate(){
        int sumvalue= numbers.parallelStream().filter(x->x%2==0).reduce(0, (a,b)->a+b,(e,f)->e+f);
        System.out.println("for each accum Filter testing="+sumvalue);
        assertEquals(sumvalue,70);
    }

    @Test
    public void runFilterToArray(){
int firstele=numbers.stream().findFirst().get();
        Integer[] array=numbers.stream().filter(x->x%2==0).toArray( (s)->new Integer[s]);//Integer[]::new);
        System.out.print("To Array:");
        for( int i: array){
            System.out.println(i);
        }

    }
    @Test
    public void runStreamOf(){
        Stream<Integer> s = Stream.of(1,2,90,12,432,4,56,3);
        int maxvalue=s.max((a,b)->a.compareTo(b)).get();
        System.out.println("Max Stream Of:"+maxvalue);

        double[] d = {1.0,2.0,3.0,4.0};
        Stream<Double> sd = Arrays.stream(d).boxed();
        System.out.println("Stream of Double system out");
        sd.forEach(System.out::println);
    }

}