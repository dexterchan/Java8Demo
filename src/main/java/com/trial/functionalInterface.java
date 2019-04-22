package com.trial;

@FunctionalInterface
interface AddInterface{
    public int add(int x, int y);
}
public class functionalInterface {

    public static void main(String[] args){
        AddInterface opt = (a,b)->{return a+b;};
        System.out.println(opt.add(10,20));

        opt = (a,b)->(a+b);
        System.out.println(opt.add(20,100));
    }

}
