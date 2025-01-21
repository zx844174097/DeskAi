package com.mugui.sqlite.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.mugui.Dui.DButton;
import com.mugui.Dui.DPanel;
import com.mugui.Dui.DTextField;
import com.mugui.Dui.DVerticalFlowLayout;
import com.mugui.sqlite.ui.component.SqliteFieldUI;

/**
 * 创建一个新的表
 * 
 * @author 木鬼
 *
 */
public class SqliteCreateUI extends DPanel {
	public SqliteCreateUI() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		JLabel label = new JLabel("新建数据表");
		label.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(label);

		DButton button = new DButton((String) null, (Color) null);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SqliteFieldUI sqliteFieldUI = new SqliteFieldUI("", "varchar(64)");
				panel_2.add(sqliteFieldUI);
				sqliteFieldUI.addDeleteActionListener(deleteActionListener);
				panel_2.validate();
				panel_2.repaint();
				scrollPane.validate();
				scrollPane.repaint();
				JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
				verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			}
		});

		textField = new DTextField(64);
		textField.setColumns(15);
		panel.add(textField);
		button.setFont(new Font("Dialog", Font.BOLD, 14));
		button.setText("添加");
		panel.add(button);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_1.add(scrollPane);

		panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new DVerticalFlowLayout());

		SqliteFieldUI sqliteFieldUI = new SqliteFieldUI("id", "PRIMARY_KEY");
		panel_2.add(sqliteFieldUI);
		sqliteFieldUI.addDeleteActionListener(deleteActionListener);
	}

	private ActionListener deleteActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			panel_2.remove((Component) e.getSource());
			panel_2.validate();
			panel_2.repaint();
			JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
			verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			scrollPane.validate();
			scrollPane.repaint();
		}
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = 2251923050918900750L;
	private JPanel panel_2;
	private DTextField textField;
	private JScrollPane scrollPane;

	public HashMap<String, String> getField() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Component[] components = panel_2.getComponents();
		for (Component component : components) {
			if (component instanceof SqliteFieldUI) {
				SqliteFieldUI sqliteFieldUI = (SqliteFieldUI) component;
				map.put(sqliteFieldUI.getFieldName(), sqliteFieldUI.getDataType());
			}
		}
		return map;
	}

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

	public String getTABLE() {
		return textField.getText();
	}

}
