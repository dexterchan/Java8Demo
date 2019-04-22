package com.trial.model;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class EmployeeTest {

    List<Employee> employees=null;

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
    public void sortAsending(){
        Collections.sort(employees, (a, b)-> a.eno>b.eno?1:a.eno<b.eno?-1:0);
        for (Employee i : employees){
            System.out.println(i);
        }
    }

}