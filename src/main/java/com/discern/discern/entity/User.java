package com.discern.discern.entity;

import com.discern.discern.excel.ExcelTitle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user")
public class User  implements Serializable  {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //用户序号
    @Column(name = "user_id", columnDefinition = "int Comment '用户主键ID'")
    private Integer userId;
    //用户名
    @Column(name = "username", unique = true, columnDefinition = "varchar(50) Comment '用户名'")
    private String username;
    // 姓名
    @Column(name = "nickname", columnDefinition = "varchar(50) Comment '姓名'")
    private String nickname;
    // 电话号码
    @Column(name = "phone", unique = true, columnDefinition = "varchar(20) Comment '电话号码'")
    private String phone;
    //是否签到
    private String isNotSign;
    //是否中奖  （0 未中奖，1一等奖，2二等奖,3三等奖）
    @Column
    private String isNotAwardn;
    //用户性别:0男,1女'
    @Column(name = "sex", columnDefinition = "int(1) Comment '性别'")
    private Integer sex;
    //头像路径
    @Column(name = "avatar", columnDefinition = "varchar(150) Comment '头像地址'")
    private String avatar;
    //密码 加密码方式：MD5（password+salt）
    //User对象转json时忽略该字段
    @JsonIgnore
    @Column(name = "password", columnDefinition = "varchar(150) Comment '密码'")
    private String password;
    //密码盐随机6位数（数字加字母）
    @Column(name = "salt", columnDefinition = "varchar(10) Comment '密码盐'")
    private String salt;
    //'帐号状态（0正常 1删除 2禁用）'
    @Column(name = "status", columnDefinition = "int(1) Comment '帐号状态: 0正常，1删除，2禁用'")
    private Integer status;
    //'员工所属部门（1公安部，2执法部，3其他）为空的用户为超级管理员的角色超级管理员不属于任何一个部门'
    @Column(name = "department", columnDefinition = "int(1) Comment '员工所属部门: 1公安部，2执法部，3其他 为空的用户为超级管理员的角色超级管理员不属于任何一个部门'")
    private Integer department;
    @Column
    private String departmentName;
    //'在职状态（0 试用期，1 正式员工，2 留职查看，3 离职）',
    @Column(name = "job_status", columnDefinition = "int(1) Comment '在职状态:0 试用期，1 正式员工，2 留职查看，3 离职'")
    private Integer jobStatus;
    //身份证
    @Column(name = "card_number", columnDefinition = "varchar(25) Comment '身份证号码'")
    private String cardNmber;
    //邮箱
    @Column(name = "email", unique = true, columnDefinition = "varchar(50) Comment '邮箱'")
    private String email;
    //员工工号
    @Column(name = "job_number", columnDefinition = "varchar(50) Comment '员工工号'")
    private String jobNumber;
    //区分超级管理员，管理员，普通用户
    @Column(name = "superType", columnDefinition = "int(1) Comment '区分管理员类型:0 超级管理员，1 管理员，2 普通用户'")
    private Integer superType;
    //创建人ID 如果是超级管理员创建的账户不可以删除
    @Column(name = "createBy", columnDefinition = "varchar(10) Comment '创建人ID'")
    private Integer createBy;
    //修改人ID
    @Column(name = "updateBy", columnDefinition = "varchar(10) Comment '修改人ID'")
    private Integer updateBy;
    //创建时间
    @Column(name = "create_time", columnDefinition = "datetime Comment '创建时间'")
    private Date createTime;
    //修改时间
    @Column(name = "update_time", columnDefinition = "datetime Comment '修改时间'")
    private Date updateTime;
    @ManyToMany(fetch = FetchType.LAZY)//立即加载
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "User_Role", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roleList;//用户与角色关系表

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @ExcelTitle(value = "姓名",order = 2)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    @ExcelTitle(value = "手机号",order = 3)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCardNmber() {
        return cardNmber;
    }

    public void setCardNmber(String cardNmber) {
        this.cardNmber = cardNmber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Set<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<Role> roleList) {
        this.roleList = roleList;
    }


    public Integer getSuperType() {
        return superType;
    }

    public void setSuperType(Integer superType) {
        this.superType = superType;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }
    @ExcelTitle(value = "单位名称",order = 1)
    public String getDepartmentName() {
        return departmentName;
    }
    @ExcelTitle(value = "是否签到",order = 4)
    public String getIsNotSign() {
        return isNotSign;
    }

    public void setIsNotSign(String isNotSign) {
        this.isNotSign = isNotSign;
    }

    public String getIsNotAwardn() {
        return isNotAwardn;
    }

    public void setIsNotAwardn(String isNotAwardn) {
        this.isNotAwardn = isNotAwardn;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

}

