package cn.net.mugui.net.pc.panel.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class ViewChatPanel extends JPanel {

    public ViewChatPanel() {
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
    private MessageBean bean;

    private JSONObject systemObject;

    public void init(MessageBean bean, JSONObject systemObject) {
        this.bean = bean;
        this.systemObject = systemObject;
        view();
    }

    private void view() {
        if (StrUtil.equals(bean.getRole(), MessageBean.ROLE_USER)) {
            userLabel.setText(systemObject.getString("userName"));
        } else if (StrUtil.equals(bean.getRole(), MessageBean.ROLE_ASSISTANT)) {
            userLabel.setText(systemObject.getString("roleName"));
        } else {
            userLabel.setText("系统");
        }
        timeLabel.setText("\t"+DateUtil.formatDateTime(bean.getCreate_time()));
        textArea.setText(viewContent());
    }

    private String viewContent() {
        String content = bean.getContent();
        if(StrUtil.isBlank(content)){
            return "";
        }
        int i = content.indexOf("\"对话\":\"");
        if (i>0) {
            String trim = content.substring(i + 6).trim();
            int i1 = trim.indexOf("\"}");
            if (i1 > 0) {
                return JSONObject.parseObject(content).getString("对话");
            }
            i1 = trim.indexOf("\"");
            if (i1 > 0) {
                return trim.substring(0, i1);
            }
            return trim;
        }
        return "";
    }
}
