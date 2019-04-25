package com.trial.Predfined;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class PredicateTest {
    @Test
    public void basicTestIntegerLarger100(){
        Predicate<Integer> t = i->i>100;
        assert(t.test(101));
    }

    @Test
    public void testEmptyCollection(){
        Predicate<Collection> p = c->c.isEmpty();
        ArrayList<String> gfg = new ArrayList<String>() {
            {
                add("Geeks");
                add("for");
                add("Geeks");
            }
        };
        assert(!p.test(gfg));
        gfg = new ArrayList<String>();
        assert(p.test(gfg));

    }

    @Test
    public void PredicateJoin(){
        Integer [] x = {100,10,20,3,41,5,2};
        Predicate<Integer> p1 = i->i>10;
        Predicate <Integer> p2 = i->i%2==0;


        BiFunction< Predicate<Integer>, Integer[] ,Integer > displayFunc = (Predicate<Integer> p, Integer[] xArray)->{
            Arrays.stream(xArray).forEach(e-> {
                if(p.test(e))
                    System.out.println(e);
            });
            return 0;
        } ;
        System.out.println(">10");
        displayFunc.apply(p1,x);
        System.out.println("<=10");
        displayFunc.apply(p1.negate(),x);
        System.out.println(">10 and even number");
        displayFunc.apply(p1.and(p2),x);
        System.out.println(">10 or even number");
        displayFunc.apply(p1.or(p2),x);
    }
}
