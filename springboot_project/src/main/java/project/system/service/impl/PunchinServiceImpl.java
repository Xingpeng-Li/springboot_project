package project.system.service.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import project.system.domain.Punchin;
import project.system.domain.User;
import project.system.mapper.PunchinMapper;
import project.system.mapper.UserMapper;
import project.system.service.PunchinService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
public class PunchinServiceImpl implements PunchinService {
    @Resource
    private PunchinMapper punchinMapper;
    @Resource
    private UserMapper userMapper;

    //判断用户今日之内是否进行过考勤打卡
    public Punchin IsPunched(int userId){
        List<Punchin> punchinList = Arrays.asList(punchinMapper.selectByUserId(userId));
        Punchin result = null;

        //获取当前日期
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);

        for(Punchin punchin: punchinList){
            if(punchin.getPuchinTime().getDate() == day){
                result = punchin;
                break;
            }
        }

        return  result;
    }

    @Override
    public XSSFWorkbook show(List<Punchin> punchinList) {
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建一张表
        Sheet sheet = wb.createSheet("Punchin");
        //创建第一行，起始为0
        Row titleRow = sheet.createRow(0);
        //创建表头
        titleRow.createCell(0).setCellValue("编号");
        titleRow.createCell(1).setCellValue("用户名");
        titleRow.createCell(2).setCellValue("电话号码");
        titleRow.createCell(3).setCellValue("打卡日期");
        titleRow.createCell(4).setCellValue("性别");


        int userId = 0;
        User user;
        int cell = 1;
        String gender;

        for(Punchin punchin: punchinList){
            //从第二行开始保存数据
            Row row = sheet.createRow(cell);
            //将数据库的数据遍历出来
            userId = punchin.getUserId();
            if(userId == 0){
                break;
            }else{
                //向表中对应位置填入相应数据
                user = userMapper.selectByPrimaryKey(userId);
                row.createCell(0).setCellValue(punchin.getPunchinId());
                row.createCell(1).setCellValue(user.getUserName());
                row.createCell(2).setCellValue(user.getUserPhonenumber());
                String date = punchin.getPuchinTime().toString();
                row.createCell(3).setCellValue(punchin.getPuchinTime().toString());
                gender = user.getUserGender();
                row.createCell(4).setCellValue(gender);
                cell++;
            }
        }

        return wb;

    }
}
