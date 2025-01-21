package cn.net.mugui.net.pc.task;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.system.SystemUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.util.ChatGptUtil;
import cn.net.mugui.net.pc.util.WxUtil;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import com.mugui.base.client.net.task.TaskCycleImpl;
import com.mugui.util.Other;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;


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
