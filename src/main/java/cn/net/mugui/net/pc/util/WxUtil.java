package cn.net.mugui.net.pc.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.task.WxMsgHandleTask;
import cn.net.mugui.net.web.util.SysConf;
import com.alibaba.fastjson.JSONObject;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.bagsend.HTTPUtil;
import com.mugui.base.util.Other;
import com.mugui.bean.JsonBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

@Component
public class WxUtil {


    @Autowired
    private WxListener wxListener;


    public void login() {

        //hook微信
        boolean b = hookWx();
        if(!b){
            throw new RuntimeException("注入失败");
        }

        //检测微信是否登录
        if (!checkLogin()) {
            throw new RuntimeException("wx not login ");
        }
        //hook 消息
        if (!hookWxMsg()) {
            throw new RuntimeException("hook 消息失败");
        }

    }

    static CMD cmd = null;

    public void run(String runCmd) {

        init();
        cmd.send(runCmd);
        Other.sleep(1000);
    }

    private void init() {
        if (cmd == null || !cmd.isColose()) {
            cmd = new CMD();
            cmd.start();
        }
    }


    @Autowired
    SysConf sysConf;

    private boolean hookWx() {

        String file = new File(ResourceUtil.getResource("jInectDll.exe").getFile()).getAbsolutePath();

        String value = sysConf.getValue("wx.hook.file");
        String wxhelper;
        if (StrUtil.isNotBlank(value)) {
            wxhelper = new File(value).getAbsolutePath();
        } else {
            wxhelper = new File(ResourceUtil.getResource("wxhelper.dll").getFile()).getAbsolutePath();
        }

        JNAUtil.enbalePrivileges();
        //jInectDll.exe -I 20404 -p E:\mugui_work\mugui-chatGpt\src\main\resources\jInectDll.dll
        run(file + " -I 9928 -p " + wxhelper);
//        run(new File("E:\\vswork\\jInectDll\\x64\\Debug\\jInectDll.exe").getAbsolutePath());
//        int i = JNIUtil.INSTANCE.InjectDll(new WString("WeChat.exe"), new WString(wxhelper));
//        System.out.println(i);
        return true;

    }

    public boolean unHookWx() {
        String file = new File(ResourceUtil.getResource("jInectDll.exe").getFile()).getAbsolutePath();
        String value = sysConf.getValue("wx.hook.file");
        String wxhelper;
        if (StrUtil.isNotBlank(value)) {
            wxhelper = new File(value).getAbsolutePath();
        } else {
            wxhelper = new File(ResourceUtil.getResource("wxhelper.dll").getFile()).getAbsolutePath();
        }
        try {
            String post = HTTPUtil.post(getUrl(10), "");
            System.out.println(post);
        }catch (Exception e){
            e.printStackTrace();
        }
        wxListener.Stop();
//       return JNIUtil.INSTANCE.UnInjectDll(new WString("WeChat.exe"),new WString("wxhelper.dll"))==1;
        run(file + " -u WeChat.exe -d " + FileUtil.getName(wxhelper));
        return true;
//        return JNAUtil.unInjectDll("WeChat.exe","wxhelper.dll");
    }







    public boolean hookWxMsg() {
        int port = 4444;
        wxListener.init(port);
        JSONObject object = new JSONObject();
        object.put("port", port + "");
        object.put("ip", "127.0.0.1");
        String post = HTTPUtil.post(getUrl(9), object.toString());
        CodeBean codeBean = CodeBean.newBean(CodeBean.class, post);
        return codeBean.getCode() > 0;

    }

    String baseUrl = "http://127.0.0.1:19088/api/?type=";


    public boolean checkLogin() {
        try {
            String post = HTTPUtil.post(getUrl(0), "");
            CodeBean codeBean = CodeBean.newBean(CodeBean.class, post);
            return codeBean.getCode() != 0;
        } catch (Exception e) {
            return false;
        }

    }

    @Autowired
    WxMsgHandleTask wxMsgHandleTask;

    public void handle(WxBean wxBean) {

        wxMsgHandleTask.add(wxBean);

    }

    /**
     * 向wx发送消息
     *
     * @return
     */
    public boolean sendWx(WxBean event, String msg) {
        String fromUser = event.getFromUser();
        String fromGroup = event.getFromGroup();
        JSONObject jsonObject = new JSONObject();
        String url;
        if (fromUser.equals(fromGroup)) {
            //单人消息
            jsonObject.put("wxid", fromUser);
            url = getUrl(2);
        } else {
            //群消息
            jsonObject.put("chatRoomId", fromGroup);
            jsonObject.put("wxids", fromUser);
            url = getUrl(3);
        }
        jsonObject.put("msg", msg);
        String post = HTTPUtil.post(url, jsonObject.toString());
        CodeBean codeBean = CodeBean.newBean(CodeBean.class, post);
        return codeBean.getCode() != 0;
    }

    public boolean sendImg(WxBean event, String content) {
        String fromUser = event.getFromUser();
        String fromGroup = event.getFromGroup();
        JSONObject jsonObject = new JSONObject();
        String url;
            //单人消息
            jsonObject.put("wxid", fromUser);
            url = getUrl(5);
        jsonObject.put("imagePath", content);
        String post = HTTPUtil.post(url, jsonObject.toString());
        CodeBean codeBean = CodeBean.newBean(CodeBean.class, post);
        return codeBean.getCode() != 0;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private static class CodeBean extends JsonBean {
        Integer code;
        String result;
    }

    private String getUrl(int s) {
        return baseUrl + s;
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class WxBean extends JsonBean {

        //{"content":"2","fromGroup":"wxid_s968dpbubsio22","fromUser":"wxid_s968dpbubsio22","isSendMsg":0,"msgId":6614103390820046659,"pid":15024,"sign":"64f63d226b3a8e579f698f27d51aac3f","signature":"<msgsource>\n\t<signature>v1_e/V/DUSJ</signature>\n</msgsource>\n","time":"2023-03-05 03:04:02","timestamp":1677956642,"type":1}

        private String content;
        private String fromGroup;
        private String fromUser;
        private int isSendMsg;
        private long msgId;
        private int pid;
        private String sign;
        private String signature;
        private String time;
        private long timestamp;
        private int type;

    }

}
