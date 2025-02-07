package cn.net.mugui.net.pc.panel.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

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
        timeLabel.setText("\t"+DateUtil.formatDateTime(new Date(bean.getCreate_time().getTime()+8*60*60*1000)));
        textArea.setText(viewContent());
    }

    private String viewContent() {
        String content = bean.getContent();
        if(StrUtil.isBlank(content)){
            return "";
        }
        try{
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
        }catch (Exception e){
            e.printStackTrace();
            return content;
        }
        return "";
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
