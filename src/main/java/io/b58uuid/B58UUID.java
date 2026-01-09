package io.b58uuid;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Fast Base58 encoding/decoding for UUIDs with zero dependencies.
 * 
 * This class provides efficient Base58 encoding and decoding for UUIDs,
 * with comprehensive error handling and zero external dependencies.
 * 
 * Features:
 * - High performance with optimized algorithms
 * - Comprehensive error handling with detailed exceptions
 * - Thread-safe operations
 * - Zero external dependencies
 * - Full UUID v4 support with proper version and variant bits
 */
public final class B58UUID {
    
    /**
     * Base58 alphabet (Bitcoin alphabet).
     * Excludes 0, O, I, and l to avoid confusion.
     */
    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    
    /**
     * Precomputed reverse lookup table for Base58 decoding.
     * Maps ASCII character codes to their Base58 values (0-57) or -1 for invalid characters.
     */
    private static final byte[] REVERSE_ALPHABET = new byte[256];
    
    /**
     * Secure random number generator for UUID generation.
     * Uses SecureRandom for cryptographic strength randomness.
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    static {
        // Initialize the reverse lookup table
        for (int i = 0; i < 256; i++) {
            REVERSE_ALPHABET[i] = -1;
        }
        
        for (byte i = 0; i < 58; i++) {
            char ch = BASE58_ALPHABET.charAt(i);
            REVERSE_ALPHABET[ch] = i;
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private B58UUID() {
        throw new AssertionError("Cannot instantiate utility class");
    }
    
    /**
     * Encodes a 16-byte UUID to a Base58 string.
     * 
     * @param data A 16-byte array representing the UUID
     * @return The Base58-encoded UUID string (exactly 22 characters)
     * @throws B58UUIDException if the input data is not exactly 16 bytes
     */
    public static String encode(byte[] data) throws B58UUIDException {
        if (data == null) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_UUID, "Input data cannot be null");
        }
        if (data.length != 16) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_LENGTH, 
                String.format("Input must be exactly 16 bytes, got %d", data.length));
        }
        
        // Convert bytes to a large integer (using byte array as big-endian)
        // We'll use a byte array to represent the number and do division manually
        byte[] num = data.clone();
        
        // Build result by repeatedly dividing by 58
        StringBuilder result = new StringBuilder();
        
        while (!isAllZero(num)) {
            int remainder = divideBy58(num);
            result.append(BASE58_ALPHABET.charAt(remainder));
        }
        
        // Reverse to get correct order
        result.reverse();
        
        // Pad with leading '1' to ensure exactly 22 characters
        while (result.length() < 22) {
            result.insert(0, '1');
        }
        
        return result.toString();
    }
    
    /**
     * Decodes a Base58 string to a 16-byte UUID.
     * 
     * @param b58 The Base58-encoded string
     * @return The decoded 16-byte UUID
     * @throws B58UUIDException if the input string is invalid
     */
    public static byte[] decode(String b58) throws B58UUIDException {
        if (b58 == null || b58.isEmpty()) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_BASE58, "Empty Base58 string");
        }
        
        // Validate length - should be 22 characters for 16-byte UUID
        if (b58.length() != 22) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_BASE58, 
                String.format("Invalid Base58 length: expected 22, got %d", b58.length()));
        }
        
        // Use manual multiplication approach for exact byte preservation
        byte[] result = new byte[16];
        
        // Process the Base58 string
        for (int i = 0; i < b58.length(); i++) {
            char ch = b58.charAt(i);
            byte digit = REVERSE_ALPHABET[ch];
            
            if (digit == -1) {
                throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_BASE58, 
                    String.format("Invalid character at position %d: %c", i, ch));
            }
            
            // Check for overflow before multiplication
            if (willOverflowOnMultiply(result)) {
                throw new B58UUIDException(B58UUIDException.ErrorType.OVERFLOW, 
                    "Decoded value exceeds maximum UUID value (2^128 - 1)");
            }
            
            multiplyBy58(result);
            
            // Check for overflow before addition
            if (willOverflowOnAdd(result, digit)) {
                throw new B58UUIDException(B58UUIDException.ErrorType.OVERFLOW, 
                    "Decoded value exceeds maximum UUID value (2^128 - 1)");
            }
            
            add(result, digit);
        }
        
        return result;
    }
    
    /**
     * Generates a new random UUID and returns its Base58-encoded representation.
     * 
     * @return A new Base58-encoded UUID
     */
    public static String generate() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        
        // Set UUID version (4) and variant bits
        bytes[6] = (byte) ((bytes[6] & 0x0F) | 0x40); // Version 4
        bytes[8] = (byte) ((bytes[8] & 0x3F) | 0x80); // Variant 10
        
        try {
            return encode(bytes);
        } catch (B58UUIDException e) {
            // This should never happen with 16-byte array
            throw new RuntimeException("Unexpected encoding error", e);
        }
    }
    
    /**
     * Encodes a UUID string to Base58 format.
     * 
     * @param uuidStr A UUID string in standard format (with or without hyphens)
     * @return The Base58-encoded UUID
     * @throws B58UUIDException if the input string is not a valid UUID
     */
    public static String encodeUUID(String uuidStr) throws B58UUIDException {
        if (uuidStr == null) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_UUID, "UUID string cannot be null");
        }
        
        String cleaned = uuidStr.replace("-", "");
        
        if (cleaned.length() != 32) {
            throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_LENGTH, 
                String.format("Invalid UUID length: expected 32 hex characters, got %d", cleaned.length()));
        }
        
        // Validate that all characters are hex
        for (int i = 0; i < cleaned.length(); i++) {
            char ch = cleaned.charAt(i);
            if (!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))) {
                throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_UUID, 
                    String.format("Invalid hex character at position %d: %c", i, ch));
            }
        }
        
        byte[] bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            String hexByte = cleaned.substring(i * 2, i * 2 + 2);
            try {
                bytes[i] = (byte) Integer.parseInt(hexByte, 16);
            } catch (NumberFormatException e) {
                throw new B58UUIDException(B58UUIDException.ErrorType.INVALID_UUID, 
                    String.format("Invalid hex at position %d: %s", i * 2, hexByte), e);
            }
        }
        
        return encode(bytes);
    }
    
    /**
     * Decodes a Base58 string to a standard UUID string format.
     * 
     * @param b58 The Base58-encoded string
     * @return The UUID string in standard format
     * @throws B58UUIDException if the input string is invalid
     */
    public static String decodeToUUID(String b58) throws B58UUIDException {
        byte[] bytes = decode(b58);
        
        return String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
            bytes[0], bytes[1], bytes[2], bytes[3],
            bytes[4], bytes[5], bytes[6], bytes[7],
            bytes[8], bytes[9], bytes[10], bytes[11],
            bytes[12], bytes[13], bytes[14], bytes[15]);
    }
    
    // Helper methods
    
    private static boolean isAllZero(byte[] data) {
        for (byte b : data) {
            if (b != 0) return false;
        }
        return true;
    }
    
    private static int divideBy58(byte[] data) {
        int remainder = 0;
        for (int i = 0; i < data.length; i++) {
            int value = (remainder << 8) + (data[i] & 0xFF);
            data[i] = (byte) (value / 58);
            remainder = value % 58;
        }
        return remainder;
    }
    
    private static void multiplyBy58(byte[] data) {
        int carry = 0;
        for (int i = data.length - 1; i >= 0; i--) {
            int value = (data[i] & 0xFF) * 58 + carry;
            data[i] = (byte) (value & 0xFF);
            carry = value >> 8;
        }
    }
    
    private static void add(byte[] data, int value) {
        int carry = value;
        for (int i = data.length - 1; i >= 0 && carry > 0; i--) {
            int sum = (data[i] & 0xFF) + carry;
            data[i] = (byte) (sum & 0xFF);
            carry = sum >> 8;
        }
    }
    
    /**
     * Checks if multiplying the current value by 58 would cause overflow.
     * 
     * @param data The current byte array
     * @return true if multiplication would overflow
     */
    private static boolean willOverflowOnMultiply(byte[] data) {
        // Check if the most significant byte would overflow
        // Max UUID is 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
        // If we multiply by 58, we need to ensure result fits in 128 bits
        
        // Quick check: if any of the top bits are set, multiplication will overflow
        if ((data[0] & 0xFF) > 4) {
            return true;
        }
        
        // More precise check: simulate multiplication and check for carry beyond 16 bytes
        int carry = 0;
        for (int i = data.length - 1; i >= 0; i--) {
            long value = ((long)(data[i] & 0xFF)) * 58L + carry;
            carry = (int)(value >> 8);
        }
        
        return carry > 0;
    }
    
    /**
     * Checks if adding a value would cause overflow.
     * 
     * @param data The current byte array
     * @param value The value to add
     * @return true if addition would overflow
     */
    private static boolean willOverflowOnAdd(byte[] data, int value) {
        // Check if adding would produce a carry beyond the first byte
        int carry = value;
        for (int i = data.length - 1; i >= 0 && carry > 0; i--) {
            int sum = (data[i] & 0xFF) + carry;
            carry = sum >> 8;
        }
        
        return carry > 0;
    }
}
