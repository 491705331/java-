package tools;

public enum MessageTypeEnum {
    Login_Success, //登录成功
    Login_Error, //登陆失败
    Register_Success, //注册成功
    Register_UsernameExist, //注册失败
    Send_Message, //发送消息
    Get_History, //获取历史消息
    Return_Message,  //返回消息
    Return_History, //返回历史消息
    Exit,  //退出
    Get_Onlines, //获取在线列表
    Return_Onlines,  //返回在线列表
    Return_Groups,  //返回群列表
    Get_Groups,  //获取群列表
    Refuse_Connect; //拒绝连接
}
