package com.group2.volunteer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterOrganizerRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Tên tổ chức không được để trống")
    @Size(max = 100, message = "Tên tổ chức không được vượt quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại liên hệ không được để trống")
    private String phoneNumber;

    @Size(max = 50, message = "Tỉnh/Thành phố không được vượt quá 50 ký tự")
    private String city;

    @Size(max = 255, message = "Địa chỉ trụ sở không được vượt quá 255 ký tự")
    private String address;

    private String website;

    @Size(max = 2000, message = "Mô tả tổ chức tối đa 2000 ký tự")
    private String description;
}
