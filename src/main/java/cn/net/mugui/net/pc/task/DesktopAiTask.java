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

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Task
@AutoTask
public class DesktopAiTask extends TaskCycleImpl<DesktopAiTask.Data> {
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Data extends JsonBean {

        private Type type;

        public enum Type {
            Master,
            douyu_live,
            Other
        }

        private String text;

        private String user;

    }


    boolean isHandle=false;


    @Override
    protected void handle(Data poll) {
        isHandle=true;
        if (poll.type == Data.Type.Master) {
            handleMaster(poll);
        } else if (poll.type == Data.Type.douyu_live) {
            handleDouyuLive(poll);
        }
        isHandle=false;

    }

    private void handleDouyuLive(Data poll) {

        JSONObject object = new JSONObject();
        object.put("直播间用户", poll.getUser());
        object.put("该用户消息", poll.getText());
        poll.setText(object.toJSONString());
        handleMaster(poll);
    }

    @Autowired
    private ChatGptUtil chatGptUtil;


    @Autowired
    private ViewAiChatPanel viewAiChatPanel;

    @Autowired
    private SysConf sysConf;


    private void handleMaster(Data data) {

        String threadId = chatGptUtil.createThread("master");
        MessageBean sendmsg = MessageBean.newUser(threadId, "master", data.getText().trim());
        if (data.type == Data.Type.douyu_live) {
            sendmsg.setRole(MessageBean.ROLE_LIVE_USER);
            MessageBean.update(sendmsg);
        }
        viewAiChatPanel.add(sendmsg);

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

        if(messageBean.getContent()!=null&&!JSONValidator.from(messageBean.getContent()).validate()){
           microsoftSpeechRecognizer.sendMsg(messageBean.getContent());
        }

        System.out.println(messageBean);
    }


    @Autowired
    private CommandHandler commandHandler;


    @Autowired
    private MicrosoftSpeechRecognizer microsoftSpeechRecognizer;

    @Override
    public void add(Data data) {
        if (isHandle) {
            return;
        }
        super.add(data);
    }
}
