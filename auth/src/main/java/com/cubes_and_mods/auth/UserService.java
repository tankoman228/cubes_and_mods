package com.cubes_and_mods.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.auth.db.User;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    public Optional<User> findByEmail(String Email) {
        return repository.findAll().stream().filter(x -> x.getEmail().equals(Email)).findFirst();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
	
}
