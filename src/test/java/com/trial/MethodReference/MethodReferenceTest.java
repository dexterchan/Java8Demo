package com.trial.MethodReference;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceTest {

    interface Interf{
        void m1();
    }

    @Test
    public void baselineByLambda(){
        Interf iLambda = ()->{
            System.out.println("IMplement by Lambda");
        };
        iLambda.m1();
    }
    public static int m2(){
        System.out.println("method reference by reusing existing static implementation with same arguments");
        return 0; //return type ignored by method reference
    }

    @Test
    public void MethodReferenceTest (){
        Interf m2 = MethodReferenceTest::m2;
        m2.m1();
    }

    static class ThreadRunnerBuilder{
        int level=0;
        public ThreadRunnerBuilder(int level){
            this.level=level;
        }
        public int m1(){
            int result=0;
            for (int i=1;i<=level;i++){
                result+=i;
            }
            System.out.println(result);
            return result;
        }
    }



    @Test
    public void MethodReferenceByInstance() throws InterruptedException{

        Function<Integer, ThreadRunnerBuilder> b = (level)->{
            return new ThreadRunnerBuilder(level);
        };

        ThreadRunnerBuilder logic = b.apply(10);
        Thread t =new Thread(logic::m1);
        t.start();

        t.join();
    }

    @Test
    public void COnstructorReference() throws InterruptedException{
        Function<Integer, ThreadRunnerBuilder> b = ThreadRunnerBuilder::new;//COnstructor reference


        ThreadRunnerBuilder logic = b.apply(20);
        Thread t =new Thread(logic::m1);
        t.start();

        t.join();

    }
}
