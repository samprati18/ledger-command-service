package com.assignment.ledger.util;

import java.util.Random;

public class AccountNumberGenerator {
    // Define the length of the account number
    private static final int ACCOUNT_NUMBER_LENGTH = 8;

    // Method to generate a random 8-digit account number
    public static String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumberBuilder = new StringBuilder();

        // Generate random digits until the account number reaches the desired length
        while (accountNumberBuilder.length() < ACCOUNT_NUMBER_LENGTH) {
            // Generate a random digit between 0 and 9
            int digit = random.nextInt(10);
            accountNumberBuilder.append(digit);
        }

        return accountNumberBuilder.toString();
    }
}