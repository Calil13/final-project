package org.example.finalproject.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void adminInitializer() {

        String adminEmail = "adminadmin1302@gmail.com";

        Users admin = usersRepository.findByEmailAndDeletedFalse(adminEmail)
                .orElseGet(Users::new);

        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("Admin@1302"));
        admin.setPhone("+994508036261");
        admin.setName("Admin");
        admin.setSurname("Admin");
        admin.setUserRole(UserRole.ADMIN);
        admin.setIsActive(false);
        admin.setDeleted(false);

        usersRepository.save(admin);
    }
}
