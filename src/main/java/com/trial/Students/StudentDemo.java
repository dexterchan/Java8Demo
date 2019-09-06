package com.trial.Students;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class StudentDemo {
   static class Student{
       public String name;
       public double mark;
       public Student(String name, double mark){
           this.name=name;
           this.mark=mark;
       }
   }

    static void prepareStudent(List<Student> studentList){
       studentList.add(new Student("apple",90));
       studentList.add(new Student("Fok",50));
       studentList.add(new Student("geek",61));
       studentList.add(new Student("pine",70));
        studentList.add(new Student("fat_tiger",40));
    }

    public static void main(String [] args){
       List<Student> studentList=new ArrayList<>();

       prepareStudent(studentList);

        Predicate<Student> definePass = p-> p.mark>=50;

        Function<Student, String> defineGrade = s->{
            double m = s.mark;
            if(m>=90){
                return "A";
            }else if(m>=70){
                return "B";
            }else if(m>=60){
                return "C";
            }else if(m>=50){
                return "D";
            }else{
                return "E";
            }
        };
        Consumer<Student> listInfo=s->{
                System.out.println(s.name+" with Grade:"+defineGrade.apply(s)+" Pass: "+definePass.test(s));
        };
        Consumer<Student> AutoComment=s->{
            String Grade=defineGrade.apply(s);
            if(Grade.equals("A")){
                System.out.println(s.name+"<-Excellent! keeps it up");
            }else if(Grade.equals("B")){
                System.out.println(s.name+" <- good!");
            }else if(Grade.equals("C")){
                System.out.println(s.name+" <- average");
            }else{
                System.out.println(s.name+" <- Don't give up");
            }
        };


        studentList.stream().forEach(
                s->{
                    if(definePass.test(s)){
                        listInfo.andThen(AutoComment).accept(s);
                    }else{
                        System.out.println("Not disclose:"+s.name);
                    }
                }

        );
    }
}
