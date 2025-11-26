package org.majdifoxx.smartshop.mapper;

import org.majdifoxx.smartshop.dto.request.ClientRequestDTO;
import org.majdifoxx.smartshop.dto.response.ClientResponseDTO;
import org.majdifoxx.smartshop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDTO toResponseDTO(Client client);

    Client toEntity(ClientRequestDTO dto);

    void updateEntityFromDTO(ClientRequestDTO dto, @MappingTarget Client client);
}
