//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.net.mugui.net.pc.panel;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.AppFrame;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.Dui.DButton;
import com.mugui.Dui.DPanel;
import com.mugui.Dui.DVerticalFlowLayout;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.CreatePopupCallback;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.event.Observer;
import com.teamdev.jxbrowser.net.*;
import com.teamdev.jxbrowser.net.callback.BeforeSendUploadDataCallback;
import com.teamdev.jxbrowser.net.callback.BeforeStartTransactionCallback;
import com.teamdev.jxbrowser.net.callback.BeforeUrlRequestCallback;
import com.teamdev.jxbrowser.net.event.ResponseBytesReceived;
import com.teamdev.jxbrowser.net.proxy.CustomProxyConfig;
import com.teamdev.jxbrowser.net.proxy.SystemProxyConfig;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

@Component
public class MuguiBrowser extends DPanel {
    private JPanel titlePanel = null;
    private JPanel body = null;

    private CardLayout bodyCard = null;
    private DButton button = null;
    private DButton button_1 = null;
    private HashMap<Integer, Browser> listenerList = null;
    private int weight = 150;
    private static final long serialVersionUID = -6167935557919438045L;
    private Dimension now_Dimension = new Dimension(1200, 800);
    private Point now_point = null;

    private JTextField textField;


    public MuguiBrowser() {

        this.setBackground((Color) null);
        this.setLayout(new BorderLayout(0, 0));
        this.body = new JPanel();
        this.body.setMaximumSize(new Dimension(2700, 1920));
        this.add(body, "Center");
        this.button_1 = new DButton((String) null, (Color) null);
        this.button_1.setFont(new Font("Dialog", 1, 12));
        this.button_1.setText("前进");
        this.button = new DButton((String) null, (Color) null);
        this.button.setFont(new Font("Dialog", 1, 12));
        this.button.setText("后退");
        this.button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                java.awt.Component[] var5;
                int var4 = (var5 = MuguiBrowser.this.body.getComponents()).length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    java.awt.Component component = var5[var3];
                    if (component.isVisible()) {
                        BrowserView browserView = (BrowserView) component;
                        Browser browser = browserView.getBrowser();
                        browser.navigation().goBack();
                    }
                }

            }
        });
        this.button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                java.awt.Component[] var5;
                int var4 = (var5 = MuguiBrowser.this.body.getComponents()).length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    java.awt.Component component = var5[var3];
                    if (component.isVisible()) {
                        BrowserView browserView = (BrowserView) component;
                        Browser browser = browserView.getBrowser();
                        browser.navigation().goForward();
                    }
                }

            }
        });
        this.body.setLayout(this.bodyCard = new CardLayout(0, 0) {
            private static final long serialVersionUID = 8107580521575614902L;

            public void show(Container parent, String name) {
                super.show(parent, name);
                int ncomponents = parent.getComponentCount();

                for (int i = 0; i < ncomponents; ++i) {
                    java.awt.Component comp = parent.getComponent(i);
                    if (comp.isVisible()) {
                        BrowserView view = (BrowserView) comp;
                        if (!view.getBrowser().isClosed()) {
                            MuguiBrowser.this.setShowUrl(view.getBrowser().url());
                            MuguiBrowser.this.setCanGoBack(view.getBrowser().navigation().canGoBack());
                            MuguiBrowser.this.setCanGoForward(view.getBrowser().navigation().canGoForward());
                        }
                        break;
                    }
                }

            }
        });
        JPanel panel = new JPanel();
        this.add(panel, "North");
        panel.setLayout(new DVerticalFlowLayout());
        this.titlePanel = new JPanel();
        panel.add(this.titlePanel);
        this.titlePanel.setBackground((Color) null);
        FlowLayout f = new FlowLayout();
        f.setAlignment(0);
        this.titlePanel.setLayout(f);
        JPanel panel_1 = new JPanel();
        panel.add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));
        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
        flowLayout_1.setVgap(0);
        panel_1.add(panel_2, "West");
        panel_2.add(this.button);
        panel_2.add(this.button_1);
        DButton button_2 = new DButton((String) null, (Color) null);
        panel_2.add(button_2);
        button_2.setFont(new Font("Dialog", 1, 12));
        button_2.setText("刷新");
        button_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                java.awt.Component[] var5;
                int var4 = (var5 = MuguiBrowser.this.body.getComponents()).length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    java.awt.Component component = var5[var3];
                    if (component.isVisible()) {
                        BrowserView browserView = (BrowserView) component;
                        Browser browser = browserView.getBrowser();
//                        BrowserContext.defaultContext().getCacheStorage().clearCache();
                        if (!browser.url().startsWith("http://www.mugui.net.cn/serach/")) {
                            browser.navigation().reload();
                        }
                    }
                }

            }
        });
        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3, "Center");
        JLabel label = new JLabel("网址：");
        this.textField = new JTextField();
        this.textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    MuguiBrowser.this.openUrl((String) null);
                }

            }
        });
        this.textField.setColumns(10);
        JPanel jPanel = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) jPanel.getLayout();
        flowLayout_2.setVgap(0);
        DButton button_3 = new DButton((String) null, (Color) null);
        jPanel.add(button_3);
        button_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        button_3.setFont(new Font("Dialog", 1, 14));
        button_3.setText("资源下载");
        GroupLayout gl_panel_3 = new GroupLayout(panel_3);
        gl_panel_3.setHorizontalGroup(gl_panel_3.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_3.createSequentialGroup().addGap(0).addComponent(label).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.textField, -1, 141, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(jPanel, -2, -1, -2)));
        gl_panel_3.setVerticalGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING).addComponent(label, -1, 31, 32767).addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE).addComponent(this.textField, -2, 0, 32767).addComponent(jPanel, -2, 31, -2)));
        DButton button_4 = new DButton((String) null, (Color) null);
        button_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        button_4.setFont(new Font("Dialog", 1, 14));
        button_4.setText("下载列表");
        jPanel.add(button_4);
        panel_3.setLayout(gl_panel_3);
        JPanel panel_4 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
        flowLayout.setAlignment(0);
        this.add(panel_4, "South");
        JLabel lblNewLabel = new JLabel("提示：双击标签栏,关闭一个页面");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setFont(new Font("宋体", 1, 12));
        panel_4.add(lblNewLabel);
    }

    public DBrowserHander openUrl(String url) {
        return openUrl(url,null);

    }

    public DBrowserHander openUrl(String url, Engine engine) {
        if(engine==null){
            engine=this.engine;
        }
        if (url != null) {
            this.textField.setText(url);
        }
        String text = this.textField.getText();
        if (StringUtils.isNotBlank(text)) {
            DBrowserHander browser = new DBrowserHander(engine, MuguiBrowser.this);
            browser.open(text);
            return browser;
        }
        return null;
    }
    private static Engine proxyEngine = null;
    public static synchronized Engine createProxyEngine(String proxy_host,Integer proxy_port) {
        if (proxyEngine == null||proxyEngine.isClosed()) {
            proxyEngine = Engine.newInstance(EngineOptions.newBuilder(OFF_SCREEN).enableProprietaryFeature(ProprietaryFeature.AAC)
                    .enableProprietaryFeature(ProprietaryFeature.H_264).userDataDir(Paths.get("userDataProxy"))
                    .addSwitch("--remote-allow-origins=http://localhost:9223")
                    .remoteDebuggingPort(9223)
                    .licenseKey("1BNDIEOFAZ1Z8R8VNNG4W07HLC9173JJW3RT0P2G9Y28L9YFFIWDBRFNFLFDQBKXAHO9ZE")
                    .build());

            CustomProxyConfig directProxyConfig = CustomProxyConfig.newInstance("socks5://"+ proxy_host+":"+proxy_port);
            proxyEngine.proxy().config(directProxyConfig);
        }
        return proxyEngine;
    }



    public boolean isDownload(Browser browser) {
        if (this.listenerList == null) {
            this.listenerList = new HashMap();
        }

        Iterator var3 = this.listenerList.values().iterator();

        while (var3.hasNext()) {
            Browser browser2 = (Browser) var3.next();
            if (browser2.isClosed()) {
                this.listenerList.remove(browser2.hashCode());
            } else if (browser2.url().equals(browser.url())) {
                return true;
            }
        }

        return false;
    }

    public final void show(String code) {
        this.bodyCard.show(this.body, code);
    }

    public final BrowserView now() {
        for (java.awt.Component component : this.body.getComponents()) {
            if (component.isVisible()) {
                return (BrowserView) component;
            }
        }
        return null;
    }

    public void add(BrowserView browserView, String code) {
        this.body.add(browserView, code);
    }

    protected void addTitlePanel(DButton title) {
        this.titlePanel.add(title);
    }

    protected void setShowUrl(String url) {
        if (!url.startsWith("http://www.mugui.net.cn/serach/")) {
            this.textField.setText(url);
        }

    }

    protected void setCanGoBack(boolean canGoBack) {
        this.button.setEnabled(canGoBack);
    }

    protected void setCanGoForward(boolean canGoForward) {
        this.button_1.setEnabled(canGoForward);
    }

    protected int getBrowserComponentSize() {
        return this.body.getComponentCount();
    }

    protected void last() {
        this.bodyCard.last(this.body);
    }

    protected void removeTitle(DButton title) {
        this.titlePanel.remove(title);
    }

    public void remove(java.awt.Component comp) {
        this.body.remove(comp);
    }

    private DButton getNewTitleObject() {
        DButton title = new DButton((String) null, (Color) null) {
            private static final long serialVersionUID = -5913086528554664138L;

            public void paint(Graphics g) {
                int w = DataSave.context.getBean(AppFrame.class).getWidth() - 20;
                int wight = w / MuguiBrowser.this.titlePanel.getComponentCount() - 10;
                if (wight < 25 && MuguiBrowser.this.weight == 25) {
                    super.paint(g);
                } else if (wight > 150 && MuguiBrowser.this.weight == 150) {
                    super.paint(g);
                } else {
                    if (wight != MuguiBrowser.this.weight) {
                        MuguiBrowser.this.weight = wight;
                        if (MuguiBrowser.this.weight > 150) {
                            MuguiBrowser.this.weight = 150;
                        }

                        if (MuguiBrowser.this.weight < 25) {
                            MuguiBrowser.this.weight = 25;
                        }

                        if (MuguiBrowser.this.weight != this.getWidth()) {
                            java.awt.Component[] var7;
                            int var6 = (var7 = MuguiBrowser.this.titlePanel.getComponents()).length;

                            for (int var5 = 0; var5 < var6; ++var5) {
                                java.awt.Component component = var7[var5];
                                component.setPreferredSize(new Dimension(MuguiBrowser.this.weight, this.getHeight()));
                            }
                        }
                    }

                    super.paint(g);
                }
            }
        };
        title.setText("");
        title.setHorizontalAlignment(2);
        title.setPreferredSize(new Dimension(150, 25));
        title.setFont(new Font("微软雅黑", 0, 12));
        return title;
    }

    @Getter
    @Setter
    private Engine engine = null;


    @Autowired
    private SysConf sysConf;

    public void init() {

        if (this.engine == null||this.engine.isClosed()) {
            this.engine = Engine.newInstance(EngineOptions.newBuilder(OFF_SCREEN).enableProprietaryFeature(ProprietaryFeature.AAC)
                    .enableProprietaryFeature(ProprietaryFeature.H_264).userDataDir(Paths.get("userData"))
                    .addSwitch("--remote-allow-origins=http://localhost:9222")
                    .remoteDebuggingPort(9222)
                    .addSwitch("allow-running-insecure-content") // 允许加载不安全内容
                    .addSwitch("disable-web-security")          // 禁用安全检查（仅用于调试）
//                    .licenseKey("1BNDIEOFAZ1Z8R8VNNG4W07HLC9173JJW3RT0P2G9Y28L9YFFIWDBRFNFLFDQBKXAHO9ZE")
                    .build());

            String value1 = sysConf.getValue("proxy.http.port");
            String value2 = sysConf.getValue("proxy.http.ip");
            if (StrUtil.isAllNotBlank(value2,value1)) {
                CustomProxyConfig directProxyConfig = CustomProxyConfig.newInstance("socks5://"+ value2+":"+12072);
                this.engine.proxy().config(directProxyConfig);
            }else {
                this.engine.proxy().config(SystemProxyConfig.newInstance());
            }
        }
    }

    public void quit() {
        System.out.println(this.getClass().getName() + " quit");
        this.now_point = DataSave.context.getBean(AppFrame.class).getLocation();
        this.now_Dimension = DataSave.context.getBean(AppFrame.class).getSize();
        dispose();
        if(engine!=null)
            engine.close();
    }

    public void dispose() {
        System.out.println(this.getClass().getName() + " dispose");
        this.titlePanel.removeAll();
        for (java.awt.Component component : this.body.getComponents()) {
            BrowserView browserView = (BrowserView) component;
            browserView.getBrowser().close();
            this.body.remove(browserView);
        }
    }

    public void dataInit() {
    }

    public void dataSave() {
    }



    public static abstract class HostListener {
        @Setter
        @Getter
        private String url;


        /**
         * 请求开始
         *
         * @param params
         * @return
         */
        public BeforeSendUploadDataCallback.Response BeforeSendUploadDataCallback(BeforeSendUploadDataCallback.Params params) {
            return null;
        }

        public void BeforeStartTransactionCallback(BeforeStartTransactionCallback.Params params) {
        }


        public void ResponseBytesReceived(ResponseBytesReceived responseBytesReceived) {
        }


        public void titleChanged(DBrowserHander browserHander, TitleChanged titleChanged) {

        }

        public void BeforeUrlRequestCallback(BeforeUrlRequestCallback.Params params) {
        }
    }

    static LinkedHashMap<String, HostListener> hostListeners = new LinkedHashMap<>();

    public void setHostListener(HostListener hostListener) {
        hostListeners.put(hostListener.url, hostListener);
    }

    public static class DBrowserHander  {
        private Engine engine;
        private Browser browser;
        @Getter
        BrowserView browserView;
        private DButton title;
        private MuguiBrowser father;

        public Browser getBrowser() {
            return this.browser;
        }

        public DBrowserHander(Engine engine, final MuguiBrowser father) {

            this.engine = engine;

            this.title = null;
            this.browser = this.engine.newBrowser();


//            browser.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
            this.father = father;
            this.title = father.getNewTitleObject();
            this.title.setActionCommand(String.valueOf(browser.hashCode()));
            father.addTitlePanel(this.title);
            browser.settings().allowJavaScriptAccessCookies();
            browser.settings().allowJavaScriptAccessClipboard();
            browser.settings().allowScriptsToCloseWindows();
            this.browserView = BrowserView.newInstance(browser);
            father.add(this.browserView, String.valueOf(browser.hashCode()));
            browser.on(TitleChanged.class, new Observer<TitleChanged>() {
                @Override
                public void on(TitleChanged titleChanged) {
                    if(!hostListeners.isEmpty()){
                        hostListeners.forEach((key, hostListener) -> {
                            String url = titleChanged.browser().url();
                            if (StrUtil.isBlank(key)||url.contains(key))
                                hostListener.titleChanged(DBrowserHander.this,titleChanged);
                        });
                    }
//                    System.out.println(titleChanged.title());
                    DBrowserHander.this.title.setText(titleChanged.title());
                    father.setShowUrl(browser.url());
                    father.setCanGoBack(browser.navigation().canGoBack());
                    father.setCanGoForward(browser.navigation().canGoForward());
                }
            });
            if (!hostListeners.isEmpty()) {
                //监听browser 所有的网络请求、
                Network network = browser.engine().network();
                network.set(BeforeSendUploadDataCallback.class, params -> {
                    Iterator<Map.Entry<String, HostListener>> iterator = hostListeners.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, HostListener> next = iterator.next();
                        String key = next.getKey();
                        if (StrUtil.isBlank(key)||params.urlRequest().url().contains(key)) {
                            BeforeSendUploadDataCallback.Response response = next.getValue().BeforeSendUploadDataCallback(params);
                            if (response != null) {
                                return response;
                            }
                        }
                    }
                    if (params.uploadData() instanceof TextData) {
                        return BeforeSendUploadDataCallback.Response.override((TextData) params.uploadData());
                    } else if (params.uploadData() instanceof ByteData) {
                        return BeforeSendUploadDataCallback.Response.override((ByteData) params.uploadData());
                    } else if ((params.uploadData() instanceof FormData)) {
                        return BeforeSendUploadDataCallback.Response.override((FormData) params.uploadData());
                    } else if (params.uploadData() instanceof MultipartFormData) {
                        return BeforeSendUploadDataCallback.Response.override((MultipartFormData) params.uploadData());
                    } else {
                        return BeforeSendUploadDataCallback.Response.proceed();
                    }
                });

                network.set(BeforeStartTransactionCallback.class, params -> {
                    hostListeners.forEach((key, hostListener) -> {
                        if (StrUtil.isBlank(key)||params.urlRequest().url().contains(key)) {
                            hostListener.BeforeStartTransactionCallback(params);
                        }
                    });
                    return BeforeStartTransactionCallback.Response.override(params.httpHeaders());
                });
                network.set(BeforeUrlRequestCallback.class, params -> {
                    hostListeners.forEach((key, hostListener) -> {
                        if (StrUtil.isBlank(key)||params.urlRequest().url().contains(key)) {
                            hostListener.BeforeUrlRequestCallback(params);
                        }
                    });
                    return BeforeUrlRequestCallback.Response.proceed();
                });

                network.on(ResponseBytesReceived.class, responseBytesReceived -> {
                    Iterator<Map.Entry<String, HostListener>> iterator = hostListeners.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, HostListener> next = iterator.next();
                        String key = next.getKey();
                        if (StrUtil.isBlank(key)||responseBytesReceived.urlRequest().url().contains(key)) {
                            HostListener value = next.getValue();
                            if (value != null) {
                                value.ResponseBytesReceived(responseBytesReceived);
                            }
                        }
                    }
                });

            }
            browser.set(CreatePopupCallback.class, new CreatePopupCallback() {
                @Override
                public Response on(Params params) {
                    String s = params.targetUrl();
                    ThreadUtil.execute(() -> {
                        DBrowserHander browserHander = new DBrowserHander(DBrowserHander.this.engine, father);
                        browserHander.open(s);
                    });
                    return null;
                }
            });


            this.title.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    if (e.getButton() == 1) {
                        switch (e.getClickCount()) {
                            case 2:
                                father.remove(DBrowserHander.this);
                                if (father.getBrowserComponentSize() == 0) {
                                    DBrowserHander browser = new DBrowserHander(DBrowserHander.this.engine, father);
                                    browser.open("https://www.baidu.com/");
                                } else {
                                    father.last();
                                }
                                father.validate();
                                father.repaint();

                                break;
                            default:
                                show();
                        }

                    }
                }
            });
        }


        public final void show() {
           SwingUtilities.invokeLater(() -> {
               father.show(String.valueOf(browser.hashCode()));
               father.body.validate();
               father.body.repaint();
               father.validate();
               father.repaint();
           });
        }

        public void open(String url) {
            browser.navigation().loadUrl(url);
            show();
        }
    }

    public void remove(DBrowserHander dBrowserHander) {

        remove(dBrowserHander.browserView);
        dBrowserHander.browser.close();
        dBrowserHander.browserView.setEnabled(false);
        removeTitle(dBrowserHander.title);

    }
}
