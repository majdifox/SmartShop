package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.dto.request.ClientRequestDTO;
import org.majdifoxx.smartshop.dto.response.ClientResponseDTO;
import org.majdifoxx.smartshop.entity.Client;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {
    ClientResponseDTO createClient(ClientRequestDTO request);
    ClientResponseDTO getClientById(Long id);
    List<ClientResponseDTO> getAllClients();
    ClientResponseDTO updateClient(Long id, ClientRequestDTO request);
    void deleteClient(Long id);
    void updateClientStatsAfterOrderConfirmation(Client client, BigDecimal orderTotalTTC);
    Client getClientEntity(Long id);
}
