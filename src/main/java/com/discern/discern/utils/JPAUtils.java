package com.discern.discern.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtils {
    //JPA的实体管理器工厂：相当于Hibernte的SessionFactory
    private static final EntityManagerFactory em;
    //使用静态代码块赋值
    static{
        //创建JPA的实体管理器工厂：该方法参数必须和persistence.xml中persistence-unit标签的name属性一致
        em = Persistence.createEntityManagerFactory("myPersistUnit");
    }

    /**
     * 获取实例管理器的工具方法
     * @return
     */
    public static EntityManager getEntityManager(){
        return em.createEntityManager();
    }
}
