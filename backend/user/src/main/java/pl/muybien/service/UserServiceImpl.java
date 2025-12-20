package pl.muybien.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.feign.IamClient;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IamClient iamClient;
}
