package com.group2.volunteer.service;

import com.group2.volunteer.constant.UserStatus;
import com.group2.volunteer.dto.OrganizerProfileDTO;
import com.group2.volunteer.exception.AuthException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.dto.LoginRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        if (user.getTotalHours() == null) {
            user.setTotalHours(0);
        }
        return user;
    }

    @Override
    public String getBadgeName(Integer totalHours) {
        int hours = totalHours == null ? 0 : totalHours;

        if (hours >= 50) {
            return "Community Hero";
        }

        if (hours >= 10) {
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

        // Xử lý Upload file ảnh nếu người dùng chọn file mới
        if (profileDTO.getAvatarFile() != null && !profileDTO.getAvatarFile().isEmpty()) {
            try {
                MultipartFile file = profileDTO.getAvatarFile();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                // Đường dẫn lưu file vào thư mục static/uploads
                String uploadDir = "src/main/resources/static/uploads/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Lưu đường dẫn avatar vào database
                user.setAvatarUrl("/uploads/" + fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        userRepository.save(user);
    }
}
