package com.group2.volunteer.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerProfileDTO {

    @Size(max = 100, message = "Tên tổ chức không được vượt quá 100 ký tự")
    private String fullName;

    private String phoneNumber;

    @Size(max = 50, message = "Tỉnh/Thành phố không được vượt quá 50 ký tự")
    private String city;

    @Size(max = 255, message = "Địa chỉ trụ sở không được vượt quá 255 ký tự")
    private String address;

    private String avatarUrl;

    private MultipartFile avatarFile;

    private String website;

    @Size(max = 2000, message = "Mô tả tổ chức tối đa 2000 ký tự")
    private String description;

    public OrganizerProfileDTO(String fullName, String phoneNumber, String city, String address, String avatarUrl, String website, String description) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.website = website;
        this.description = description;
    }
}
