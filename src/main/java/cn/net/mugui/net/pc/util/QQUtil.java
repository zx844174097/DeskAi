package cn.net.mugui.net.pc.util;

import cn.net.mugui.net.pc.task.QQMsgHandleTask;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import lombok.Getter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.EnumMap;

@Component
public class QQUtil {

    @Getter
    private Bot bot=null;

    @Autowired
    private SysConf sysConf;

    public void login() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if(bot==null||!bot.isOnline()){
            String user = sysConf.getValue("qq.user");
            String password = sysConf.getValue("qq.password");
            BotConfiguration botConfiguration = new BotConfiguration() {
                {
                    fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
                    setProtocol(MiraiProtocol.ANDROID_WATCH); // 切换协议
                    setLoginCacheEnabled(true);
                }
            };
            bot = BotFactory.INSTANCE.newBot(Long.parseLong(user), BotAuthorization.byQRCode(), botConfiguration);
            bot.login();
        }
        myQQNumber = bot.getId();
    }

    long myQQNumber ;
    /**
     * 监听好友消息
     */
    public  void listenerAllMsg() {
        bot.getEventChannel().subscribeAlways(MessageEvent.class, (event) -> {
            if (event.getSender().getId() == myQQNumber) {
                return;
            }
            if(System.currentTimeMillis()/1000-event.getTime()<10000){

                qqMsgHandleTask.add(event);
            }



        });

    }

    @Autowired
    QQMsgHandleTask qqMsgHandleTask;


    public void unLogin() {
        if(bot!=null)
            bot.close();
    }
}
