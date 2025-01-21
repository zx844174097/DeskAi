package cn.net.mugui.net.pc.util;

import cn.net.mugui.net.douyu.danmu.core.IDanmuku;
import cn.net.mugui.net.douyu.danmu.core.impl.DouyuDanmuku;
import cn.net.mugui.net.pc.panel.ChatDialog;
import cn.net.mugui.net.pc.panel.ViewAiChatPanel;
import cn.net.mugui.net.pc.panel.ViewTMChatPanel;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

import java.awt.*;

@Component
public class DouyuTMUtil {

    private ChatDialog chatDialog;
    @Autowired
    private ViewTMChatPanel viewTMChatPanel;


    @Autowired
    private SysConf sysConf;

    @Autowired
    private DouyuDanmuku douyuDanmuku;

    public void start() {
        if (chatDialog == null) {
            chatDialog = new ChatDialog(viewTMChatPanel);
            chatDialog.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 400, 0, 400, Toolkit.getDefaultToolkit().getScreenSize().height-100);
            chatDialog.setVisible(true);
            viewTMChatPanel.init(chatDialog);
            douyuDanmuku.start();
        }
    }

    public void stop() {
        if (chatDialog != null) {
            douyuDanmuku.stop();
            chatDialog.setVisible(false);
            chatDialog.dispose();
            chatDialog = null;
        }
    }
}
