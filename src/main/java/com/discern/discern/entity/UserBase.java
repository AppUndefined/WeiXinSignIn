package com.discern.discern.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
class UserBase {

    @Id
    private Long id;

//    private String name;

}