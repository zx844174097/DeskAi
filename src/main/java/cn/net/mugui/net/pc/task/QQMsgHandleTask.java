package cn.net.mugui.net.pc.task;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;


@Task
@AutoTask
@Component
public class QQMsgHandleTask extends MsgHandleTask<MessageEvent> {


    @Override
    protected void sendWebImg(MessageEvent event, String s1) {
        ExternalResource externalResource = ExternalResource.Companion.create(IoUtil.readBytes(URLUtil.getStream(URLUtil.url(s1))));
        Image image = ExternalResource.uploadAsImage(externalResource, event.getSubject());
        event.getSubject().sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(event.getMessage()))
                .append(image)
                .build()
        );
    }

    void sendMsg(MessageEvent event, String substring) {
        event.getSubject().sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(event.getMessage()))
                .append(substring)
                .build()
        );
    }

     void sendImg(MessageEvent event, String substring) {
        ExternalResource externalResource = ExternalResource.Companion.create(new File(substring));
        Image image = ExternalResource.uploadAsImage(externalResource, event.getSubject());
        event.getSubject().sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(event.getMessage()))
                 .append(image)
                .build()
        );
    }

    @Override
    public String getUserId(MessageEvent event) {
        return event.getSender().getId()+"";
    }

    @Override
    public String getFlag() {
        return "qq";
    }

    @Override
    public String getContent(MessageEvent event) {
        MessageChain message = event.getMessage();
        if (message.size()>=2) {
            return message.get(1).contentToString();
        }
        return "";
    }


}
