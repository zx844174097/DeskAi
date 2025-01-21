package cn.net.mugui.net.pc.panel;

import com.mugui.Dui.DVerticalFlowLayout;

import javax.swing.*;
import java.awt.*;

public class ChatDialog extends JDialog { // 改用 JDialog 以支持更好的透明性
    // 创建透明背景层
    JPanel backgroundPanel =null;
    public ChatDialog(JPanel panel) {
        super((Frame) null);

        // 设置对话框无边框及透明背景
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // 整体透明背景
        setLayout(new BorderLayout());

        backgroundPanel= new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // 半透明背景
                g2d.setComposite(AlphaComposite.SrcOver.derive(0f)); // 透明度调整
                g2d.setColor(new Color(0, 0, 0, 128)); // 半透明黑色背景
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paint(g2d);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false); // 背景层必须不透明，绘制透明效果
        add(backgroundPanel, BorderLayout.CENTER); // 添加到对话框底层
        panel.setBackground(null);
        panel.setForeground(null);
//        backgroundPanel.setLayout(new BorderLayout());
        // 顶部面板
        JPanel topPanel = new JPanel();
        JLabel lblNewLabel = new JLabel("显示框：");
        topPanel.add(lblNewLabel);
        topPanel.setOpaque(true); // 顶部面板保持不透明
        add(topPanel, BorderLayout.NORTH);

        // 启用鼠标拖动功能
        topMove(topPanel);

        // 中心内容
//        panel.setOpaque(true); // 内容面板保持不透明
        backgroundPanel.add(panel, BorderLayout.CENTER);

        // 设置窗口大小及位置
        pack();
        setLocationRelativeTo(null);

        // 设置窗口始终置顶
        setAlwaysOnTop(true);
    }

    int oldX, oldY;

    private void topMove(JPanel panel) {
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                oldX = e.getX();
                oldY = e.getY();
            }
        });
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                int xx = x - oldX;
                int yy = y - oldY;
                ChatDialog.this.setLocation(xx, yy);
            }
        });
    }
}
