package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.RefreshTokenCreationRequest;
import com.example.identity_service.entity.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshToken toRefreshToken(RefreshTokenCreationRequest request);
}
