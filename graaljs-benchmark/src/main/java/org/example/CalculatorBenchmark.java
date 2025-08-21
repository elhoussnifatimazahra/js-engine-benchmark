package org.example;


import org.graalvm.polyglot.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class CalculatorBenchmark {

    private Context polyglot;
    private Calculator calculator;

    private static final String JS_CODE = """
        class Calculator {
            static add(a, b) {
                Calculator_Builtins.log("Calling add with " + a + " and " + b);
                return a + b;
            }

            static multiply(a, b) {
                Calculator_Builtins.log("Calling multiply");
                return a * b;
            }

            static divide(a, b) {
                if (b === 0) {
                    throw new Error("Divide by zero");
                }
                Calculator_Builtins.log("Dividing " + a + " / " + b);
                return a / b;
            }
        }
        """;

    @Setup(Level.Trial)
    public void setup() {
        CalculatorContext context = new CalculatorContext();

        polyglot = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .build();

        Value jsBindings = polyglot.getBindings("js");
        jsBindings.putMember("Calculator_Builtins", context);

        polyglot.eval("js", JS_CODE);

        calculator = jsBindings.getMember("Calculator").as(Calculator.class);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        polyglot.close();
    }

    @Benchmark
    public int benchmarkAdd() {
        return calculator.add(10, 20);
    }

    @Benchmark
    public int benchmarkMultiply() {
        return calculator.multiply(7, 6);
    }

    @Benchmark
    public double benchmarkDivide() {
        return calculator.divide(42, 6);
    }

    @Benchmark
    public double benchmarkDivideByZero() {
        try {
            return calculator.divide(10, 0);
        } catch (RuntimeException e) {
            return -1;
        }
    }

}