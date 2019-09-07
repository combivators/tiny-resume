package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import net.tiny.benchmark.Benchmark;

public class ExpressionTest {

    static Expression exp = new Expression.Builder()
            .exp("value / 65.0 * 0.05")
            .build();

    @Test
    public void testVariable() {
        String exp;
        exp = "value+((3+4.5)*2-rate)/2.1*3.2^2";
        String ret = exp.replaceAll("\\d+(\\.\\d+)?", " "); //去除数字
        ret = ret.replaceAll("[+()/^|*|-]", " "); //去除运算操作符
        ret = ret.trim();
        assertEquals("value          rate", ret);
        String[] vars = ret.split("[ ]+");
        assertEquals(2, vars.length);
        assertEquals("value", vars[0]);
        assertEquals("rate", vars[1]);
    }

    @Test
    public void testFunctionEvaluate() {
        Expression sc;
        sc = new Expression.Builder()
                .fun("var exp = function(value) {return value+((3+4)*2-22)/2*3;};")
                .call("exp(var1)")
                .build();
        assertEquals(-10.0d, sc.evaluate(2));

        sc = new Expression.Builder()
                .fun("var exp = function(value) {return 1 + ((2 + 3) * 4) - value;};")
                .call("exp(var1)")
                .build();
        assertEquals(16.0d, sc.evaluate(5));


        sc = new Expression.Builder()
                .fun("var exp = function(value, rate) {return value / 65.0 * rate;};")
                .call("exp(var1, var2)")
                .build();
        assertEquals(0.03076923076923077d, sc.evaluate(40.0, 0.05));
        assertEquals(0.03461538461538462d, sc.evaluate(45.0, 0.05));
        assertEquals(0.038461538461538464d, sc.evaluate(50.0, 0.05));
    }

    @Test
    public void testExpression() {
        Expression sc = new Expression.Builder()
                .exp("value + 2.0 * 6.0")
                .build();
        assertEquals(22.0d, sc.evaluate(10.0));
    }

    @Test
    public void testOnlyExpression() {
        Expression sc;
        sc = new Expression.Builder()
                .exp("value / 65.0 * rate")
                .build();
        assertEquals(0.03076923076923077d, sc.evaluate(40.0, 0.05));
        assertEquals(0.03461538461538462d, sc.evaluate(45.0, 0.05));
        assertEquals(0.038461538461538464d, sc.evaluate(50.0, 0.05));
    }

    @Benchmark(measure=100000, threads = 10) //250ms
    public void testBenchmark() throws Exception {
        assertEquals(0.03076923076923077d, exp.evaluate(40.0));
        assertEquals(0.03461538461538462d, exp.evaluate(45.0));
        assertEquals(0.038461538461538464d, exp.evaluate(50.0));
    }
}
