package com.washywash.repository;

import com.washywash.model.User;
import java.util.List;

public interface UserRepository {
    void save(User user);
    void update(User user);
    void delete(String kodeUser);
    User findById(String kodeUser);
    User findByEmailOrKode(String input);
    List<User> findAll();
}
