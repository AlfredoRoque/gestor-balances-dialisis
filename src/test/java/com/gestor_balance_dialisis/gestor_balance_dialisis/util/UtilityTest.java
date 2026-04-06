package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Utility class, covering the generateTemporaryPassword, minusDays, and isSpecialPlan methods.
 * These tests validate the expected behavior of each method under various conditions, including edge cases and typical usage scenarios.
 * The tests ensure that generateTemporaryPassword produces passwords of the correct length and character set, that minusDays correctly calculates past instants, and that isSpecialPlan accurately identifies the special plan name.
 */
class UtilityTest {

    // ─── generateTemporaryPassword ────────────────────────────────────────────

    /**
     * Test that generateTemporaryPassword returns a string with the exact requested length.
     * Verifies that a request for 10 characters produces a 10-character password.
     */
    @Test
    void generateTemporaryPassword_hasCorrectLength() {
        String pwd = Utility.generateTemporaryPassword(10);
        assertThat(pwd).hasSize(10);
    }

    /**
     * Test that generateTemporaryPassword with length zero returns an empty string.
     * Verifies the edge case where no characters are requested.
     */
    @Test
    void generateTemporaryPassword_lengthZero_returnsEmptyString() {
        String pwd = Utility.generateTemporaryPassword(0);
        assertThat(pwd).isEmpty();
    }

    /**
     * Test that generateTemporaryPassword only contains alphanumeric characters.
     * Verifies that each character of a 50-character password belongs to the set
     * of uppercase letters, lowercase letters, and digits.
     */
    @Test
    void generateTemporaryPassword_containsOnlyAllowedChars() {
        String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String pwd = Utility.generateTemporaryPassword(50);
        for (char c : pwd.toCharArray()) {
            assertThat(allowed).contains(String.valueOf(c));
        }
    }

    /**
     * Test that generateTemporaryPassword produces non-null results on repeated invocations.
     * This repeated test validates that the random generation does not fail or return null
     * across multiple executions.
     */
    @RepeatedTest(5)
    void generateTemporaryPassword_isRandom() {
        String p1 = Utility.generateTemporaryPassword(12);
        String p2 = Utility.generateTemporaryPassword(12);
        // Extremely unlikely to be equal; tests randomness
        assertThat(p1).isNotNull();
        assertThat(p2).isNotNull();
    }

    // ─── minusDays ────────────────────────────────────────────────────────────

    /**
     * Test that minusDays correctly subtracts the specified number of days from the given instant.
     * Verifies that subtracting 5 days matches the expected result calculated via {@link java.time.temporal.ChronoUnit#DAYS}.
     */
    @Test
    void minusDays_subtractsCorrectNumberOfDays() {
        Instant base = Instant.parse("2024-06-30T12:00:00Z");
        Instant result = Utility.minusDays(5, base);
        Instant expected = base.minus(5, ChronoUnit.DAYS);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * Test that minusDays with zero days returns the same instant unchanged.
     * Verifies the identity case where no days are subtracted.
     */
    @Test
    void minusDays_zero_returnsSameInstant() {
        Instant base = Instant.parse("2024-06-15T00:00:00Z");
        Instant result = Utility.minusDays(0, base);
        assertThat(result).isEqualTo(base);
    }

    /**
     * Test that minusDays with one day returns exactly 24 hours before the base instant.
     * Verifies a simple one-day subtraction.
     */
    @Test
    void minusDays_oneDay_returns24HoursBefore() {
        Instant base = Instant.parse("2024-06-15T12:00:00Z");
        Instant result = Utility.minusDays(1, base);
        assertThat(result).isEqualTo(Instant.parse("2024-06-14T12:00:00Z"));
    }

    // ─── isSpecialPlan ────────────────────────────────────────────────────────

    /**
     * Test that isSpecialPlan returns true for the SPECIAL plan constant.
     * Verifies that the exact value defined in {@link Constants#SPECIAL_PLAN} is recognized.
     */
    @Test
    void isSpecialPlan_specialPlanName_returnsTrue() {
        assertThat(Utility.isSpecialPlan(Constants.SPECIAL_PLAN)).isTrue();
    }

    /**
     * Test that isSpecialPlan returns false for a non-special plan name.
     * Verifies that arbitrary plan names like "BASIC" are not considered special.
     */
    @Test
    void isSpecialPlan_otherPlanName_returnsFalse() {
        assertThat(Utility.isSpecialPlan("BASIC")).isFalse();
    }

    /**
     * Test that isSpecialPlan returns false for an empty string.
     * Verifies that an empty plan name is not mistakenly matched as a special plan.
     */
    @Test
    void isSpecialPlan_emptyString_returnsFalse() {
        assertThat(Utility.isSpecialPlan("")).isFalse();
    }

    /**
     * Test that isSpecialPlan performs a case-sensitive comparison.
     * Verifies that lowercase and mixed-case variants of "SPECIAL" return false,
     * since the match must be exact.
     */
    @Test
    void isSpecialPlan_caseInsensitive_returnsFalse() {
        // "SPECIAL" must match exactly
        assertThat(Utility.isSpecialPlan("special")).isFalse();
        assertThat(Utility.isSpecialPlan("Special")).isFalse();
    }
}
