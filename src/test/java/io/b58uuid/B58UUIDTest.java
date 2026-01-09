package io.b58uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.EmptySource;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for B58UUID encoding and decoding.
 * 
 * Tests cover:
 * - Standard test vectors
 * - Edge cases and error conditions
 * - Thread safety
 * - Performance benchmarks
 * - UUID generation
 * - Round-trip consistency
 */
public class B58UUIDTest {
    
    // Standard test vectors from test-vectors.json (canonical)
    private static final String[][] TEST_VECTORS = {
        {"00000000000000000000000000000000", "1111111111111111111111"},  // nil_uuid
        {"ffffffffffffffffffffffffffffffff", "YcVfxkQb6JRzqk5kF2tNLv"},  // max_uuid
        {"550e8400e29b41d4a716446655440000", "BWBeN28Vb7cMEx7Ym8AUzs"},  // standard_uuid
        {"123e4567e89b12d3a456426614174000", "3FfGK34vwMvVFDedyb2nkf"},  // uuid_v1_example
        {"00000000000000000000000000000001", "1111111111111111111112"},  // uuid_with_leading_zeros
        {"deadbeefcafebabe0123456789abcdef", "UVqy39vS4tbfPzthw5VEKg"},  // deadbeef_uuid
        {"0102030405060708090a0b0c0d0e0f10", "18DfbjXLth7APvt3qQPgtf"},  // sequential_bytes
        {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "N5L7eAc4PsHfZViqAMbFEH"},  // all_same_byte
        {"123456789abcdef0123456789abcdef0", "3FP9ScppY3pxArsirSpyro"},  // mixed_pattern
    };
    
    private static byte[][] BYTE_TEST_VECTORS;
    
    static {
        BYTE_TEST_VECTORS = new byte[TEST_VECTORS.length][16];
        for (int i = 0; i < TEST_VECTORS.length; i++) {
            BYTE_TEST_VECTORS[i] = hexToBytes(TEST_VECTORS[i][0]);
        }
    }
    
    @Test
    @DisplayName("Standard test vectors - encode")
    void testStandardVectorsEncode() throws B58UUIDException {
        for (int i = 0; i < TEST_VECTORS.length; i++) {
            byte[] input = BYTE_TEST_VECTORS[i];
            String expected = TEST_VECTORS[i][1];
            
            String result = B58UUID.encode(input);
            assertEquals(expected, result, 
                String.format("Test vector %d failed: expected %s, got %s", i, expected, result));
        }
    }
    
    @Test
    @DisplayName("Standard test vectors - decode")
    void testStandardVectorsDecode() throws B58UUIDException {
        for (int i = 0; i < TEST_VECTORS.length; i++) {
            String input = TEST_VECTORS[i][1];
            byte[] expected = BYTE_TEST_VECTORS[i];
            
            byte[] result = B58UUID.decode(input);
            assertArrayEquals(expected, result, 
                String.format("Test vector %d failed: expected %s, got %s", i, 
                    bytesToHex(expected), bytesToHex(result)));
        }
    }
    
    @Test
    @DisplayName("Round-trip consistency")
    void testRoundTripConsistency() throws B58UUIDException {
        for (int i = 0; i < TEST_VECTORS.length; i++) {
            byte[] original = BYTE_TEST_VECTORS[i];
            String encoded = B58UUID.encode(original);
            byte[] decoded = B58UUID.decode(encoded);
            
            assertArrayEquals(original, decoded, 
                String.format("Round-trip failed for test vector %d", i));
        }
    }
    
