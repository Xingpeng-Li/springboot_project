package project.system.error;

/*
@author 李星鹏
@CreateDate 2020-10-10
@update
@description 与业务逻辑有关的异常类
*/
public class BusinessException extends RuntimeException implements CommonError {
    private CommonError commonError;

    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    public BusinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        return commonError.setErrMsg(errMsg);
    }
}
