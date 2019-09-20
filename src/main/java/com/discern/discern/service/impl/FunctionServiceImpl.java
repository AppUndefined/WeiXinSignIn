package com.discern.discern.service.impl;

import com.discern.discern.dao.FunctionJpaDao;
import com.discern.discern.entity.Function;
import com.discern.discern.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FunctionServiceImpl implements FunctionService {
    @Autowired
    private FunctionJpaDao functionJpaDao;

    @Override
    public List<Function> findAllByIds(List<Integer> ids){
        return functionJpaDao.findAllById(ids);
    }
    @Override
    public void save(Function function) {
        functionJpaDao.save(function);
    }

    @Override
    public Function findById(Integer functionId) {
        Optional<Function> function = functionJpaDao.findById(functionId);
        if(function.isPresent()){
            return function.get();
        }else {
            return  null;
        }
    }

    @Override
    public Page<Function> findAll(Integer pageNumber, Integer pageSize) {
        return functionJpaDao.findAll(new PageRequest(pageNumber,pageSize));
    }

    @Override
    public void deleteById(Integer functionId) {
        functionJpaDao.deleteById(functionId);
    }

    @Override
    public void updateFunction(Function function) {
        functionJpaDao.save(function);
    }

    @Override
    public void saveAll(List<Function> functions) {
        functionJpaDao.saveAll(functions);
    }

    @Override
    public Page<Function> findByFunction(Function function, Integer pageNumber, Integer pageSize) {
        if(function==null){
            return functionJpaDao.findAll(new PageRequest(pageNumber,pageSize));
        }
        Page<Function> functions = functionJpaDao.findAll(new Specification<Function>() {
            @Override
            public Predicate toPredicate(Root<Function> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                ArrayList<Predicate> list = new ArrayList<>();
                if(function.getFunction_id()!=null)
                    list.add(criteriaBuilder.equal(root.get("function_id").as(Integer.class),function.getFunction_id()));
                    if(function.getPermission()!=null)
                        list.add(criteriaBuilder.like(root.get("permission").as(String.class),"%"+function.getPermission()+"%"));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, new PageRequest(pageNumber, pageSize));
        return functions;
    }
}
