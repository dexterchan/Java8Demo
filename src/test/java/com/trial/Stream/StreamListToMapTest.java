package com.trial.Stream;

import com.trial.model.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamListToMapTest {

    List<Employee> employees;
    @Before
    public void prepareCompare(){
        employees = new ArrayList<Employee>();

        employees.clear();
        employees.add(new Employee(100, "apple"));
        employees.add(new Employee(200, "orange"));
        employees.add(new Employee(1100, "pineapple"));
        employees.add(new Employee(1, "banana"));
    }

    @Test
    public void usingLambda(){
        Map<Integer, Employee> resultMap  = employees.stream()
                .collect(Collectors.toMap(e -> e.eno, e->e));
        for( Map.Entry<Integer,Employee> e:resultMap.entrySet()){
            System.out.println(e.getKey()+";"+e.getValue());

        }

    }

    @Test
    public void duplicatedKey(){
        List cards = Arrays.asList("Visa", "MasterCard", "American Express", "Visa");
        System.out.println("list: " + cards);

        Map<String, Integer> cards2Length = (Map<String,Integer>) cards.stream()
                .collect(Collectors.toMap(Function.identity(), String::length, (e1, e2) -> e1));

        System.out.println("map: " + cards2Length);

    }

    @Test
    public void duplicatedKey2(){
        List cards = Arrays.asList("Visa", "MasterCard", "American Express", "Visa");
        System.out.println("list: " + cards);

        Map<String, Integer> cards2Length = (Map<String,Integer>) cards.stream()
                .collect(Collectors.toMap(Function.identity(), String::length, (e1, e2) -> e1));
        System.out.println("map: " + cards2Length);

    }

}