    @Test
    @DisplayName("UUID string encoding")
    void testUuidStringEncoding() throws B58UUIDException {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        String expected = "BWBeN28Vb7cMEx7Ym8AUzs";
        
        String result = B58UUID.encodeUUID(uuidStr);
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("UUID string decoding")
    void testUuidStringDecoding() throws B58UUIDException {
        String b58Str = "BWBeN28Vb7cMEx7Ym8AUzs";
        String expected = "550e8400-e29b-41d4-a716-446655440000";
        
        String result = B58UUID.decodeToUUID(b58Str);
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("UUID string round-trip")
    void testUuidStringRoundTrip() throws B58UUIDException {
        String[] testUuids = {
            "550e8400-e29b-41d4-a716-446655440000",
            "12345678-1234-5678-1234-567812345678",
            "ffffffff-ffff-ffff-ffff-ffffffffffff",
            "00000000-0000-0000-0000-000000000000"
        };
        
        for (String uuid : testUuids) {
            String encoded = B58UUID.encodeUUID(uuid);
            String decoded = B58UUID.decodeToUUID(encoded);
            assertEquals(uuid, decoded, 
                String.format("UUID round-trip failed for %s", uuid));
        }
    }
    
    @Test
    @DisplayName("UUID generation")
    void testUuidGeneration() throws B58UUIDException {
        String b58Uuid = B58UUID.generate();
        
        assertNotNull(b58Uuid);
        assertEquals(22, b58Uuid.length(), "Generated Base58 UUID should be 22 characters");
        
        // Verify it can be decoded
        byte[] decoded = B58UUID.decode(b58Uuid);
        assertEquals(16, decoded.length, "Decoded UUID should be 16 bytes");
        
        // Verify version and variant bits
        assertEquals(4, (decoded[6] >> 4) & 0x0F, "Should be UUID version 4");
        assertEquals(2, (decoded[8] >> 6) & 0x03, "Should be UUID variant 10");
    }
    
    @RepeatedTest(100)
    @DisplayName("UUID generation uniqueness")
    void testUuidGenerationUniqueness() {
        String uuid1 = B58UUID.generate();
        String uuid2 = B58UUID.generate();
        
        assertNotEquals(uuid1, uuid2, "Generated UUIDs should be unique");
    }
    
    @Test
    @DisplayName("Null input handling - encode")
    void testNullInputEncode() {
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.encode(null);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_UUID, exception.getErrorType());
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    @DisplayName("Invalid length input - encode")
    void testInvalidLengthEncode() {
        byte[] invalidInput = new byte[15]; // Should be 16 bytes
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.encode(invalidInput);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_LENGTH, exception.getErrorType());
        assertTrue(exception.getMessage().contains("16 bytes"));
    }
    
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Invalid input - decode")
    void testInvalidInputDecode(String input) {
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.decode(input);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_BASE58, exception.getErrorType());
    }
    
