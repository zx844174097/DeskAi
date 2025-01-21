package cn.net.mugui.net.pc.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.net.mugui.net.pc.bean.PcConversationalMsgBean;
import cn.net.mugui.net.pc.util.ChatGptUtil;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;


@Component
@AutoTask
@Task
public class PcMsgHandleTask extends MsgHandleTask<PcConversationalMsgBean>{


    @Override
    protected void sendWebImg(PcConversationalMsgBean event, String url) {

    }

    @Autowired
    private MicrosoftSpeechRecognizer microsoftSpeechRecognizer;

    @Override
    void sendMsg(PcConversationalMsgBean event, String content) {
        microsoftSpeechRecognizer.sendMsg(content);
    }

    @Override
    void sendImg(PcConversationalMsgBean event, String filePath) {

    }

    @Override
    public String getUserId(PcConversationalMsgBean event) {
        return event.getUserId();
    }

    @Override
    public String getFlag() {
        return "pcCM";

    }
   public boolean whetherTheDialogCommandIsTriggered(PcConversationalMsgBean event){
        return false;
   }
    @Override
    public String getContent(PcConversationalMsgBean event) {
        return event.getContext();
    }
}
