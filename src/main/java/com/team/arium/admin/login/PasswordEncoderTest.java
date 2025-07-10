package com.team.arium.admin.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode("1111");
        System.out.println("암호화된 1111: " + hashedPw);

        // 매치 테스트
        boolean result = encoder.matches("1111", "$2a$10$Gvv3UJIoBxYPpUGJF0NAnO6AUOanTry170v20YJjHzWenh4sCEQVO");
        System.out.println("입력한 1111가 해시와 일치하는가? " + result);
        

    }
}
