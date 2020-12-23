package project.system.service.impl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.Md5Utils;
import project.system.domain.Company;
import project.system.domain.Dept;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.CompanyMapper;
import project.system.mapper.DeptMapper;
import project.system.mapper.UserMapper;
import project.system.service.ImportUserService;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static project.system.common.utils.Validator.isMobile;

/*
 @author:zws
 @createDate:2020/11/18
 @description:导入部门员工通讯录Service

 @update:2020/11/25
 @description:添加了无论单元格数据类型，都按照String类型返回单元格数据的函数

 @update:2020/11/25
 @description:若用户号码已经存在数据库中，更改了导入部门员工逻辑

 @
 */
@Service
public class ImportUserServiceImpl implements ImportUserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private CompanyMapper companyMapper;

    //从Excel文件中导入员工，返回导入员工数
    public int importUser(MultipartFile file, Integer userId) throws IOException {
        User admin = userMapper.selectByPrimaryKey(userId);
        int count=0;
        List<User> list = new ArrayList<>();

        Workbook workbook = null;

        //获取文件名
        String filename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        //把文件转换为输入流
        InputStream inputStream = file.getInputStream();
        //根据文件后缀判断
        if(suffix.equals("xlsx")){
            workbook = new XSSFWorkbook(inputStream);
        }else if(suffix.equals("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        }else{
            throw new BusinessException(EmBusinessError.FILE_ERROR);
        }

        //获得sheet表
        Sheet sheet=workbook.getSheetAt(0);

        if(sheet!=null){
            //首先判定表头是否符合要求
            Row head = sheet.getRow(0);
            for(int i = 0;i < 5;i++){
                if(1 != head.getCell(i).getCellType()){
                    throw new BusinessException(EmBusinessError.FILE_ERROR);
                }
            }

            //数据是从第二行开始，所以这里从第二行开始遍历
            for (int line = 1;line <= sheet.getLastRowNum();line++){
                User user = new User();
                Row row = sheet.getRow(line);
                if(row == null)
                    continue;

                //判断单元格类型是否为文本类型
                for(int i = 0;i < 5;i++){
                    if(1 != row.getCell(i).getCellType()){
                        continue;
                    }
                }

                //根据excel表的内容修改user属性
                user.setUserName(getCellValue(row.getCell(0)));
                String gender = getCellValue(row.getCell(1));
                //性别需要为男或者女
                if(!"男".equals(gender) && !"女".equals(gender)){
                    throw new BusinessException(EmBusinessError.GENDER_ERROR);
                }
                user.setUserGender(gender);

                String phoneNumber = getCellValue(row.getCell(2));
                if(!isMobile(phoneNumber)){
                    throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
                }
                user.setUserPhonenumber(phoneNumber);

                //根据企业名称从数据库中找到企业id
                String companyName = getCellValue(row.getCell(4));
                Company company = companyMapper.selectByName(companyName);

                //数据库中没有该企业
                if(company == null){
                    throw new BusinessException(EmBusinessError.COMPANY_NOT_FOUND);
                }
                user.setCompanyId(company.getCompanyId());

                //根据部门名称从数据库中找到部门id
                String deptName = getCellValue(row.getCell(3));
                Dept dept = deptMapper.selectByName(deptName);
                //数据库中没有该部门
                if(dept == null){
                    throw new BusinessException(EmBusinessError.DEPT_NOT_FOUND);
                }
                user.setDeptId(dept.getDeptId());

                if("admin".equals(admin.getUserPosition()) && admin.getCompanyId().equals(company.getCompanyId()) ||
                        "master".equals(admin.getUserPosition()) && admin.getDeptId().equals(dept.getDeptId())) {
                    user.setUserPosition("employee");
                }
                else {
                    throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
                }

                User userByPhone = userMapper.getUserByPhoneNumber(phoneNumber);
                if(userByPhone == null){
                    String password = phoneNumber;
                    String userPassword= Md5Utils.inputPassToDBPass(password,phoneNumber+"miaowhu");
                    user.setUserPassword(userPassword);
                    list.add(user);
                }
                else{
                    user.setUserId(userByPhone.getUserId());
                    userMapper.updateByPrimaryKeySelective(user);
                    count += 1;
                }

            }
        }
        //把得到的数据集合插入数据库
        for (User user:list){
            int i= userMapper.insertSelective(user);
            count += 1;
        }
        //返回插入的行数
        return count;
    }

    private String getCellValue(Cell cell){
        String cellValue = null;
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                DecimalFormat df = new DecimalFormat("#");
                cellValue = df.format(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                break;
            default:
                cellValue = ",";
                break;
        }
        return cellValue;
    }
}
