package com.discern.discern.service;


import com.discern.discern.entity.Function;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FunctionService {
    void save(Function function);

    public List<Function> findAllByIds(List<Integer> ids);

    Function findById(Integer functionId);

    Page<Function> findAll(Integer pageNumber, Integer pageSize);

    void deleteById(Integer functionId);

    void updateFunction(Function function);

    void saveAll(List<Function> functions);

    Page<Function> findByFunction(Function function, Integer pageNumber, Integer pageSize);
}
