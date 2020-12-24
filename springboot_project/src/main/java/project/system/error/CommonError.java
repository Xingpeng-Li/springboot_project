package project.system.error;

/*
@author 李星鹏
@CreateDate 2020-10-10
@update
@description 返回前端错误信息接口
*/
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
