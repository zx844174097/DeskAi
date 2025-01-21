package com.mugui.sqlite.ui;//package com.mugui.sqlite.ui;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTable;
//import javax.swing.plaf.basic.BasicArrowButton;
//import javax.swing.table.TableCellRenderer;
//
//import lombok.Getter;
//import lombok.Setter;
//
//public class SqliteTableCellRenderer extends JPanel implements TableCellRenderer {
//	public SqliteTableCellRenderer() {
//
//		JLabel lblNewLabel = new JLabel("1");
//
//		JPanel panel = new JPanel();
//		JPanel panel_1 = new JPanel();
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
//				.createSequentialGroup().addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
//				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
//						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
//		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
//						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
//				.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE));
//		panel_1.setLayout(new BorderLayout(0, 0));
//
//		BasicArrowButton lblNewLabel_2 = new BasicArrowButton(BasicArrowButton.SOUTH);
//		lblNewLabel_2.setBackground(Color.WHITE);
//		panel_1.add(lblNewLabel_2);
//		panel.setLayout(new BorderLayout(0, 0));
//		lblNewLabel_2.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(listener!=null) {
//					listener.down();
//				}
//			}
//		});
//
//		BasicArrowButton lblNewLabel_1 = new BasicArrowButton(BasicArrowButton.NORTH);
//		lblNewLabel_1.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(listener!=null) {
//					listener.up();
//				}
//			}
//		});
//		lblNewLabel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if(listener!=null) {
//					listener.blank();
//				}
//			}
//		});
////		lblNewLabel_1.addMouseListener(new MouseAdapter() {
////			@Override
////			public void mouseClicked(MouseEvent e) {
////				if(listener!=null) {
////					listener.up();
////				}
////			}
////		});
////		lblNewLabel_2.addMouseListener(new MouseAdapter() {
////			@Override
////			public void mouseClicked(MouseEvent e) {
////				if(listener!=null) {
////					listener.down();
////				}
////			}
////		});
//		lblNewLabel_1.setBackground(Color.WHITE);
//		panel.add(lblNewLabel_1, BorderLayout.CENTER);
//		setLayout(groupLayout);
//		setPreferredSize(new Dimension(117, 40));
//	}
//
//	@Getter
//	@Setter
//	Listener listener = null;
//
//	public static interface Listener {
//		/**
//		 * 点击了up
//		 */
//		public void up();
//
//		/**
//		 * 点击了down
//		 */
//		public void down();
//
//		/**
//		 * 点击了空白
//		 */
//		public void blank();
//	}
//	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -957706582365642268L;
//
//	@Override
//	public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//			boolean hasFocus, int row, int column) {
//		setSize(40, 25); 
//		return this;
//	}
//
//}