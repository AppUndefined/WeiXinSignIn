package com.discern.discern.service.impl;

import com.discern.discern.dao.FunctionJpaDao;
import com.discern.discern.dao.RoleJpaDao;
import com.discern.discern.entity.Function;
import com.discern.discern.entity.Role;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * @author:XiaoYao
 * @date:2018/7/3 14:57
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleJpaDao roleJpaDao;

    @Autowired
    private FunctionJpaDao functionJpaDao;



    @Autowired
    private RedisService redisService;



    @Override
    public Boolean save(Role role, List<Integer> functionIds) {
        List<Function> functions = functionJpaDao.findAllById(functionIds);
        if(functionIds != null){
            role.setFunctionList(new HashSet<Function>(functions));
        }else {
            role.setFunctionList(null);
        }
        Role perm = roleJpaDao.save(role);
        if (null != perm && perm.getId() != null) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean update(Role permission, List<Integer> functionIds) {
        Role perm = null;
        if (null != permission && null != permission.getId() && null != functionIds && functionIds.size() > 0) {
            List<Function> functions = functionJpaDao.findAllById(functionIds);
            permission.setFunctionList(new HashSet<Function>(functions));
            perm = roleJpaDao.saveAndFlush(permission);
        } else if (null != permission && null != permission.getId()) {
            perm = roleJpaDao.saveAndFlush(permission);
        }
        if (null != perm && perm.getId() != null) {
            return true;
        }
        return false;
    }


    @Override
    public Boolean deleteRoleById(Integer permissionId) {
        Optional<Role> role = roleJpaDao.findById(permissionId);
        if (role.isPresent()) {
            roleJpaDao.delete(role.get());
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteRoleByIds(List<Integer> permissionIds) {
        List<Role> permissions = roleJpaDao.findAllById(permissionIds);
        if (null != permissions && permissions.size() > 0) {
            roleJpaDao.deleteAll(permissions);
            return true;
        }
        return false;
    }

    @Override
    public Role findRoleById(Integer permissionId) {
        return roleJpaDao.findById(permissionId).get();
    }

    /**
     * 根据当前用户的部门查询旗下的角色如果为空查询所有
     * @param department
     * @return
     */
    @Override
    public List<Role> findByDepartment(Integer department) {
        if(department==null){
          return   roleJpaDao.findAll();
        }
        return roleJpaDao.findByDepartment(department);
    }

    @Override
    public Role findById(Integer roleId) {
        return roleJpaDao.findById(roleId).get();
    }


}
