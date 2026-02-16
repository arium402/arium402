package com.team.arium.admin.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode("1111");
        System.out.println("암호화된 1111: " + hashedPw);

        // 매치 테스트
        boolean result = encoder.matches("1111", "$2a$10$rz0tIZBWn/uWai3Wy5nrfejr.NUdjDaKw6ymgU.ZiozZKMamrKZz2");
        System.out.println("입력한 1111가 해시와 일치하는가? " + result);
        

    }
}
