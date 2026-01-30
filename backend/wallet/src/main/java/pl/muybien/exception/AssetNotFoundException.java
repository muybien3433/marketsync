package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class AssetNotFoundException extends ServiceException {
    public AssetNotFoundException(String message) {
        super(404, "ASSET_NOT_FOUND", message);
    }
}
