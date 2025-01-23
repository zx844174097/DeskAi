package cn.net.mugui.net.pc.task;

import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.handler.CommandHandler;
import cn.net.mugui.net.pc.panel.ViewAiChatPanel;
import cn.net.mugui.net.pc.util.ChatGptUtil;
import cn.net.mugui.net.web.util.SysConf;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import com.mugui.base.client.net.task.TaskCycleImpl;
import com.mugui.bean.JsonBean;
import com.mugui.util.Other;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Task
@AutoTask
public class DesktopAiTask extends TaskCycleImpl<MessageBean> {

    boolean isHandle = false;


    @Override
    protected void handle(MessageBean poll) {
        isHandle = true;
        handleMaster(poll);
        isHandle = false;

    }

    @Autowired
    private ChatGptUtil chatGptUtil;


    @Autowired
    private ViewAiChatPanel viewAiChatPanel;

    @Autowired
    private SysConf sysConf;


    private void handleMaster(MessageBean sendmsg) {

        viewAiChatPanel.add(sendmsg);
        if (sendmsg.getStatus() == MessageBean.Status.READING.getValue()) {
            return;
        }

        String getGptModel = sysConf.getValue("chatGpt.model");

        //向chatGpt请求
        ConcurrentLinkedQueue<String> strings = chatGptUtil.sendMsg(sendmsg, getGptModel);
        MessageBean messageBean = new MessageBean();
        messageBean.setUser_id("master");
        messageBean.setSession_id(sendmsg.getSession_id());
        messageBean.setRole(MessageBean.ROLE_ASSISTANT);
        messageBean.setStatus(MessageBean.Status.READING.getValue());
        messageBean = MessageBean.save(messageBean);


        String poll = strings.poll();
        while (!StrUtil.equals(poll, "[done]")) {
            if (StrUtil.isBlank(poll)) {
                Other.sleep(1);
                poll = strings.poll();
                continue;
            }
            if (messageBean.getContent() == null) {
                messageBean.setContent("");
            }
            messageBean.setContent(messageBean.getContent() + poll);
            MessageBean.update(messageBean);
            poll = strings.poll();
            commandHandler.add(messageBean);
            viewAiChatPanel.add(messageBean);
            microsoftSpeechRecognizer.speech(messageBean);
        }
        messageBean.setStatus(MessageBean.Status.SUCCESS.getValue());
        MessageBean.update(messageBean);
        microsoftSpeechRecognizer.speech(messageBean);

        if (messageBean.getContent() != null && !JSONValidator.from(messageBean.getContent()).validate()) {
            microsoftSpeechRecognizer.sendMsg(messageBean.getContent());
        }

        System.out.println(messageBean);
    }


    @Autowired
    private CommandHandler commandHandler;


    @Autowired
    private MicrosoftSpeechRecognizer microsoftSpeechRecognizer;

    @Override
    public void add(MessageBean data) {
        if (isHandle) {
            return;
        }
        super.add(data);
    }
}
