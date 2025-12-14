package pl.muybien.service.auth;

import pl.muybien.dto.request.IamUserLoginRequest;
import pl.muybien.dto.response.IamUserLoginResponse;

public interface IamAuthService {
    IamUserLoginResponse loginUser(IamUserLoginRequest request);
}
