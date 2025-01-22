package cn.net.mugui.net.pc.panel;

import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.handler.GlobalKeyListenerExample;
import cn.net.mugui.net.pc.manager.FunctionUI;

import cn.net.mugui.net.pc.task.DesktopAiTask;
import cn.net.mugui.net.pc.task.MicrosoftSpeechRecognizer;
import cn.net.mugui.net.pc.util.*;
import cn.net.mugui.net.web.util.SysConf;
import com.alibaba.fastjson.JSONObject;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.mugui.Dui.DButton;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

@Component
public class GptPanel extends FunctionUI {


    public GptPanel() {
        setTitle("聊天");
        setMenu_name("聊天");
        setLayout(new BorderLayout(0, 0));

        initComponents();

    }

    ChatDialog chatDialog = null;


    @Autowired
    private ViewAiChatPanel viewAiChatPanel;


    @Override
    public void init() {

        if (wxUtil.checkLogin()) {
            wxButton.setText("解除绑定微信");
            wxUtil.hookWxMsg();

        }


        textArea1.setText("");
        MsglinkedList.clear();

        if (chatDialog == null) {
            chatDialog = new ChatDialog(viewAiChatPanel);
            chatDialog.setBounds(0, 0, 600, Toolkit.getDefaultToolkit().getScreenSize().height - 100);
            chatDialog.setVisible(true);
            viewAiChatPanel.init(chatDialog);

            // 注册全局键盘监听
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(globalKeyListenerExample);
                System.out.println("Listening for system shortcuts...");
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }


    }

    @Autowired
    private GlobalKeyListenerExample globalKeyListenerExample;

    @Override
    public void quit() {

    }

    @Override
    public void dataInit() {

    }

    @Override
    public void dataSave() {

    }

    @Autowired
    private QQUtil qqUtil;


    @Autowired
    private WxUtil wxUtil;


    @Autowired
    private SysConf sysConf;

    DButton wxButton = null;
    DButton aiButton = null;

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setLineWrap(true);
        textArea1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));

        //======== this ========
        setLayout(new BorderLayout(3, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());
        }
        add(panel1, BorderLayout.NORTH);

        DButton button = new DButton("QQ登录", (Color) null);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (button.getText().equals("QQ登录")) {
                        qqUtil.login();
                        qqUtil.listenerAllMsg();
                        button.setText("QQ退登");
                    } else {
                        qqUtil.unLogin();
                        button.setText("QQ登录");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel1.add(button);


        wxButton = new DButton("绑定微信", (Color) null);
        wxButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (wxButton.getText().equals("绑定微信")) {
                    wxUtil.login();
                    wxButton.setText("解除绑定微信");
                } else {
                    wxUtil.unHookWx();
                    wxButton.setText("绑定微信");
                }
            }
        });
        panel1.add(wxButton);


        aiButton = new DButton("ai精灵", (Color) null);
        aiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (aiButton.getText().equals("ai精灵")) {
                    microsoftSpeechRecognizer.start();
                    aiButton.setText("关闭ai精灵");
                } else {
                    microsoftSpeechRecognizer.stop();
                    aiButton.setText("ai精灵");
                }
            }
        });
        panel1.add(aiButton);

        DButton sysButton = new DButton("系统信息", (Color) null);
        sysButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JSONObject now = SystemUtil.now();
                textArea1.append("\r\n" + now.toJSONString());
            }
        });
        panel1.add(sysButton);

        DButton douyuButton = new DButton("弹幕助手", (Color) null);
        douyuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (douyuButton.getText().equals("弹幕助手")) {
                    douyuTMUtil.start();
                    douyuButton.setText("关闭弹幕助手");
                } else {
                    douyuTMUtil.stop();
                    douyuButton.setText("弹幕助手");
                }
            }
        });
        panel1.add(douyuButton);


        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textArea1);
        }
        add(scrollPane1, BorderLayout.CENTER);

        panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        JTextArea textArea = new JTextArea();
        dButton3 = new DButton("发送聊天", null);
        dButton3.addActionListener(e-> ThreadUtil.execute(()-> {
                    String text = textArea.getText();
                    if (StrUtil.isBlank(text)) {
                        return;
                    }
                    String threadId = chatGptUtil.createThread("master");
                    MessageBean messageBean1 = MessageBean.newUser(threadId, "master", text);
                    messageBean1.setStatus(MessageBean.Status.SUCCESS.getValue());
                    MessageBean.update(messageBean1);
                    desktopAiTask.add(messageBean1);
                })
        );
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(dButton3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                        .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                                                .addComponent(dButton3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
        );

        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);
        panel.setLayout(gl_panel);

    }



    @Autowired
    private MicrosoftSpeechRecognizer microsoftSpeechRecognizer;


    @Autowired
    private ChatGptUtil chatGptUtil;

    @Autowired
    private DouyuTMUtil douyuTMUtil;


    static ConcurrentLinkedDeque<MessageBean> MsglinkedList = new ConcurrentLinkedDeque<>();

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private DButton dButton3;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JPanel panel;


    @Autowired
    private DesktopAiTask desktopAiTask;

}
