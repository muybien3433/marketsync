package pl.muybien.account;

@FunctionalInterface
public interface UsernameAvailabilityChecker {
    boolean isAvailable(String username);
}
