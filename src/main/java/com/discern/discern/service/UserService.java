package com.discern.discern.service;


import com.discern.discern.entity.User;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface UserService  {

    User findByUsername(String username);

    void addUser(User user);

    Page<User> findByUsername(Integer pageNumber, Integer pageSize, User userName, Integer  department);

    void delete(Integer userId);

    void update(User user);

    User findById(Integer userId);

    void importUser(Workbook wb);

    List<User> findAllByUser(User user);

    File export() throws FileNotFoundException, Exception;
}
