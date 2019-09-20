package com.discern.discern.service;


import com.discern.discern.entity.Role;

import java.util.List;

public interface RoleService {

    /**
     * 新增权限
     * @param Role
     * @return
     */
    public Boolean save(Role Role, List<Integer> functionIds);

    /**
     * 根据ID修改角色信息
     * @param Role
     * @return
     */
    public Boolean update(Role Role, List<Integer> functionIds);


    /**
     * 根据ID删除角色
     * @param permissionId
     * @return
     */
    public Boolean deleteRoleById(Integer permissionId);

    /**
     * 根据ID集批量删除
     * @param permissionIds
     * @return
     */
    public Boolean deleteRoleByIds(List<Integer> permissionIds);

    /**
     * 根据ID查询角色信息
     * @param RoleId
     * @return
     */
    public Role findRoleById(Integer RoleId);

    List<Role> findByDepartment(Integer department);

    Role findById(Integer roleId);
}
