package com.group2.volunteer.service;

import com.group2.volunteer.constant.UserStatus;
import com.group2.volunteer.exception.AuthException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.dto.RegisterDTO;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public User authenticate(LoginDTO loginDTO) {
        User user = userRepository.findByEmailAndPass(loginDTO.getEmail(),
                loginDTO.getPassword()).orElseThrow(() -> new AuthException("Email hoặc mật khẩu không hợp lệ!"));

        if (UserStatus.BLOCKED.name().equals(user.getStatus())) {
            throw new AuthException("Tài khoản của bạn đã bị chặn.");
        }

        if (UserStatus.PENDING.name().equals(user.getStatus())) {
            throw new AuthException("Tài khoản đang chờ kích hoạt.");
        }
        return user;
    }

    @Override
    public Long register(RegisterDTO registerDTO) {
        return 0L;
    }
}
