package com.discern.discern.dao;


import com.discern.discern.entity.Function;
import com.discern.discern.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserJpaDao extends JpaRepository<User,Integer>,JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    List<User> findByNicknameIsNotNull();
    User findByPhone(String phone);
}
