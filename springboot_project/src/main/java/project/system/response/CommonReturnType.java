package project.system.response;

/*
@author WL
@CreateDate 2020-7-08
@update
@description 与前端交互通用返回类型，包装了状态信息以及返回给前端的数据
*/
public class CommonReturnType {
    //表明对应请求的返回处理结果"success"或"fail"
    private String status;

    //若status=success,则data内返回前端需要的json数据，也可以没有data
    //若status=fail,则data内使用通用的错误码格式
    private Object data;

    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
