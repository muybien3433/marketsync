package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class ServiceNotFoundException extends ServiceException {
    public ServiceNotFoundException(String message) {
        super(404, "SERVICE_NOT_FOUND", message);
    }
}
