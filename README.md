# JavaScript Engine Benchmark: QuickJS4J vs GraalJS

This project compares the performance and behavior of two JavaScript engines embedded in Java applications:

- **QuickJS4J**: A lightweight Java binding for the QuickJS engine.
- **GraalJS**: The JavaScript engine from GraalVM with full polyglot support.

The comparison is done using **JMH (Java Microbenchmark Harness)** for throughput benchmarks and **Hyperfine** for measuring startup time.

---

## Project Structure

```

js-engine-benchmark/
├── graaljs-benchmark/        # GraalJS implementation and JMH benchmark
├── quickjs4j-benchmark/      # QuickJS4J implementation and JMH benchmark
├── docs/                     # Benchmark results and analysis
│   ├── results-graaljs.txt
│   ├── results-quickjs4j.txt
│   
└── README.md                 # This documentation

````

---

## Objective

The goal is to:
- Evaluate and compare **execution performance**, **startup latency**, and **Java interop** between GraalJS and QuickJS4J.
- Identify use cases where each engine performs better.

---

## Tools & Technologies

- **Java 17**
- **JMH 1.37** – for microbenchmarking throughput (ops/ms)
- **Hyperfine** – for measuring `java -jar` startup time
- **QuickJS4J** – Java bindings for the QuickJS engine
- **GraalJS** – JavaScript engine from GraalVM

---

##  Benchmark Configuration (JMH)

```java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@Fork(1)
````

Each engine executes the following operations inside a benchmarked method:

* `add(a, b)`
* `multiply(a, b)`
* `divide(a, b)` with error handling

Each function logs its execution using the Java side via interop.

---

## JavaScript Code Used

```js
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
```

---

## How to Run

### QuickJS4J

```bash
cd quickjs4j-benchmark
mvn clean package
java -jar target/quickjs4j-benchmark -1.0-SNAPSHOT.jar
java -cp target/quickjs4j-benchmark -1.0-SNAPSHOT.jar org.openjdk.jmh.Main
```

### GraalJS

```bash
cd graaljs-benchmark
mvn clean package
java -jar target/graaljs-benchmark -1.0-SNAPSHOT.jar
java -cp target/graaljs-benchmark -1.0-SNAPSHOT.jar org.openjdk.jmh.Main
```

### Measure startup time with Hyperfine

```bash
hyperfine "java -jar target/quickjs4j-benchmark -1.0-SNAPSHOT.jar"
hyperfine "java -jar target/graaljs-benchmark-1.0-SNAPSHOT.jar"
```

---

## Benchmark Results

### JMH Throughput (ops/ms)

| Operation        | QuickJS4J | GraalJS        |
| ---------------- | --------- | -------------- |
| `add(10, 20)`    | 5.52      | 65.80          |
| `multiply(6, 7)` | 5.86      | 73.95          |
| `divide(42, 6)`  | 5.55      | 31.21 (± high) |

### Startup Time (Hyperfine)

| Engine    | Startup Time (avg) |
| --------- | ------------------ |
| QuickJS4J | \~301 ms           |
| GraalJS   | \~591 ms           |

---

## Analysis

