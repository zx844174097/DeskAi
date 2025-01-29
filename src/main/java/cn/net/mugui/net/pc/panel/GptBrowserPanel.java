package cn.net.mugui.net.pc.panel;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.net.mugui.net.pc.manager.FunctionUI;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.Dui.DButton;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.teamdev.jxbrowser.devtools.DevTools;
import com.teamdev.jxbrowser.net.UrlRequest;
import com.teamdev.jxbrowser.net.callback.BeforeSendUploadDataCallback;
import com.teamdev.jxbrowser.net.callback.BeforeStartTransactionCallback;
import com.teamdev.jxbrowser.net.callback.BeforeUrlRequestCallback;
import com.teamdev.jxbrowser.net.proxy.CustomProxyConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class GptBrowserPanel extends FunctionUI {

    public GptBrowserPanel() {
        setTitle("浏览器");
        setMenu_name("浏览器");
        setLayout(new BorderLayout(0, 0));


        JPanel panel_3 = new JPanel();
        DButton devTools = getDevTools();
        panel_3.add(devTools);
        add(panel_3, BorderLayout.NORTH);
    }


    private @NotNull DButton getDevTools() {
        DButton devTools = new DButton("调试模式", (Color) null);
        devTools.setText("网页调试");
        devTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                DevTools devTools1 = muguiBrowser.now().getBrowser().devTools();
                devTools1.remoteDebuggingUrl().ifPresent((url) -> {
                    muguiBrowser.openUrl(url);
                });
            }
        });
        return devTools;
    }

    @Autowired
    private MuguiBrowser muguiBrowser;

    @Autowired
    private SysConf sysConf;

    @Override
    public void init() {
        muguiBrowser.init();
        muguiBrowser.setHostListener(hostListener);

        add(muguiBrowser, BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dBrowserHander = muguiBrowser.openUrl("https://youtube.com/");
            }
        });
    }

    MuguiBrowser.DBrowserHander dBrowserHander = null;

    MuguiBrowser.HostListener hostListener = new MuguiBrowser.HostListener() {

        boolean aBoolean = false;

        @Override
        public BeforeSendUploadDataCallback.Response BeforeSendUploadDataCallback(BeforeSendUploadDataCallback.Params params) {
            if (!aBoolean) {
                aBoolean = true;
                ThreadUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        dBrowserHander.browserView.getBrowser().mainFrame().ifPresent(frame -> {
                            frame.executeJavaScript("Array.prototype.findLast = Array.prototype.findLast || function (callback) { for (let i = this.length - 1; i >= 0; i--) { if (callback(this[i], i, this)) { return this[i]; } } return undefined; };");
                        });
                        aBoolean = false;
                    }
                });
            }


            return super.BeforeSendUploadDataCallback(params);
        }

        @Override
        public void BeforeStartTransactionCallback(BeforeStartTransactionCallback.Params params) {

        }

        @Override
        public void BeforeUrlRequestCallback(BeforeUrlRequestCallback.Params params) {
        }


    };


    @Override
    public void quit() {

    }

    @Override
    public void dataInit() {

    }

    @Override
    public void dataSave() {

    }
}
