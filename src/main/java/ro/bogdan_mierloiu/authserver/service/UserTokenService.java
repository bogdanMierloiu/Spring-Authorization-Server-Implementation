package ro.bogdan_mierloiu.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdan_mierloiu.authserver.config.CustomEncoder;
import ro.bogdan_mierloiu.authserver.entity.UserToken;
import ro.bogdan_mierloiu.authserver.repository.UserTokenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    public List<UserToken> getAll() {
        return userTokenRepository.findAll();
    }

    public void deleteByToken(String token, CustomEncoder customEncoder) {
        getAll().forEach(userToken -> {
                    String tokenDecrypted = customEncoder.decrypt(userToken.getToken());
                    if (tokenDecrypted.equals(token)) {
                        userTokenRepository.delete(userToken);
                    }
                }
        );
    }

}
