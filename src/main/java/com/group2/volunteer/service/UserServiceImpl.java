package com.group2.volunteer.service;

import com.group2.volunteer.constant.UserStatus;
import com.group2.volunteer.dto.OrganizerProfileDTO;
import com.group2.volunteer.exception.AuthException;
import com.group2.volunteer.exception.InvalidProfileException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.dto.LoginRequest;
import com.group2.volunteer.dto.VolunteerProfileUpdateRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        return user;
    }

    @Override
    public String getBadgeName(Long attendedProjectCount) {
        long projectCount = attendedProjectCount == null ? 0 : attendedProjectCount;

        if (projectCount >= 10) {
            return "Community Hero";
        }

        if (projectCount >= 3) {
            return "Kind Heart";
        }

        return "New Volunteer";
    }

    @Override
    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameAndPass(loginRequest.getUsername(),
                loginRequest.getPassword()).orElseThrow(() -> new AuthException("Email hoặc mật khẩu không hợp lệ!"));

        if (UserStatus.BLOCKED.name().equals(user.getStatus())) {
            throw new AuthException("Tài khoản của bạn đã bị chặn.");
        }

        if (UserStatus.PENDING.name().equals(user.getStatus())) {
            throw new AuthException("Tài khoản đang chờ kích hoạt.");
        }
        return user;
    }

    @Override
    public void register(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAllPendingUsers() {
        return userRepository.findByStatus("PENDING");
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public List<User> getUsersByFilter(String keyword, String role, String status) {
        String cleanKeyword = (keyword != null) ? keyword.trim() : null;
        return userRepository.getUsersByFilter(cleanKeyword, role, status);
    }

    @Override
    public void updateUserByAdmin(Long id, User userForm) {
        User existingUser = getUserById(id);

        existingUser.setFullName(userForm.getFullName());
        existingUser.setEmail(userForm.getEmail());
        existingUser.setPhoneNumber(userForm.getPhoneNumber());
        existingUser.setRole(userForm.getRole());
        existingUser.setStatus(userForm.getStatus());

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User updateVolunteerProfile(Long userId, VolunteerProfileUpdateRequest profileRequest) {
        validateProfileRequest(profileRequest);

        User user = getUserById(userId);
        if (!"VOLUNTEER".equalsIgnoreCase(user.getRole())) {
            throw new InvalidProfileException("Chỉ Volunteer mới có thể cập nhật hồ sơ này.");
        }

        user.setFullName(profileRequest.getFullName().trim());
        user.setPhoneNumber(emptyToNull(profileRequest.getPhoneNumber()));
        user.setDateOfBirth(profileRequest.getDateOfBirth());
        user.setGender(emptyToNull(profileRequest.getGender()));
        user.setCity(emptyToNull(profileRequest.getCity()));
        user.setAddress(emptyToNull(profileRequest.getAddress()));
        user.setAvatarUrl(emptyToNull(profileRequest.getAvatarUrl()));
        user.setBio(emptyToNull(profileRequest.getBio()));

        return userRepository.save(user);
    }

    private void validateProfileRequest(VolunteerProfileUpdateRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new InvalidProfileException("Họ và tên không được để trống.");
        }

        String phoneNumber = emptyToNull(request.getPhoneNumber());
        if (phoneNumber != null && !phoneNumber.matches("^[+]?[0-9]{10,15}$")) {
            throw new InvalidProfileException("Số điện thoại không hợp lệ.");
        }

        if (request.getDateOfBirth() != null
                && !request.getDateOfBirth().isBefore(LocalDate.now())) {
            throw new InvalidProfileException("Ngày sinh phải là một ngày trong quá khứ.");
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();

    }

    @Override
    public OrganizerProfileDTO getOrganizerProfileDTO(Long userId) {
        User user = getUserById(userId);
        return new OrganizerProfileDTO(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCity(),
                user.getAddress(),
                user.getAvatarUrl(),
                user.getWebsite(),
                user.getDescription()
        );
    }

    @Override
    @Transactional
    public void updateOrganizerProfile(Long userId, OrganizerProfileDTO profileDTO) {
        User user = getUserById(userId);

        user.setFullName(profileDTO.getFullName());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        user.setCity(profileDTO.getCity());
        user.setAddress(profileDTO.getAddress());
        user.setWebsite(profileDTO.getWebsite());
        user.setDescription(profileDTO.getDescription());

        if (profileDTO.getAvatarFile() != null && !profileDTO.getAvatarFile().isEmpty()) {
            try {
                MultipartFile file = profileDTO.getAvatarFile();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                String uploadDir = "src/main/resources/static/uploads/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                user.setAvatarUrl("/uploads/" + fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        userRepository.save(user);
    }
}
