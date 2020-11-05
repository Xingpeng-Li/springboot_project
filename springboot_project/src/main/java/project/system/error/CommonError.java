package project.system.error;

/*
@author WL
@CreateDate 2020-7-08
@update
@description 返回前端错误信息接口
*/
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
