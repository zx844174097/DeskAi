package cn.net.mugui.net.pc.panel;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.panel.component.ViewChatPanel;
import com.alibaba.fastjson.JSONObject;
import com.mugui.Dui.DPanel;
import com.mugui.Dui.DVerticalFlowLayout;
import com.mugui.base.base.Component;
import com.mugui.util.Other;

@Component
public class ViewAiChatPanel extends DPanel {
    JScrollPane scrollPane = new JScrollPane() ;

    public ViewAiChatPanel() {
        super();


        setLayout(new BorderLayout(0, 0));

        add(scrollPane, BorderLayout.CENTER);
        //关闭横向滚动条
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setViewportView(listPanel);
        listPanel.setLayout(new DVerticalFlowLayout());

    }

    JPanel listPanel = new JPanel() ;


    @Override
    public void init() {

    }


    private void add(MessageBean bean, JSONObject systemObject) {

        if (Objects.equals(bean.getRole(), MessageBean.ROLE_LIVE_USER)) {
            return;
        }

        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                Other.sleep(100);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listPanel.revalidate();
                        scrollPane.revalidate();
                        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                    }
                });
            }
        });

        java.awt.Component[] components = listPanel.getComponents();
        for (java.awt.Component component : components) {
            if (component instanceof ViewChatPanel) {
                ViewChatPanel viewChatPanel = (ViewChatPanel) component;
                if (Objects.equals(viewChatPanel.getBean().getMessage_id(), bean.getMessage_id())) {
                    viewChatPanel.init(bean, systemObject);
                    return;
                }
            }
        }

        while (components.length > 100) {
            listPanel.remove(0);
        }
        ViewChatPanel viewChatPanel = new ViewChatPanel();
        viewChatPanel.init(bean, systemObject);
        listPanel.add(viewChatPanel);
    }

    @Override
    public void quit() {

    }

    @Override
    public void dataInit() {

    }

    @Override
    public void dataSave() {

    }

    private ChatDialog father;

    public void init(ChatDialog father) {
        this.father = father;

        MessageBean messageBean = MessageBean.byDesc(new MessageBean());
        if (messageBean == null) {
            return;
        }
        List<MessageBean> all = MessageBean.all(messageBean.getSession_id());

        MessageBean systemMsg = all.remove(0);
        JSONObject object = JSONObject.parseObject(systemMsg.getContent());
        for (MessageBean bean : all) {
            add(bean, object);
        }
    }

    public void add(MessageBean sendmsg) {
        try {
            JSONObject object = new JSONObject();
            object.put("userName", "主人");
            object.put("roleName", "猫娘");
            add(sendmsg, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
