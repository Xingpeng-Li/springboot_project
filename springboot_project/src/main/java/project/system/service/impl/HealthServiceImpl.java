package project.system.service.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import project.system.domain.HealthPunchin;
import project.system.domain.User;
import project.system.mapper.HealthPunchinMapper;
import project.system.mapper.NotificationMapper;
import project.system.mapper.UserMapper;
import project.system.service.HealthService;
import project.system.service.LoginService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HealthServiceImpl implements HealthService {
    @Resource
    HealthPunchinMapper healthPunchinMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    LoginService loginService;
    @Resource
    NotificationMapper notificationMapper;


    public XSSFWorkbook show(List<HealthPunchin> healthPunchinList){
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建一张表
        Sheet sheet = wb.createSheet("HealthPunchin");
        //创建第一行，起始为0
        Row titleRow = sheet.createRow(0);
        //创建表头
        titleRow.createCell(0).setCellValue("编号");
        titleRow.createCell(1).setCellValue("用户名");
        titleRow.createCell(2).setCellValue("体温");
        titleRow.createCell(3).setCellValue("所在城市");
        titleRow.createCell(4).setCellValue("所在省份");
        titleRow.createCell(5).setCellValue("电话号码");
        titleRow.createCell(6).setCellValue("打卡日期");
        titleRow.createCell(7).setCellValue("性别");
        titleRow.createCell(8).setCellValue("健康状况");
        titleRow.createCell(9).setCellValue("是否接触疑似病例");
        titleRow.createCell(10).setCellValue("备注");

        int userId = 0;
        User user;
        int cell = 1;
        String gender;

        for(HealthPunchin healthPunchin: healthPunchinList){
            //从第二行开始保存数据
            Row row = sheet.createRow(cell);
            //row.createCell(0).setCellValue(cell);
            //将数据库的数据遍历出来
            userId = healthPunchin.getUserId();
            if(userId == 0){
                break;
            }else{
                //向表中对应位置填入相应数据
                user = userMapper.selectByPrimaryKey(userId);
                row.createCell(0).setCellValue(healthPunchin.getPunchinId());
                row.createCell(1).setCellValue(user.getUserName());
                row.createCell(2).setCellValue(healthPunchin.getUserBodyTemp());
                row.createCell(3).setCellValue(healthPunchin.getCity());
                row.createCell(4).setCellValue(healthPunchin.getProvince());
                row.createCell(5).setCellValue(user.getUserPhonenumber());
                String date = healthPunchin.getPunchinDate().toString();
                row.createCell(6).setCellValue(healthPunchin.getPunchinDate().toString());

                gender = user.getUserGender();
                row.createCell(7).setCellValue(gender);
                row.createCell(8).setCellValue(healthPunchin.getUserHealthStatus());
                row.createCell(9).setCellValue(healthPunchin.getContactSuspectedCase());
                row.createCell(10).setCellValue(healthPunchin.getPunchinNote());
                cell++;
            }
        }

        return wb;

    }

    public HealthPunchin[] selectByUserID(int userId){
        return healthPunchinMapper.selectByUserID(userId);
    }

    public HealthPunchin[] selectAll(){
        return healthPunchinMapper.selectAll();
    }

    public int deleteByPrimaryKey(Integer punchinId){
        return healthPunchinMapper.deleteByPrimaryKey(punchinId);
    }

    public int insert(HealthPunchin record){
        return healthPunchinMapper.insert(record);
    }
}
