package cn.net.mugui.net.pc.panel.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.pc.bean.MessageBean;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class TMChatPanel extends JPanel {

    public TMChatPanel() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
        flowLayout.setHgap(10);
        flowLayout.setVgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        add(panel_3, BorderLayout.NORTH);

        userLabel = new JLabel("");
        panel_3.add(userLabel);
        userLabel.setFont(new Font("宋体", Font.BOLD, 22));

        timeLabel = new JLabel("");
        panel_3.add(timeLabel);

        textArea = new JTextArea();
        textArea.setText("");
        //自动换行
        textArea.setLineWrap(true);
        add(textArea, BorderLayout.CENTER);
        textArea.setEditable(false);
        textArea.setFont(new Font("宋体", Font.BOLD, 24));
    }

    JLabel userLabel;
    JLabel timeLabel;
    JTextArea textArea;

    @Getter
    private Msg msg;

    private void view() {
        userLabel.setText(msg.getSrcUser().name+"("+msg.getSrcUser().level+")");
        timeLabel.setText("\t" + DateUtil.formatDateTime(new Date(msg.getTime())));
        textArea.setText(viewContent());
    }

    private String viewContent() {
        String content = msg.getContent();
        return content;
    }

    public void init(Msg sendmsg) {

        this.msg = sendmsg;
        view();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        // 半透明背景
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f)); // 透明度调整
        g2d.setColor(new Color(0, 0, 0, 128)); // 半透明黑色背景
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g2d);
        g2d.dispose();
    }
}
