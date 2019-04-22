package com.trial;

public class ThreadRunFnInterface {
    final static int loop=10;

    int myVariable=999;

    public void execute(){
        Thread t = new Thread(
                ()->{
                    for (int i=0;i<loop;i++){
                        System.out.println("Child Thread:"+i+": my variable"+this.myVariable);
                    }
                }
        );
        t.start();
    }

    public static void main(String[] args) throws Exception{

        ThreadRunFnInterface  c = new ThreadRunFnInterface();
        c.execute();


        //Thread.sleep(1);
        for (int i=0;i<loop;i++){
            System.out.println("Main Thread:"+i);
        }

    }
}
