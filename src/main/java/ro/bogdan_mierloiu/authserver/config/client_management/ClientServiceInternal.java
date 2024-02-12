package ro.bogdan_mierloiu.authserver.config.client_management;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdan_mierloiu.authserver.config.CustomEncoder;
import ro.bogdan_mierloiu.authserver.entity.ClientInternal;
import ro.bogdan_mierloiu.authserver.repository.ClientInternalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class ClientServiceInternal {

    private final CustomEncoder encoder;

    private final ClientInternalRepository clientInternalRepository;

    public List<ClientInternal> getAll() {
        return clientInternalRepository.findAll().stream()
                .peek(client -> client.setClientSecret(encoder.decrypt(client.getClientSecret())))
                .toList();
    }
}
