package org.example;

public class Main {
    public static void main(String[] args) {
        String js = """
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

        CalculatorContext context = new CalculatorContext();

        try (var calculator = new Calculator_Proxy(js, context)) {

            System.out.println("4 + 6 = " + calculator.add(4, 6));
            System.out.println("3 * 5 = " + calculator.multiply(3, 5));
            System.out.println("10 / 2 = " + calculator.divide(10, 2));

            try {
                calculator.divide(10, 0);
            } catch (RuntimeException e) {
                System.err.println("JavaScript Error: " + e.getMessage());
            }

       }
    }
}
