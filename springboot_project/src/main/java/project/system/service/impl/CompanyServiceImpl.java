package project.system.service.impl;


import org.springframework.stereotype.Service;
import project.system.domain.Company;
import project.system.domain.Dept;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.CompanyMapper;
import project.system.mapper.DeptMapper;
import project.system.mapper.UserMapper;
import project.system.service.CompanyService;
import project.system.service.UserService;

import javax.annotation.Resource;
import java.util.*;

/*
 @author:李星鹏
 @createDate:2020/7/10
 @description:创建企业、添加部门Service

 @update:2020/7/11
 @description:添加了返回部门员工的函数

 @update:2020/7/15
 @description:添加了只有管理员能添加部门的权限以及企业邀请码的生成

 @update:2020/7/19
 @description:选择联系人添加部门管理员
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    @Resource
    CompanyMapper companyMapper;

    @Resource
    DeptMapper deptMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;

    //生成邀请码的最小长度
    private final int MIN_CODE_LENGTH = 6;
    //位数不足时自动补长时，充当分隔，该字段为保持唯一性，不放入下方的列表
    private final String STOP_CHAR = "Z";
    //考虑用户体验，此处去掉了 i o 1 0，具体列表内容自由换序
    private final String[] CHARS = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"};
    private final int OFFSET = CHARS.length - 1;

    public String createCompany(String companyname,Integer adminID){
        Company company = companyMapper.selectByName(companyname);
        User admin = userMapper.selectByPrimaryKey(adminID);

        //公司名称已经存在
        if(company != null){
            throw new BusinessException(EmBusinessError.COMPANY_ALREADY_EXIST);
        }

        String inviteCode = intToChars(adminID);
        company = new Company();
        company.setCompanyName(companyname);
        company.setCompanyAdmin(adminID);
        company.setCompanyInviteCode(inviteCode);
        companyMapper.insertSelective(company);

        company = companyMapper.selectByCode(inviteCode);

        //设置企业管理员的职位字段
        admin.setUserPosition("admin");
        admin.setCompanyId(company.getCompanyId());
        userMapper.updateByPrimaryKeySelective(admin);
        return inviteCode;
    }

    private String intToChars(int id) {
        int x = id / OFFSET;
        int remainder = id % OFFSET;
        String inviteCode;
        if (x == 0) {
            inviteCode = CHARS[id];
        } else if (x < OFFSET) {
            inviteCode = CHARS[x] + CHARS[remainder];
        } else {
            inviteCode = intToChars(x) + CHARS[remainder];
        }
        //将邀请码补至6位
        int tailLength = MIN_CODE_LENGTH - inviteCode.length();
        if (tailLength > 1) {
            inviteCode = inviteCode + STOP_CHAR + codeTail(tailLength - 1);
        } else if (tailLength == 1) {
            inviteCode = inviteCode + STOP_CHAR;
        }
        return inviteCode;
    }

     //获取补长的邀请码（随机）
    private String codeTail(int len) {
        String res = "";
        Random r = new Random();
        for (int i = 0; i < len; i++) {
            res += CHARS[r.nextInt(OFFSET)];
        }
        return res;
    }

    public void addDept(Integer userId,String deptName,String masterPhoneNumber, String[] userIds){

        if(!userService.isCompanyAdmin(userId)){
            throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
        }
        User user = userMapper.selectByPrimaryKey(userId);

        //由于user从cookie中获得，因此一定不为null
        Integer companyId = user.getCompanyId();

        Company company = companyMapper.selectByPrimaryKey(companyId);
        //公司名称不存在
        if(company == null){
            throw new BusinessException(EmBusinessError.COMPANY_NOT_FOUND);
        }

        User master = userMapper.getUserByPhoneNumber(masterPhoneNumber);
        //用户不存在或不属于该企业
        if(master == null || !master.getCompanyId().equals(companyId)){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        Dept[] depts = deptMapper.selectByCompanyId(companyId);
        for (Dept aDept: depts) {
            if(deptName.equals(aDept.getDeptName())){
                throw new BusinessException(EmBusinessError.DEPT_ALREADY_EXIST);
            }
        }

        Dept dept = new Dept();
        dept.setDeptName(deptName);
        dept.setDeptMaster(master.getUserId());
        dept.setCompanyId(companyId);
        deptMapper.insertSelective(dept);

        //更新部门员工的部门号以及职位
        for(String employeeId:userIds){
            User employee = userMapper.selectByPrimaryKey(Integer.parseInt(employeeId));
            employee.setDeptId(dept.getDeptId());
            //企业管理员也有可能被设置为某一部门的员工，但其职位不能变
            if(!"admin".equals(employee.getUserPosition())){
                employee.setUserPosition("employee");
            }
            userMapper.updateByPrimaryKey(employee);
        }

        //更新部门管理员的职位
        if(!"admin".equals(master.getUserPosition())){
            master.setUserPosition("master");
        }
        master.setDeptId(dept.getDeptId());
        userMapper.updateByPrimaryKey(master);
    }

    public List<Map<String,String>> getDeptUser(Integer userId){
        List<Map<String,String>> mapList = new ArrayList<>();

        User user = userMapper.selectByPrimaryKey(userId);
        int deptId = user.getDeptId();

        //获取部门名
        Dept dept = deptMapper.selectByPrimaryKey(deptId);
        String deptName = dept.getDeptName();

        //获取同一部门中的人
        List<User> userList = Arrays.asList(userMapper.selectByDeptId(deptId));
        for(User user1: userList){

            Map<String,String> map = new HashMap<>();
            map.put("userName",user1.getUserName());
            map.put("phoneNumber",user1.getUserPhonenumber());
            map.put("deptName",deptName);

            mapList.add(map);

        }
        return mapList;
    }

    @Override
    public String joinCompany(String inviteCode, Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        Company company = companyMapper.selectByCode(inviteCode);

        //企业不存在
        if(company == null){
            throw new BusinessException((EmBusinessError.COMPANY_NOT_FOUND));
        }
        else{
            user.setCompanyId(company.getCompanyId());
            userMapper.updateByPrimaryKeySelective(user);
        }
        return company.getCompanyName();
    }
}
