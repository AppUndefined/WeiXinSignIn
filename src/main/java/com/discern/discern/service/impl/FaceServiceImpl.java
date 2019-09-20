package com.discern.discern.service.impl;

import com.discern.discern.dao.FaceJpaDao;
import com.discern.discern.entity.Baseface;
import com.discern.discern.service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

@Service
public class FaceServiceImpl implements FaceService {
    @Autowired
    private FaceJpaDao faceJpaDao;

    /**
     * 保存人脸信息到本地
     * @param face
     */
    @Override
    public void save(Baseface face) {
        faceJpaDao.save(face);
    }

    /**
     * 根据id删除人脸信息
     * @param faceId
     */
    @Override
    public void deketeById(Integer faceId) {
        faceJpaDao.deleteById(faceId);
    }

    /**
     * 根据人脸对象查询
     * @param face
     * @return
     */
    @Override
    public List<Baseface> find(Baseface face) {
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() ;//构建对象
        Example<Baseface> basefaceExample = Example.of(face, matcher);
        return  faceJpaDao.findAll(basefaceExample);
    }

    /**
     * 查询最近10条人脸记录
     * @return
     */
    @Override
    public Page<Baseface> findLocalHistory(Integer department, Baseface baseface) {
        PageRequest pageRequest = new PageRequest(baseface.getPageNumber(), 24);
        return faceJpaDao.findAll(new Specification<Baseface>() {
            @Override
            public Predicate toPredicate(Root<Baseface> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                ArrayList<Predicate> list = new ArrayList<>();
                Path<Object> createDate = root.get("createDate");
                CriteriaQuery<?> createDate1 = query.orderBy(toOrders(new Sort(Sort.Direction.DESC, "createDate"), root, cb));
                if(department!=null) {
                    list.add(cb.equal(root.get("department").as(Integer.class), department));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageRequest);
    }
}
