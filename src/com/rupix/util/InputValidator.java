package com.rupix.util;

public class InputValidator {

    public static boolean isValidUpi(String upi) {
        return upi != null && upi.matches("[a-zA-Z0-9._-]+@[a-zA-Z]+");
    }

    public static boolean isValidAmount(long amountPaise) {
        return amountPaise > 0;
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty();
    }
}