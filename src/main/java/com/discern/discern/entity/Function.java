package com.discern.discern.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "functions")
public class Function   implements Serializable {
    private static final long serialVersionUID = 1L;
    //功能序号

    @Id
    @GeneratedValue
    private Integer function_id;
    //权限字符串

    private String permission;
    //方法名

    private String name;

    @ManyToMany
    @JoinTable(name = "Role_Function", joinColumns = { @JoinColumn(name = "function_id") }, inverseJoinColumns = {
            @JoinColumn(name = "Role_id") })
    //角色

    private List<Role> roleList = new ArrayList<>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getFunction_id() {
        return function_id;
    }

    public void setFunction_id(Integer function_id) {
        this.function_id = function_id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @JsonBackReference
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
