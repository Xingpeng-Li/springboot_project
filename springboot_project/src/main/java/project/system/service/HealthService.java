package com.project.service;

import com.project.domain.HealthPunchin;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface HealthService {
    XSSFWorkbook show(List<HealthPunchin> healthPunchinList);

}