    @Test
    @DisplayName("Invalid length - decode")
    void testInvalidLengthDecode() {
        String invalidInput = "111111111111111111111"; // 21 chars, should be 22
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.decode(invalidInput);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_BASE58, exception.getErrorType());
        assertTrue(exception.getMessage().contains("22"));
    }
    
    @Test
    @DisplayName("Invalid characters - decode")
    void testInvalidCharactersDecode() {
        String invalidInput = "111111111111111111111O"; // Contains 'O' which is not in Base58
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.decode(invalidInput);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_BASE58, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Invalid character"));
    }
    
    @Test
    @DisplayName("Null UUID string - encodeUUID")
    void testNullUuidStringEncode() {
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.encodeUUID(null);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_UUID, exception.getErrorType());
    }
    
    @Test
    @DisplayName("Invalid UUID format - encodeUUID")
    void testInvalidUuidFormat() {
        String invalidUuid = "550e8400-e29b-41d4-a716-44665544000"; // 31 chars instead of 32
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.encodeUUID(invalidUuid);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_LENGTH, exception.getErrorType());
    }
    
    @Test
    @DisplayName("Invalid hex characters - encodeUUID")
    void testInvalidHexCharacters() {
        String invalidUuid = "550e8400-e29b-41d4-a716-44665544000g"; // Contains 'g' which is not hex
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.encodeUUID(invalidUuid);
        });
        
        assertEquals(B58UUIDException.ErrorType.INVALID_UUID, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Invalid hex character"));
    }
    
    @Test
    @DisplayName("Thread safety - concurrent encoding")
    void testThreadSafetyConcurrentEncoding() throws InterruptedException {
        final int threadCount = 10;
        final int iterationsPerThread = 1000;
        final byte[] testData = hexToBytes("550e8400e29b41d4a716446655440000");
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentHashMap<String, Integer> results = new ConcurrentHashMap<>();
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        String encoded = B58UUID.encode(testData);
                        results.merge(encoded, 1, Integer::sum);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent encoding");
        assertEquals(1, results.size(), "All threads should produce the same result");
        
        String expectedResult = "BWBeN28Vb7cMEx7Ym8AUzs";
        assertTrue(results.containsKey(expectedResult), 
            String.format("Result should contain expected value %s", expectedResult));
    }
    
    @Test
    @DisplayName("Thread safety - concurrent decoding")
    void testThreadSafetyConcurrentDecoding() throws InterruptedException {
        final int threadCount = 10;
        final int iterationsPerThread = 1000;
        final String testData = "BWBeN28Vb7cMEx7Ym8AUzs";
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentHashMap<String, Integer> results = new ConcurrentHashMap<>();
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        byte[] decoded = B58UUID.decode(testData);
                        String hexResult = bytesToHex(decoded);
                        results.merge(hexResult, 1, Integer::sum);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent decoding");
        assertEquals(1, results.size(), "All threads should produce the same result");
        
        String expectedResult = "550e8400e29b41d4a716446655440000";
        assertTrue(results.containsKey(expectedResult), 
            String.format("Result should contain expected value %s", expectedResult));
    }
    
    @Test
    @DisplayName("Performance - encode speed")
    void testPerformanceEncode() throws B58UUIDException {
        final int iterations = 10000;
        byte[] testData = hexToBytes("550e8400e29b41d4a716446655440000");
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            B58UUID.encode(testData);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double opsPerSecond = (double) iterations / (duration / 1_000_000_000.0);
        
        System.out.println(String.format("Encode performance: %.0f operations/second", opsPerSecond));
        
        // Should be able to handle at least 100,000 operations per second
        assertTrue(opsPerSecond > 100000, 
            String.format("Encode performance too slow: %.0f ops/sec", opsPerSecond));
    }
    
    @Test
    @DisplayName("Performance - decode speed")
    void testPerformanceDecode() throws B58UUIDException {
        final int iterations = 10000;
        String testData = "BWBeN28Vb7cMEx7Ym8AUzs";
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            B58UUID.decode(testData);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double opsPerSecond = (double) iterations / (duration / 1_000_000_000.0);
        
        System.out.println(String.format("Decode performance: %.0f operations/second", opsPerSecond));
        
        // Should be able to handle at least 100,000 operations per second
        assertTrue(opsPerSecond > 100000, 
            String.format("Decode performance too slow: %.0f ops/sec", opsPerSecond));
    }
    
    @Test
    @DisplayName("Edge case - all zeros")
    void testAllZeros() throws B58UUIDException {
        byte[] zeros = new byte[16];
        String encoded = B58UUID.encode(zeros);
        assertEquals("1111111111111111111111", encoded);
        
        byte[] decoded = B58UUID.decode(encoded);
        assertArrayEquals(zeros, decoded);
    }
    
    @Test
    @DisplayName("Edge case - all ones")
    void testAllOnes() throws B58UUIDException {
        byte[] ones = new byte[16];
        Arrays.fill(ones, (byte) 0xFF);
        String encoded = B58UUID.encode(ones);
        assertEquals("YcVfxkQb6JRzqk5kF2tNLv", encoded);
        
        byte[] decoded = B58UUID.decode(encoded);
        assertArrayEquals(ones, decoded);
    }
    
    @Test
    @DisplayName("Edge case - alternating bytes")
    void testAlternatingBytes() throws B58UUIDException {
        byte[] alternating = new byte[16];
        for (int i = 0; i < 16; i++) {
            alternating[i] = (i % 2 == 0) ? (byte) 0xAA : (byte) 0x55;
        }
        
        String encoded = B58UUID.encode(alternating);
        byte[] decoded = B58UUID.decode(encoded);
        assertArrayEquals(alternating, decoded);
    }
    
    @Test
    @DisplayName("22-character output consistency")
    void test22CharacterOutput() throws B58UUIDException {
        // Test that all encodings produce exactly 22 characters
        for (int i = 0; i < TEST_VECTORS.length; i++) {
            byte[] input = BYTE_TEST_VECTORS[i];
            String encoded = B58UUID.encode(input);
            assertEquals(22, encoded.length(), 
                String.format("Test vector %d should produce 22 characters, got %d: %s", 
                    i, encoded.length(), encoded));
        }
        
        // Test with random UUIDs
        for (int i = 0; i < 100; i++) {
            String b58uuid = B58UUID.generate();
            assertEquals(22, b58uuid.length(), 
                String.format("Generated UUID should be 22 characters, got %d: %s", 
                    b58uuid.length(), b58uuid));
        }
    }
    
    @Test
    @DisplayName("Overflow detection - invalid Base58 string")
    void testOverflowDetection() {
        // Create a Base58 string that would decode to a value > 2^128 - 1
        // "Z" repeated 22 times would be much larger than max UUID
        String overflowString = "ZZZZZZZZZZZZZZZZZZZZZZ";
        
        B58UUIDException exception = assertThrows(B58UUIDException.class, () -> {
            B58UUID.decode(overflowString);
        });
        
        assertEquals(B58UUIDException.ErrorType.OVERFLOW, exception.getErrorType());
        assertTrue(exception.getMessage().contains("overflow") || 
                   exception.getMessage().contains("exceeds maximum"));
    }
    
    @Test
    @DisplayName("Version and variant bits validation")
    void testVersionAndVariantBits() throws B58UUIDException {
        // Generate 100 UUIDs and verify version and variant bits
        for (int i = 0; i < 100; i++) {
            String b58uuid = B58UUID.generate();
            byte[] decoded = B58UUID.decode(b58uuid);
            
            // Check version 4 (bits 12-15 of time_hi_and_version)
            int version = (decoded[6] >> 4) & 0x0F;
            assertEquals(4, version, "UUID should be version 4");
            
            // Check variant (bits 6-7 of clock_seq_hi_and_reserved should be 10)
            int variant = (decoded[8] >> 6) & 0x03;
            assertEquals(2, variant, "UUID should have variant 10 (RFC 4122)");
        }
    }
    
    @Test
    @DisplayName("Max UUID boundary test")
    void testMaxUuidBoundary() throws B58UUIDException {
        // Test that max UUID (all 0xFF) encodes and decodes correctly
        byte[] maxUuid = new byte[16];
        Arrays.fill(maxUuid, (byte) 0xFF);
        
        String encoded = B58UUID.encode(maxUuid);
        assertEquals("YcVfxkQb6JRzqk5kF2tNLv", encoded);
        assertEquals(22, encoded.length());
        
        byte[] decoded = B58UUID.decode(encoded);
        assertArrayEquals(maxUuid, decoded);
    }
    
    @Test
    @DisplayName("Nil UUID boundary test")
    void testNilUuidBoundary() throws B58UUIDException {
        // Test that nil UUID (all 0x00) encodes to 22 '1' characters
        byte[] nilUuid = new byte[16];
        
        String encoded = B58UUID.encode(nilUuid);
        assertEquals("1111111111111111111111", encoded);
        assertEquals(22, encoded.length());
        
        byte[] decoded = B58UUID.decode(encoded);
        assertArrayEquals(nilUuid, decoded);
    }
    
    @Test
    @DisplayName("Leading zeros preservation")
    void testLeadingZerosPreservation() throws B58UUIDException {
        // Test UUIDs with various numbers of leading zero bytes
        for (int leadingZeros = 1; leadingZeros <= 15; leadingZeros++) {
            byte[] uuid = new byte[16];
            // Fill non-zero bytes with a pattern
            for (int i = leadingZeros; i < 16; i++) {
                uuid[i] = (byte) (i - leadingZeros + 1);
            }
            
            String encoded = B58UUID.encode(uuid);
            assertEquals(22, encoded.length(), 
                String.format("UUID with %d leading zeros should encode to 22 chars", leadingZeros));
            
            byte[] decoded = B58UUID.decode(encoded);
            assertArrayEquals(uuid, decoded, 
                String.format("UUID with %d leading zeros should round-trip correctly", leadingZeros));
        }
    }
    
    // Helper methods
    
    private static byte[] hexToBytes(String hex) {
        hex = hex.replace("-", "");
        byte[] result = new byte[16];
        for (int i = 0; i < 16; i++) {
            String hexByte = hex.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(hexByte, 16);
        }
        return result;
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}