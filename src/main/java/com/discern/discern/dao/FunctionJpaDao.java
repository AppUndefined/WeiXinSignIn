package com.discern.discern.dao;

import com.discern.discern.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FunctionJpaDao extends JpaRepository<Function, Integer>,JpaSpecificationExecutor<Function> {

}
