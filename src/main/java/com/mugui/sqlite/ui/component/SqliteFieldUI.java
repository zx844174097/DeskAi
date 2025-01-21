package com.mugui.sqlite.ui.component;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import com.mugui.Dui.DTextField;
import com.mugui.Dui.DButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SqliteFieldUI extends JPanel {
	public SqliteFieldUI(String field_name, String data_type) {
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();

		button = new DButton((String) null, (Color) null);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (actionListener != null) {
					e.setSource(SqliteFieldUI.this);
					actionListener.actionPerformed(e);
				}
			}
		});
		button.setFont(new Font("Dialog", Font.PLAIN, 12));
		button.setText("删除");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(button, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
				.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(button, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE).addContainerGap()));

		JLabel label_1 = new JLabel("数据类型：");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);

		dataType = new DTextField(32);
		dataType.setText(data_type);
		if (data_type.equals("PRIMARY_KEY")) {
			dataType.setEditable(false);
			button.setEnabled(false);
		}
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup().addContainerGap()
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(dataType, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE).addContainerGap()));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(label_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
						.addComponent(dataType, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
				.addContainerGap()));
		panel_1.setLayout(gl_panel_1);

		JLabel label = new JLabel("字段名：");
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		fieldName = new DTextField(32);
		fieldName.setText(field_name);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(fieldName, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, gl_panel
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(fieldName, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
				.addContainerGap()));
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5481067753819114167L;
	private DTextField fieldName;
	private DTextField dataType;
	private DButton button;

	public String getFieldName() {
		return fieldName.getText().trim();
	}

	public String getDataType() {
		if (dataType.getText().equals("PRIMARY_KEY")) {
			return " integer PRIMARY KEY autoincrement";
		}
		return dataType.getText().trim();
	}

	ActionListener actionListener = null;

	public void addDeleteActionListener(ActionListener deleteActionListener) {
		actionListener = deleteActionListener;
	}
}
