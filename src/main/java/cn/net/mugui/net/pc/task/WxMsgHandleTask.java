package cn.net.mugui.net.pc.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import cn.net.mugui.net.pc.util.WxUtil;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import com.mugui.util.Other;

import java.io.InputStream;


@Task
@AutoTask
@Component
public class WxMsgHandleTask extends MsgHandleTask<WxUtil.WxBean> {


    @Autowired
    private WxUtil wxUtil;


    @Override
    protected void sendWebImg(WxUtil.WxBean event, String s1) {
        //保存到临时文件
        String path = FileUtil.getTmpDirPath() +Other.getUUID() + ".jpg";
        InputStream stream = URLUtil.getStream(URLUtil.url(s1));
        FileUtil.writeFromStream(stream, path);
        wxUtil.sendImg(event,path);
    }

    @Override
    void sendMsg(WxUtil.WxBean event, String content) {
        wxUtil.sendWx(event,content);
    }

    @Override
    void sendImg(WxUtil.WxBean event, String path) {
        wxUtil.sendImg(event,path);
    }

    @Override
    public String getUserId(WxUtil.WxBean event) {
        return event.getFromUser()+"";
    }

    @Override
    public String getFlag() {
        return "wx";
    }

    @Override
    public String getContent(WxUtil.WxBean event) {
        return event.getContent();
    }















}
