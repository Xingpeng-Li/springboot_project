package com.project.service;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    String createCompany(String companyname,Integer adminID);

    void addDept(Integer companyId,String deptName,String masterPhoneNumber, String[] userIds);

    List<Map<String,String>> getDeptUser(Integer userId);

    String joinCompany(String inviteCode, Integer userId);
}
