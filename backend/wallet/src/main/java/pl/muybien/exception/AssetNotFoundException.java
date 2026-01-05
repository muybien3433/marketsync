package pl.muybien.exception;

public class AssetNotFoundException extends ServiceException {
    public AssetNotFoundException(String message) {
        super(404, "ASSET_NOT_FOUND", message);
    }
}
