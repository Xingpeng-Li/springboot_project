package project.system.controller;

import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.response.CommonReturnType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/*
@author WL
@CreateDate 2020-7-08
@update
@description 异常捕获类
*/
@RestController
public class BaseController {

    @ExceptionHandler(Exception.class)
    public Object handlerException(Exception e, HttpServletRequest request) {
        Map<String, Object> responseData = new HashMap<>();
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;

            responseData.put("errCode", exception.getErrCode());
            responseData.put("errMsg", exception.getErrMsg());
        } else {
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData, "fail");
    }
}
