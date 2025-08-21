# JavaScript Engine Benchmark: QuickJS4J vs GraalJS

This project compares the performance and behavior of two JavaScript engines embedded in Java applications:

* **QuickJS4J**: A lightweight Java binding for the QuickJS engine.
* **GraalJS**: The JavaScript engine from GraalVM with full polyglot support.

The comparison is done using **JMH (Java Microbenchmark Harness)** for throughput benchmarks and **Hyperfine** for measuring startup time.

---

## Objective

The goal is to:

* Evaluate and compare **execution performance**, **startup latency**, and **Java interop** between GraalJS and QuickJS4J.

---

## Tools & Technologies

* **Java 24**
* **JMH 1.37** – microbenchmarking throughput (ops/ms)
* **Hyperfine** – measuring `java -jar` startup time
* **QuickJS4J** – Java bindings for QuickJS
* **GraalJS** – JavaScript engine from GraalVM

---

## Benchmark Configuration (JMH)

```java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@Fork(1)
```

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
java -jar target/quickjs4j-benchmark-1.0-SNAPSHOT.jar
java -cp target/quickjs4j-benchmark-1.0-SNAPSHOT.jar org.openjdk.jmh.Main
```

### GraalJS

```bash
cd graaljs-benchmark
mvn clean package
java -jar target/graaljs-benchmark-1.0-SNAPSHOT.jar
java -cp target/graaljs-benchmark-1.0-SNAPSHOT.jar org.openjdk.jmh.Main
```

### Measure startup time with Hyperfine

```bash
hyperfine "java -jar target/quickjs4j-benchmark-1.0-SNAPSHOT.jar"
hyperfine "java -jar target/graaljs-benchmark-1.0-SNAPSHOT.jar"
```

---

## Performance Benchmark (JMH)

| Benchmark Operation | QuickJS4J (ops/ms) | GraalJS (ops/ms)  |
| ------------------- | ------------------ | ----------------- |
| `benchmarkAdd`      | 5.505 ± 1.554      | 111.469 ± 620.471 |
| `benchmarkMultiply` | 5.765 ± 0.795      | 148.780 ± 13.209  |
| `benchmarkDivide`   | 5.621 ± 1.102      | 152.286 ± 25.850  |

> 🟢 **GraalJS outperforms QuickJS4J significantly** in throughput operations.

---

## Startup Time (Hyperfine)

| Platform    | Engine    | Mean Time         |
| ----------- | --------- | ----------------- |
| **Linux**   | QuickJS4J | 0.639 s ± 0.016 s |
|             | GraalJS   | 0.930 s ± 0.015 s |
| **Windows** | QuickJS4J | 0.811 s ± 0.014 s |
|             | GraalJS   | 1.237 s ± 0.022 s |

> ⏱ **QuickJS4J starts faster** than GraalJS on both Linux and Windows.

---

