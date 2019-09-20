package com.discern.discern.dao;

import com.discern.discern.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoleJpaDao extends JpaRepository<Role, Integer>,JpaSpecificationExecutor<Role> {
    List<Role> findByDepartment(Integer department);
}
