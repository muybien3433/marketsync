package pl.muybien.service.user;

import pl.muybien.dto.request.IamUserCreateRequest;
import pl.muybien.dto.response.IamUserCreatedResponse;

public interface IamUserService {
    IamUserCreatedResponse createUser(IamUserCreateRequest request);
}
