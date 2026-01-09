package io.b58uuid;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark for B58UUID encoding and decoding operations.
 * 
 * Run with: mvn test-compile exec:java -Dexec.mainClass="io.b58uuid.B58UUIDBenchmarkTest" -Dexec.classpathScope=test
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class B58UUIDBenchmarkTest {
    
    private static final byte[] TEST_BYTES = hexToBytes("550e8400e29b41d4a716446655440000");
    private static final String TEST_STRING = "BWBeN28Vb7cMEx7Ym8AUzs";
    
    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkEncode() throws B58UUIDException {
        return B58UUID.encode(TEST_BYTES);
    }
    
    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkDecode() throws B58UUIDException {
        return B58UUID.decode(TEST_STRING);
    }
    
    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkGenerate() {
        return B58UUID.generate();
    }
    
    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkEncodeUuid() throws B58UUIDException {
        return B58UUID.encodeUUID("550e8400-e29b-41d4-a716-446655440000");
    }
    
    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkDecodeToUuid() throws B58UUIDException {
        return B58UUID.decodeToUUID(TEST_STRING);
    }
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(B58UUIDBenchmarkTest.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .forks(1)
                .build();
        
        new Runner(opt).run();
    }
    
    private static byte[] hexToBytes(String hex) {
        hex = hex.replace("-", "");
        byte[] result = new byte[16];
        for (int i = 0; i < 16; i++) {
            String hexByte = hex.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(hexByte, 16);
        }
        return result;
    }
}