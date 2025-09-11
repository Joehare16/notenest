package com.nestenote.notenest.repository;

import com.nestenote.notenest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findById(Long id);
        Optional<User> findByEmail(String email);
}
