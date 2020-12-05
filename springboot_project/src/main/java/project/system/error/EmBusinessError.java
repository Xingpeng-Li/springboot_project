package project.system.error;

/*
@author WL DKR LXP XBC
@CreateDate 2020-7-08
@update
@description 一个枚举类，用来返回前端错误码以及错误信息，实现了CommonError接口
*/
public enum EmBusinessError implements CommonError {
    PUBLIC_ACCOUNT_NOT_EXISTS(7001,"公众号不存在"),
    POST_NOT_EXISTS(7002,"公众号文章不存在"),
    PUBLIC_ACCOUNT_ALREADY_EXISTS(7002,"公众号已存在"),
    PUBLIC_ACCOUNT_HAS_SUBSCRIBED(7003,"公众号以订阅"),
    PUBLIC_ACCOUNT_HAS_NOT_SUBSCRIBED(7004,"公众号未订阅"),


    NORMAL_ERR(8999,"参数异常"),
    SYSTEM_ERR(9999,"系统异常"),
    //10000开头为通用错误类型
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    UNKNOWN_ERROR(10002, "未知错误"),
    DB_ERROR(10003, "数据库错误"),
    FILE_ERROR(10004,"文件格式错误"),
    GENDER_ERROR(10005,"性别参数不合法"),
    DATE_FORMAT_ERROR(10006,"日期格式不正确"),
    DATE_ERROR(10007,"开始时间不能晚于结束时间"),
    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "手机号不存在"),
    USER_LOGIN_FAIL(20002, "用户未开户"),
    UNLOGIN(1, "用户未登录"),
    USER_PHONE_EXIST(2004,"手机号已注册"),
    USER_PHONE_ERROR(2005,"手机号错误"),
    USER_VERIFICATION_CODE_ERROR(2006,"验证码填写错误"),
    USER_VERIFICATION_CODE_SEND_FAIL(2007,"验证码发送失败"),
    USER_VERIFICATION_CODE_EXPIRED(2008,"验证码过期"),
    COMPANY_NOT_FOUND(20009,"所属企业不存在"),
    DEPT_NOT_FOUND(20010,"所属部门不存在"),
    USER_LOGIN_VERIFY_FAIL(20011,"用户名或者密码错误"),
    USER_LOGIN_EXPIRED(20012,"登录已过期"),
    USER_WITHOUT_AUTHORITY(20013,"用户没有权限"),
    //30000开头为视频会议信息错误定义,
    VIDEO_ERROR(30001, ""),
    GROUP_NOT_EXISTS(40001,"会议不存在"),
    //50000开头为部门
    COMPANY_ALREADY_EXIST(50001,"该企业已经存在"),
    DEPT_ALREADY_EXIST(50002,"该部门已经存在"),
    //60000开头为文件相关
    FILE_UPLOAD_ERROR(60001,"文件上传失败"),
    FILE_TYPE_ERROR(60002,"文件类型不匹配"),
    FILE_CONTENT_ERROR(60003,"文件内容不符合要求"),
    FILE_UPLOAD_FAIL(60004,"文件上传失败"),
    FILE_DOWNLOAD_FAIL(60005,"文件下载失败"),
    FILE_COPY_FAIL(60006,"文件保存失败"),
    FILE_DELETE_FAIL(60007,"文件删除失败"),
    FILE_TRANSFER_FAIL(60008,"服务器接收文件失败"),
    FILE_DATA_EMPTY(60009,"没有更多数据了"),

    //70000开头为打卡相关错误
    ALREADY_PUNCHIN(70001,"您今日已经打过卡!"),
    UNAUTHORIZED(70002,"没有权限操作!"),
    //80000开头为与邮箱有关的错误
    MAIL_BINDING_ERROR(80001,"邮箱账号或授权码错误！"),
    MAIL_RECEIVE_ERROR(80002,"邮件读取出错！"),
    MAIL_SEND_ERROR(80003,"邮件发送出错！"),
    MAIL_FROMATE_ERROR(80004,"邮箱格式出错！"),
    //90000开头为语音识别相关错误码
    RECOGNITION_FAIL(90001,"语音转换请求失败"),
    RECOGNITION_RESULT_FAIL(90002,"语音转换失败"),
    RECOGNITION_PROCESSING(90003,"转换进行中,请稍等!"),
    RECOGNITION_WAITING(90004,"等待转换中");

    private int errCode;
    private String errMsg;

    EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
