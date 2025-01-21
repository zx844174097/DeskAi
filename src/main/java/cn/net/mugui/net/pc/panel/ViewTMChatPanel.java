package cn.net.mugui.net.pc.panel;

import cn.hutool.core.thread.ThreadUtil;
import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.panel.component.TMChatPanel;
import com.alibaba.fastjson.JSONObject;
import com.mugui.Dui.DPanel;
import com.mugui.Dui.DVerticalFlowLayout;
import com.mugui.base.base.Component;
import com.mugui.util.Other;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Component
public class ViewTMChatPanel extends DPanel {
    JScrollPane scrollPane = new JScrollPane();
    public ViewTMChatPanel() {
        super();
        setLayout(new BorderLayout(0, 0));

        add(scrollPane, BorderLayout.CENTER);
        //关闭横向滚动条
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setViewportView(listPanel);
        listPanel.setLayout(new DVerticalFlowLayout());

    }

    JPanel listPanel = new JPanel();


    @Override
    public void init() {

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
    }

    public void add(Msg sendmsg) {
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
        while (components.length > 100) {
            listPanel.remove(0);
        }

        TMChatPanel viewChatPanel = new TMChatPanel();
        viewChatPanel.init(sendmsg);
        listPanel.add(viewChatPanel);
    }
}
