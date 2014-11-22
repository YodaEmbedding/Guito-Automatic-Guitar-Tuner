package com.muntoo.guito;

/**
 * Created by Mateen on 2014-11-21.
 */
public class test {
    private double Num = 0.0;

    public test(double x) {
        Num = x;
    }

    public static String multNum(double x)
    {
        String re = "";

        double num = x * 5.0;
        re = num + "";

        return re;
    }

    public double multNum2()
    {
        double re = Num * 5.0;
        return re;
    }
}
