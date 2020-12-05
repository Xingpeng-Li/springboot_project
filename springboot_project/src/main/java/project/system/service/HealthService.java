package project.system.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import project.system.domain.HealthPunchin;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface HealthService {
    XSSFWorkbook show(List<HealthPunchin> healthPunchinList);
    HealthPunchin[] selectByUserID(int userId);
    HealthPunchin[] selectAll();
    int deleteByPrimaryKey(Integer punchinId);
    int insert(HealthPunchin record);
}
