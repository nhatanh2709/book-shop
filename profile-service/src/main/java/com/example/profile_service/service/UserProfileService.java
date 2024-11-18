package com.example.profile_service.service;

import com.example.profile_service.dto.request.ProfileCreationRequest;
import com.example.profile_service.dto.request.ProfileUpdateRequest;
import com.example.profile_service.dto.response.UserProfileResponse;
import com.example.profile_service.dto.response.UserResponse;
import com.example.profile_service.entity.UserProfile;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import com.example.profile_service.mapper.UserProfileMapper;
import com.example.profile_service.repository.UserProfileRepository;
import com.example.profile_service.repository.httpClient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    IdentityClient identityClient;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getProfile() {
        UserResponse userResponse = identityClient.getMyInfo().getResult();
        UserProfile userProfile = userProfileRepository.findByUserId(userResponse.getId()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        return userProfileMapper.toUserProfileReponse(userProfile);
    }
//
    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        UserResponse userResponse = identityClient.getMyInfo().getResult();
        UserProfile userProfile = userProfileRepository.findByUserId(userResponse.getId()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        if (request.getProfilePic() != null) {
            userProfile.setProfilePic(request.getProfilePic());
        }
        if (request.getDob() != null) {
            userProfile.setDob(request.getDob());
        }
        if (request.getCity() != null) {
            userProfile.setCity(request.getCity());
        }
        if (request.getLastName() != null) {
            userProfile.setLastName(request.getLastName());
        }
        if (request.getFirstName() != null) {
            userProfile.setFirstName(request.getFirstName());
        }

        return userProfileMapper.toUserProfileReponse(userProfileRepository.save(userProfile));
    }
//
//
    public void deleteProfile(String id) {
        userProfileRepository.deleteById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        var profiles = userProfileRepository.findAll();
        return profiles.stream().map(userProfileMapper::toUserProfileReponse).toList();
    }
}
