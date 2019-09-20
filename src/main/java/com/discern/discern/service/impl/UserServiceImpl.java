package com.discern.discern.service.impl;

import com.discern.discern.constant.UserConstants;
import com.discern.discern.dao.FunctionJpaDao;
import com.discern.discern.dao.UserJpaDao;
import com.discern.discern.entity.User;
import com.discern.discern.excel.ExcelTitle;
import com.discern.discern.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.io.*;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserJpaDao userJpaDao;
    @Autowired
    private FunctionJpaDao functionJpaDao;

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        return userJpaDao.findByUsername(username);
    }



    @Override
    public void addUser(User user) {
        user.setIsNotSign(UserConstants.UN_SIGN);
        userJpaDao.save(user);
    }

    /**
     * 根据用户名和分页信息分页查找用户
     * @param pageNumber
     * @param pageSize
     * @param user
     * @return
     */
    @Override
    public Page<User> findByUsername(Integer pageNumber, Integer pageSize, User user, Integer  department) {
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        Page<User> users = userJpaDao.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (user.getNickname() != null && !"".equals(user.getNickname())) {
                    list.add(criteriaBuilder.like(root.get("nickname").as(String.class), "%"+user.getNickname()+"%"));
                }
                if (user.getDepartmentName() != null && !"".equals(user.getDepartmentName())) {
                    list.add(criteriaBuilder.like(root.get("departmentName").as(String.class), "%"+user.getDepartmentName()+"%"));
                }
                if (user.getPhone() != null && !"".equals(user.getPhone())) {
                    list.add(criteriaBuilder.like(root.get("phone").as(String.class), user.getPhone()));
                }
                if (user.getIsNotSign() != null && !"".equals(user.getIsNotSign())) {
                    list.add(criteriaBuilder.like(root.get("isNotSign").as(String.class), user.getIsNotSign()));
                }
                list.add(criteriaBuilder.notEqual(root.get("username").as(String.class), "admin"));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return users;
    }

    @Override
    public void delete(Integer userId) {
        userJpaDao.deleteById(userId);
    }

    @Override
    public void update(User user) {
        userJpaDao.save(user);
    }

    @Override
    public User findById(Integer userId) {
        return userJpaDao.findById(userId).get();
    }

    @Override
    public void importUser(Workbook wb) {
        ArrayList<User> users = new ArrayList<>();
        //获取第一张表
        Sheet sheet = wb.getSheetAt(0);
        for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
            User user = new User();
            //获取索引为i的行，以0开始
            Row row = sheet.getRow(i);
            //获取第i行的索引为0的单元格数据
            String departmentName = row.getCell(0).toString();
            String nickname = row.getCell(1).toString();
            Cell cell = row.getCell(2);
            String stringCellValue = cell.toString();
            String phone = stringCellValue;
            user.setUsername(UUID.randomUUID().toString());
            user.setDepartmentName(departmentName.replace(".0",""));
            user.setNickname(nickname.replace(".0",""));
            user.setPhone(phone.replace(".0",""));
            user.setIsNotSign(UserConstants.UN_SIGN);
            users.add(user);
        }
        userJpaDao.saveAll(users);
    }

    @Override
    public List<User> findAllByUser(User user) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<User> example = Example.of(user, matcher);
        return  userJpaDao.findAll( example);
    }

    @Override
    public File export() throws Exception {
        List<User> all = userJpaDao.findByNicknameIsNotNull();
        File mk= new File("签到");
        if(!mk.exists()) {
            mk.mkdirs();
        }
        File file= new File("统计.xls");
        OutputStream out = new FileOutputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle center = workbook.createCellStyle();

        if(all.size() < 1){
            return null;
        }

        //获取导出对象的方法
        Method[] methods = all.get(0).getClass().getDeclaredMethods();
        List<Method> getterMethods = new ArrayList<Method>();
        for(Method method : methods) {
            method.getDeclaredAnnotations();
            if(method.getName().contains("get") && method.isAnnotationPresent(ExcelTitle.class)) {   //过滤方法，只获取ExcelTitle注解的get方法
                getterMethods.add(method);
            }
        }
        Collections.sort(getterMethods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                int i = o1.getAnnotation(ExcelTitle.class).order() - o2.getAnnotation(ExcelTitle.class).order();
                return i;
            }
        });
        //设置表头
        HSSFSheet sheet = workbook.createSheet("统计");
        int index = 0;
        HSSFRow row = sheet.createRow(index);
        HSSFCell cell0 = row.createCell(0);
        HSSFRichTextString textString0 = new HSSFRichTextString("序号");
        cell0.setCellStyle(center);
        cell0.setCellValue(textString0);
        sheet.setColumnWidth(0,100*60);
        for(int i = 1; i < getterMethods.size()+1; i++) {
            sheet.setColumnWidth(i,100*58);
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString textString = new HSSFRichTextString(getterMethods.get(i-1).getAnnotation(ExcelTitle.class).value());
            cell.setCellValue(textString);
            cell.setCellStyle(center);
        }

        //表格中填充数据
        for(Object obj : all) {
            index++;
            row = sheet.createRow(index);
            HSSFCell cellData1 = row.createCell(0);
            HSSFRichTextString textData1 = new HSSFRichTextString(index+"");;
            cellData1.setCellValue(textData1);
            cellData1.setCellStyle(center);
            for(int i =1; i < getterMethods.size()+1; i++) {
                HSSFCell cellData = row.createCell(i);
                HSSFRichTextString textData = null;
                String value = new String(getterMethods.get(i-1).invoke(obj, null) + "");
                if(!value.equals("null")) {
                    textData = new HSSFRichTextString(value);
                } else {
                    textData = new HSSFRichTextString("");
                }
                cellData.setCellValue(textData);
                cellData.setCellStyle(center);
            }
        }
        try {
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}