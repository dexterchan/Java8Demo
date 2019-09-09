package mocktutorial.App;

import mocktutorial.Service.CalculatorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
public class MathApplicationTest {
    Logger log = LoggerFactory.getLogger(MathApplicationTest.class);

    //@InjectMocks annotation is used to create and inject the mock object
    //@InjectMocks
    MathApplication mathApplication = new MathApplication();

    //@Mock annotation is used to create the mock object to be injected
    //@Mock
    CalculatorService calcService;

    @Before
    public void init(){
        calcService=mock(CalculatorService.class);
        mathApplication.setCalculatorService(calcService);
    }

    @Test
    public void testAdd(){
        //add the behavior of calc service to add two numbers
        when(calcService.add(10.0,20.0)).thenReturn(30.00);
        when(calcService.add(30,50)).thenReturn(80.00);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);
        Assert.assertThat(mathApplication.add(30,50),closeTo(80,0.001));
    }

    @Test
    public void testAddSubstract(){
        //add the behavior of calc service to add two numbers
        when(calcService.add(10.0,20.0)).thenReturn(30.00);

        //add the behavior of calc service to subtract two numbers
        when(calcService.subtract(20.0,10.0)).thenReturn(10.00);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);

        //test the subtract functionality
        Assert.assertEquals(mathApplication.subtract(20.0, 10.0),10.0,0.0);

        //default call count is 1
        verify(calcService).subtract(20.0, 10.0);

        //check if add function is called three times
        verify(calcService, times(3)).add(10.0, 20.0);

        //verify that method was never called on a mock
        verify(calcService, never()).multiply(10.0,20.0);
    }


    @Test
    public void testVaryingCall(){
        //add the behavior of calc service to add two numbers
        when(calcService.add(10.0,20.0)).thenReturn(30.00);

        //add the behavior of calc service to subtract two numbers
        when(calcService.subtract(20.0,10.0)).thenReturn(10.00);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);
        Assert.assertEquals(mathApplication.add(10.0, 20.0),30.0,0);

        //test the subtract functionality
        Assert.assertEquals(mathApplication.subtract(20.0, 10.0),10.0,0.0);

        //check a minimum 1 call count
        verify(calcService, atLeastOnce()).subtract(20.0, 10.0);

        //check if add function is called minimum 2 times
        verify(calcService, atLeast(2)).add(10.0, 20.0);

        //check if add function is called maximum 3 times
        verify(calcService, atMost(3)).add(10.0,20.0);
    }

    @Test(expected = RuntimeException.class)
    public void testAddException(){
        //add the behavior to throw exception
        doThrow(new RuntimeException("Add operation not implemented"))
                .when(calcService).add(80.0,20.0);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(80.0, 20.0),100.0,0);
    }

    @Test
    public void testAddAndSubtract(){

        //add the behavior to add numbers
        when(calcService.add(20.0,10.0)).thenReturn(30.0);

        //subtract the behavior to subtract numbers
        when(calcService.subtract(20.0,10.0)).thenReturn(10.0);



        //test the subtract functionality
        Assert.assertEquals(mathApplication.subtract(20.0, 10.0),10.0,0);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(20.0, 10.0),30.0,0);

        //create an inOrder verifier for a single mock
        InOrder inOrder = inOrder(calcService);

        //following will make sure that add is first called then subtract is called.
        inOrder.verify(calcService).subtract(20.0,10.0);
        inOrder.verify(calcService).add(20.0,10.0);
    }

    @Test
    public void testCallback(){
        when(calcService.add(20,10)).thenAnswer(
                (invocation)->{
                    //get the arguments passed to mock
                    Object[] args = invocation.getArguments();
                    Stream.of(args).forEach(
                            (a)->{
                                //System.out.println(a.toString());
                                log.debug(a.toString());
                            }
                    );
                    //get the mock
                    Object mock = invocation.getMock();
                    //return the result
                    return 30.0;
                }
        );

        Assert.assertEquals(mathApplication.add(20.0, 10.0),30.0,0.01);


    }

    @Test
    public void testSpy(){
        CalculatorService svc = spy(new Calculator());
        mathApplication.setCalculatorService(svc);

        Assert.assertThat(mathApplication.add(80,20),closeTo(100,0.001));

    }

    @Test(expected = AssertionError.class)
    public void testReset(){
        when(calcService.add(100,10)).thenReturn(110.0);

        Assert.assertThat(mathApplication.add(100,10),closeTo(110.00,0.001));

        reset(calcService);
        Assert.assertThat(mathApplication.add(100,10),closeTo(110.00,0.001));

    }

    @Test
    public void testAddAndSubtractwithTimeLimit(){

        //add the behavior to add numbers
        when(calcService.add(20.0,10.0)).thenReturn(30.0);

        //subtract the behavior to subtract numbers
        when(calcService.subtract(20.0,10.0)).thenReturn(10.0);

        //test the subtract functionality
        Assert.assertEquals(mathApplication.subtract(20.0, 10.0),10.0,0);

        //test the add functionality
        Assert.assertEquals(mathApplication.add(20.0, 10.0),30.0,0);

        //verify call to add method to be completed within 100 ms
        verify(calcService, timeout(100)).add(20.0,10.0);

        //invocation count can be added to ensure multiplication invocations
        //can be checked within given timeframe
        verify(calcService, timeout(100).times(1)).subtract(20.0,10.0);
    }


    class Calculator implements CalculatorService {
        @Override
        public double add(double input1, double input2) {
            return input1 + input2;
        }

        @Override
        public double subtract(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }

        @Override
        public double multiply(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }

        @Override
        public double divide(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }
    }

}