package com.discern.discern.dao;


import com.discern.discern.entity.Baseface;
import com.discern.discern.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FaceJpaDao extends JpaRepository<Baseface,Integer>,JpaSpecificationExecutor<Baseface> {

}
