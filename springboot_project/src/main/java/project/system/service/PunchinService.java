package com.project.service;

import com.project.domain.HealthPunchin;
import com.project.domain.Punchin;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface PunchinService {
    public Punchin IsPunched(int userId);

    XSSFWorkbook show(List<Punchin> punchinList);
}
