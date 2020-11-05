package project.system.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import project.system.domain.Punchin;

import java.util.List;

public interface PunchinService {
    public Punchin IsPunched(int userId);

    XSSFWorkbook show(List<Punchin> punchinList);
}
