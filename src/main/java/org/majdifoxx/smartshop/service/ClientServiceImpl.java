package org.majdifoxx.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.ClientRequestDTO;
import org.majdifoxx.smartshop.dto.response.ClientResponseDTO;
import org.majdifoxx.smartshop.entity.Client;
import org.majdifoxx.smartshop.entity.User;
import org.majdifoxx.smartshop.enums.CustomerTier;
import org.majdifoxx.smartshop.enums.UserRole;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.exception.ResourceNotFoundException;
import org.majdifoxx.smartshop.mapper.ClientMapper;
import org.majdifoxx.smartshop.repository.ClientRepository;
import org.majdifoxx.smartshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final LoyaltyService loyaltyService;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO request) {
        // Check email uniqueness
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already registered: " + request.getEmail());
        }

        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException("Username already taken: " + request.getUsername());
        }

        // Create User entity (CLIENT role)
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(UserRole.CLIENT)
                .build();

        // SAVE USER FIRST (important!)
        User savedUser = userRepository.save(user);

        // Create Client entity
        Client client = clientMapper.toEntity(request);
        client.setUser(savedUser);
        client.setTier(CustomerTier.BASIC);

        // Save client
        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }


    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toResponseDTO(client);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        // Check email uniqueness if changed
        if (!client.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already registered: " + request.getEmail());
        }

        clientMapper.updateEntityFromDTO(request, client);
        Client updated = clientRepository.save(client);
        return clientMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        clientRepository.delete(client);
    }

    @Override
    public void updateClientStatsAfterOrderConfirmation(Client client, BigDecimal orderTotalTTC) {
        // PDF: "Suivre automatiquement: Statistiques (totalOrders, totalSpent)"
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(orderTotalTTC));

        // PDF: "Date de première et dernière commande"
        client.setLastOrderDate(LocalDateTime.now());
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(LocalDateTime.now());
        }

        // PDF: "Mise à jour du niveau après chaque commande confirmée"
        CustomerTier newTier = loyaltyService.calculateTier(client);
        client.setTier(newTier);

        clientRepository.save(client);
    }

    @Override
    public Client getClientEntity(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}
