package com.teachsync.services.user;

import com.teachsync.entities.Role;
import com.teachsync.entities.User;
import com.teachsync.repositories.UserRepository;
import com.teachsync.services.role.RoleService;
import com.teachsync.utils.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Override
    public User signup(User user) throws Exception {
        boolean isExists = userRepository.existsByUsernameAndStatusNot(user.getUsername(), Status.DELETED);

        if (isExists) {
            System.out.println(isExists);
            throw new IllegalArgumentException("Already exists account with username: " + user.getUsername());
        }

        Role role = roleService.getById(user.getRoleId());

        if (role == null) {
            throw new IllegalArgumentException("No role found with roleId: " + user.getRoleId());
        }

        user.setRole(role);

        user = userRepository.saveAndFlush(user);

        return user;
    }

    @Override
    public User login(String username, String password) throws Exception {
        Optional<User> user =
                userRepository.findByUsernameAndPasswordAndStatusNot(username, password, Status.DELETED);

        return user.orElse(null);
    }

    @Override
    public List<User> getListUserByType(Long type) {
        System.out.println("type = "+type);
        List<User> x = userRepository.findAllByRoleId(type);
        System.out.println(x);
        return x;
    }

}
