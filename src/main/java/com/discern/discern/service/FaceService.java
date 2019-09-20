package com.discern.discern.service;


import com.discern.discern.entity.Baseface;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FaceService {

    void save(Baseface face);

    void deketeById(Integer faceId);

    List<Baseface> find(Baseface face);

    Page<Baseface> findLocalHistory(Integer department, Baseface baseface);
}
