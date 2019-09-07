package com.trial.Predfined;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.matchesPattern;


public class SupplierTest {

    @Test
    public void generateOTP(){
        final int numberOfDigits=6;

        Supplier<Integer> randomDigitGenerator=()->{
            return ThreadLocalRandom.current().nextInt(10);
        };

        Supplier<String> OTPGenerator = ()->{
            StringBuffer sb = new StringBuffer();
            for (int i=0;i<numberOfDigits;i++){
                sb.append(randomDigitGenerator.get());
            }
            return sb.toString();
        };


        Supplier<Character> randomCharGenerator=()->{
            final String CharacterPool ="ABCDEFGHIJKLMNOPQRSTUVWXYZ#%?";
            return CharacterPool.charAt(ThreadLocalRandom.current().nextInt(CharacterPool.length()));
        };


        Supplier<String> PasswordGenerator=()->{
            StringBuffer sb = new StringBuffer();
            for (int i=0;i<numberOfDigits;i++){
                if(i%2==0){
                    sb.append(randomCharGenerator.get());
                }else{
                    sb.append(randomDigitGenerator.get());
                }
            }
            return sb.toString();
        };

        for (int i=0;i<10;i++) {
            String patternString = "\\d{6}";
            String otp = OTPGenerator.get();
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(otp);
            //System.out.println(otp);
            Assert.assertTrue(matcher.matches());
            Assert.assertThat(otp, matchesPattern(patternString));
            Assert.assertThat(0.001, closeTo(0.001,0.00001));
            //Assert.assertThat(otp, )
        }
        for (int i=0;i<10;i++) {
            String patternString = "[A-Z#%?]\\d[A-Z#%?]\\d[A-Z#%?]\\d";
            String pwd = PasswordGenerator.get();
            System.out.println(pwd);
            Assert.assertThat(pwd, matchesPattern(patternString));
        }

    }
}
