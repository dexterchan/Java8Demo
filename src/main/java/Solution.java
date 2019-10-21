import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    // Complete the miniMaxSum function below.
    static void miniMaxSum(int[] arr) {
        //sort the number first
        List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
        Stream<Integer> newS=list.stream().sorted((a, b)->b-a);

        Integer [] newA = newS.toArray(Integer[]::new);
        int min=0;
        int max=0;
        for (int i=0;i<newA.length-1;i++){
            min += newA[i];
        }
        for (int i=1;i<newA.length;i++){
            max += newA[i];
        }
        System.out.println(min+" "+max);
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int[] arr = new int[5];

        String[] arrItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < 5; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        miniMaxSum(arr);

        scanner.close();
    }
}

