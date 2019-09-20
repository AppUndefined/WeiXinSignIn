package com.discern.discern.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "baseface")
public class Baseface {
    @Id
    @GeneratedValue
    private Integer id;//序号
    @Column( columnDefinition = "LongBlob  Comment '图片 base64 编码字符串'")
    private String face;//图片 base64 编码字符串
    @Column( columnDefinition = "LongBlob  Comment '图片 base64 编码字符串'")
    private String baseFace;//图片 base64 编码字符串
    private String name;//人脸的名称 可为空
    private String sex;//人脸的性别 可为空
    private String age;//人脸的 年龄 可为空
    private String baseName;//人脸的名称 可为空
    private String baseSex;//人脸的性别 可为空
    private String baseAge;//人脸的 年龄 可为空
    private String similarity;//人脸对比的相似率
    private String user;//人脸的标识用来确认用户身份 可为空
    private Integer department;//该图片属于哪个部门
    private Integer compareNumber;//该人像的比较次数
    private Date createDate;//创建日期
    private Date updateDate;//修改日期
    @Transient
    @JsonBackReference
    private Integer pageNumber;
}
