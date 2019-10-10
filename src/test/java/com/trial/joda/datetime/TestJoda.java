package com.trial.joda.datetime;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class TestJoda {

    @Test
    public void testJoda(){
        LocalDate date = LocalDate.now();
        System.out.println(date);
        LocalTime time = LocalTime.now();
        System.out.println(time);
    }

    @Test
    public void testDateTime(){
        LocalDate l = LocalDate.now();
        int dd = l.getDayOfMonth();
        int mm = l.getMonthValue();
        int yyyy = l.getYear();

        System.out.println(String.format("%d %d %d",yyyy,mm,dd));

        LocalTime t = LocalTime.now();
        int hh = t.getHour();
        int m = t.getMinute();
        int s = t.getSecond();
        int n = t.getNano();
        System.out.printf("%d:%d:%d.%d",hh,m,s,n);


    }

    @Test
    public void testDataTime2(){
        LocalDateTime ld = LocalDateTime.now();
        System.out.println(ld);

        int dd = ld.getDayOfMonth();
        int mm = ld.getMonthValue();
        int yyyy = ld.getYear();
        System.out.println(String.format("%d %d %d",yyyy,mm,dd));
        int hh = ld.getHour();
        int m = ld.getMinute();
        int s = ld.getSecond();
        int n = ld.getNano();
        System.out.printf("%d:%d:%d.%d",hh,m,s,n);

    }

    @Test
    public void testDateTimeSet(){
        LocalDateTime ld = LocalDateTime.of(2019,1,2,2,30,31);
        int dd = ld.getDayOfMonth();
        int mm = ld.getMonthValue();
        int yyyy = ld.getYear();
        System.out.println(String.format("%d %d %d",yyyy,mm,dd));
        int hh = ld.getHour();
        int m = ld.getMinute();
        int s = ld.getSecond();
        int n = ld.getNano();
        System.out.printf("%d:%d:%d.%d\n",hh,m,s,n);

        System.out.printf("After 6 months:%s\n",ld.plusMonths(6).toString());

        System.out.printf("Before 6 months:%s\n",ld.plusMonths(-6).toString());
    }

    @Test
    public void testPeriod(){
        LocalDate Start = LocalDate.of(2018,1,23);
        LocalDate End = LocalDate.now();
        Period p = Period.between(Start,End);
        System.out.printf("Year:%d, Month:%d, Day:%d\n",p.getYears(),p.getMonths(),p.getDays());
        System.out.printf("Number of days:%d\n",ChronoUnit.DAYS.between(Start,End));

    }

    @Test
    public void ZoneId(){
        ZoneId zone = ZoneId.systemDefault();
        System.out.println(zone);
        ZoneId la = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dt = ZonedDateTime.now(la);
        System.out.println(dt);
    }
}
