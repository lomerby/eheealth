package com.ehealthcare.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordUtil provides secure password hashing and verification.
 * Uses SHA-256 with a random salt (no external libraries required).
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a plain text password with a randomly generated salt.
     * Returns a stored string in the format: "salt:hash"
     */
    public static String hash(String plainPassword) {
        byte[] salt = generateSalt();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hash = computeHash(plainPassword, salt);
        return saltBase64 + ":" + hash;
    }

    /**
     * Verifies a plain text password against a stored "salt:hash" string.
     */
    public static boolean verify(String plainPassword, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) return false;
        String[] parts = storedHash.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String expectedHash = parts[1];
        String actualHash = computeHash(plainPassword, salt);
        return expectedHash.equals(actualHash);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static String computeHash(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
