package com.lukbol.ProjectNoSQL.Configs;

import com.lukbol.ProjectNoSQL.Models.Privilege;
import com.lukbol.ProjectNoSQL.Models.Role;
import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Repositories.PrivilegeRepository;
import com.lukbol.ProjectNoSQL.Repositories.RoleRepository;
import com.lukbol.ProjectNoSQL.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = true;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;

        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege updatePrivilege = createPrivilegeIfNotFound("UPDATE_PRIVILEGE");
        Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, updatePrivilege, deletePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);

        List<Privilege> clientPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, updatePrivilege, deletePrivilege);
        createRoleIfNotFound("ROLE_CLIENT", clientPrivileges);

        if (userRepository.findByEmail("admin@testowy.com") == null) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            User adminUser = new User();
            adminUser.setUsername("Admin");
            adminUser.setName("Jan");
            adminUser.setSurname("Kowalski");
            adminUser.setPassword(passwordEncoder.encode("admin1234"));
            adminUser.setEmail("admin@testowy.com");
            adminUser.setPhoneNumber("123456789");
            adminUser.setActivated(true);
            adminUser.setRoles(List.of(adminRole));
            userRepository.save(adminUser);
        }

        alreadySetup = true;
    }

    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(new ArrayList<>(privileges));
            roleRepository.save(role);
        }
        return role;
    }
}

