# KCBench - Kotlin Concurrency Benchmark

A comprehensive collection of Kotlin concurrency bug benchmarks for testing and educational purposes.

## Overview

This repository contains 100 Kotlin projects demonstrating various concurrency bugs across 10 categories. Each project contains 1-2 concurrency bugs with 50-200 lines of code.

## Categories

### 1. Data Race / Suspension Race
Shared state corruption across suspension points in coroutines.

### 2. Atomicity Violation
Check-then-act pattern interrupted by suspension points.

### 3. Order Violation
Dependency order issues in concurrent operations.

### 4. Deadlock
Circular waiting for resources causing system freeze.

### 5. Scope Passing Bug
Incorrect coroutine scope usage leading to memory leaks.

### 6. Cancellation Race
Cancelling without waiting for completion.

### 7. Channel Misuse
Unclosed channels, buffer configuration issues.

### 8. Blocking Misuse
Blocking calls in coroutines blocking threads.

### 9. StateFlow/SharedFlow Race
Non-atomic read-modify-write operations.

### 10. Exception Propagation Silent Cancellation
Silent cancellation of sibling coroutines.

## Project Structure

```
KCBench/
├── 01_DataRace/
├── 02_AtomicityViolation/
├── 03_OrderViolation/
├── 04_Deadlock/
├── 05_ScopePassingBug/
├── 06_CancellationRace/
├── 07_ChannelMisuse/
├── 08_BlockingMisuse/
├── 09_StateFlowSharedFlowRace/
└── 10_ExceptionPropagationSilentCancellation/
```

## Error Summary

For detailed error information including error lines, variables, and reasons for all 100 projects, please see:

- [Complete Error Summary (English)](Complete_Error_Summary_EN.md)
- [完整错误汇总 (中文)](Complete_Error_Summary.md)

## Usage

Each project is a standalone Maven project. To run a benchmark:

```bash
cd KCBench/01_DataRace/ECommerceInventory
mvn clean compile exec:java -Dexec.mainClass="ECommerceInventoryKt"
```

## Purpose

These benchmarks are designed to:
- Demonstrate common concurrency bugs in Kotlin coroutines
- Provide educational examples for learning
- Serve as test cases for static analysis tools
- Help developers understand and avoid concurrency issues

## License

This project is for educational and research purposes.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.