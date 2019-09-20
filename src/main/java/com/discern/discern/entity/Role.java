package com.discern.discern.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
//权限表
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//序号
    @Column(name = "r_name", columnDefinition = "varchar(150) Comment '名称'")
    private String name;//名称
    @Column(name = "r_desc", columnDefinition = "varchar(500) Comment '描述'")
    private String desc;//描述
    @Column(name = "r_department", columnDefinition = "int(1) Comment '员工所属部门: 1公安部，2执法部，3其他'")
    private Integer department;//角色所属部门（1公安部，2执法部，3其他 为空的角色为超级管理员的角色超级管理员不属于任何一个部门）'
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
            @JoinColumn(name = "user_id") })
    private Set<User> userList=new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="role_function",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns={
            @JoinColumn(name="function_id")})
    private Set<Function> functionList=new HashSet<>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    @JsonBackReference
    public Set<User> getUserList() {
        return userList;
    }

    public void setUserList(Set<User> userList) {
        this.userList = userList;
    }
    @JsonBackReference
    @JsonManagedReference
    public Set<Function> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(Set<Function> functionList) {
        this.functionList = functionList;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }
}
