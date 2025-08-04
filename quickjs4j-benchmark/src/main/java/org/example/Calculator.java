package org.example;

import io.roastedroot.quickjs4j.annotations.ScriptInterface;

@ScriptInterface(context = CalculatorContext.class)
public interface Calculator {
    int add(int a, int b);
    int multiply(int a, int b);
    double divide(int a, int b);
}
