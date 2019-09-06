package com.trial.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

import static org.junit.Assert.fail;

//@RunWith(JUnit4.class)
public class testOptionalMain {

    static String s = "Hello World!";
    static String nullString = null;

    @Test
    public  void testOptionalOf(){
        Optional<String> optionalS1 = Optional.of(s); // Will work
        Optional<String> optionalS2 = Optional.ofNullable(s); // Will work too

        //System.out.println(optionalS1.get()); // prints "Hello World!"
        assert(optionalS1.get().equals(s));
        assert(optionalS2.get().equals(s));
    }

    @Test
    public void testNullable(){
        Optional<String> optionalNull1 ;
        Optional<String> optionalNull2 ;

        try {
            optionalNull1 = Optional.of(nullString); // -> NullPointerException
            fail("not expected not throwing nullpointer");
        }catch (Exception e){
            if(e instanceof NullPointerException){
                assert(true);
            }else{
                fail("Not ok");
            }
        }
        optionalNull2 = Optional.ofNullable(nullString); // Will work

        assert(!optionalNull2.isPresent() );

    }

    @Test
    public void testNullAssignment(){
        Optional<String> dbName=Optional.ofNullable(nullString);

        assert(!dbName.isPresent());
        dbName=Optional.of("abc");
        dbName.ifPresent(v->{System.out.println("dbName found"); });


    }
    @Test
    public void testOrElse(){
        int v=100;
        //model answer
        Optional<Integer> value = Optional.of(v);
        Integer ret;
        ret=doubleValueOrZero(value);
        assert(ret.intValue() == v*2);
        ret = doubleValueOrZero(Optional.ofNullable(null));
        assert(ret.intValue()==0);


        ret = value.map((kv)->kv*2).orElse(0);
        assert(ret.intValue()==v*2);

        value =Optional.ofNullable(null);
        ret=value.map((kk)->kk*2).orElse(0);
        assert(ret.intValue()==0);


    }

    public Integer doubleValueOrZero(Optional<Integer> value) {
        if(value.isPresent()) {
            return value.get() * 2;
        }
        return 0;
    }

}

