package org.example;

import org.graalvm.polyglot.*;

public class Main {

    public static void main(String[] args) {
        CalculatorContext context = new CalculatorContext();

        try (Context polyglot = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .build()) {
            Value jsBindings = polyglot.getBindings("js");
            jsBindings.putMember("Calculator_Builtins", context);

            String jsCode = """
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
            polyglot.eval("js", jsCode);

            Calculator calculator = jsBindings.getMember("Calculator").as(Calculator.class);

            System.out.println("4 + 6 = " + calculator.add(4, 6));
            System.out.println("3 * 5 = " + calculator.multiply(3,5));
            System.out.println("10 / 2 = " + calculator.divide(10, 2));

            try {
                calculator.divide(10,0);
            } catch (PolyglotException e) {
                System.err.println("JavaScript Error: " + e.getMessage());
            }

        }
    }
}
