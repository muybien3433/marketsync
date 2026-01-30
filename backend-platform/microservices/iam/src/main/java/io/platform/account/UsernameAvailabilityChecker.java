package io.platform.account;

@FunctionalInterface
public interface UsernameAvailabilityChecker {
    boolean isAvailable(String username);
}
