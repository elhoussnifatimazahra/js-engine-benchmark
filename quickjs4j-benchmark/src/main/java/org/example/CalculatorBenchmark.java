package org.example;


import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class CalculatorBenchmark {

    private Calculator_Proxy calculator;

    private static final String JS_CODE = """
        function add(a, b) {
            Calculator_Builtins.log("Calling add with " + a + " and " + b);
            return a + b;
        }

        function multiply(a, b) {
            Calculator_Builtins.log("Calling multiply");
            return a * b;
        }

        function divide(a, b) {
            if (b === 0) {
                throw new Error("Divide by zero");
            }
            Calculator_Builtins.log("Dividing " + a + " / " + b);
            return a / b;
        }

        export { add, multiply, divide };
    """;

    @Setup(Level.Trial)
    public void setup() {
        calculator = new Calculator_Proxy(JS_CODE, new CalculatorContext());
    }

    @TearDown(Level.Trial)
    public void teardown() throws Exception {
        calculator.close();
    }

    @Benchmark
    public double benchmarkAdd() {
        return calculator.add(10, 20);
    }

    @Benchmark
    public double benchmarkMultiply() {
        return calculator.multiply(6, 7);
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
