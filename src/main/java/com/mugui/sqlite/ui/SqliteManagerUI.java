package com.mugui.sqlite.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;

import com.mugui.Dui.DButton;
import com.mugui.Dui.DOptionPanel;
import com.mugui.Dui.DPanel;
import com.mugui.Dui.DVerticalFlowLayout;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.sql.TableMode;
import com.mugui.sqlite.dao.Sqlite;

@Component
public class SqliteManagerUI extends DPanel {
	public SqliteManagerUI() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GRAY);
		panel.add(panel_1, BorderLayout.NORTH);

		JLabel label = new JLabel("表列表");
		panel_1.add(label);

		DButton button = new DButton((String) null, (Color) null);
		button.addActionListener(new ActionListener() {
			SqliteCreateUI sqliteCreateUI = null;

			public void actionPerformed(ActionEvent e) {
				if (sqliteCreateUI == null)
					sqliteCreateUI = new SqliteCreateUI();
				int showPanel = DOptionPanel.showPanel(SqliteManagerUI.this, sqliteCreateUI,
						DOptionPanel.OPTION_OK_CANCEL);
				if (showPanel == DOptionPanel.AGREE) {
					Map<String, String> field = sqliteCreateUI.getField();
					Set<Entry<String, String>> entrySet = field.entrySet();
					if (StringUtils.isBlank(sqliteCreateUI.getTABLE())) {
						DOptionPanel.showMessageDialog(SqliteManagerUI.this, "表名不能为空", "错误", DOptionPanel.OPTION_OK);
						return;
					}
					String sql = "CREATE TABLE " + sqliteCreateUI.getTABLE() + "(\n";
					boolean bool = false;
					for (Entry<String, String> entry : entrySet) {
						if (StringUtils.isBlank(entry.getKey()) || StringUtils.isBlank(entry.getValue())) {
							DOptionPanel.showMessageDialog(SqliteManagerUI.this, "请勿创建空参数", "错误",
									DOptionPanel.OPTION_OK);
							return;
						}
						if (bool)
							sql += ",\n`" + entry.getKey() + "` ";
						else
							sql += "`" + entry.getKey() + "` ";
						bool = true;
						sql += entry.getValue() + " ";

					}
					sql += ");";
					System.out.println(sql);
					sqlite.updateSql(sql);
					// 加载表列表
					loadTables();
					sqliteCreateUI = null;
				}
			}
		});
		button.setFont(new Font("Dialog", Font.PLAIN, 13));
		button.setText("新建");
		panel_1.add(button);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addComponent(panel_3,
				GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addComponent(panel_3,
				GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE));
		panel_3.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_3.add(scrollPane);

		table_list = new JPanel();
		scrollPane.setViewportView(table_list);
		table_list.setLayout(new DVerticalFlowLayout());

		panel_2.setLayout(gl_panel_2);

		JPanel panel_5 = new JPanel();

		add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		panel_5.add(scrollPane_1, BorderLayout.CENTER);

		table = new JTable();
		scrollPane_1.setViewportView(table);
		table.setSurrendersFocusOnKeystroke(true);
		table.setToolTipText("");
//		table.getTableHeader().setDefaultRenderer(new SqliteTableCellRenderer());
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String string = label_table_name.getText().split("[：]")[1].trim();
				if (StringUtils.isNotBlank(string)) {
					int columnAtPoint = table.getTableHeader().columnAtPoint(e.getPoint());
					String columnName = sqliteTableModel.getColumnName(columnAtPoint);

					if (columnName.equals(last_comnut)) {
						down_or_up = (down_or_up + 1);
						if (down_or_up > 1) {
							down_or_up = -1;
						}
					} else {
						down_or_up = 1;
					}

					showTable(string, index_i, index_len, columnName, down_or_up);
					last_comnut = columnName;
				}
			}
		});
		JPanel panel_4 = new JPanel();
		panel_5.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new BorderLayout(0, 0));

		JPanel panel_8 = new JPanel();
		panel_4.add(panel_8);
		panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		label_table_name = new JLabel("表：");
		panel_8.add(label_table_name);
		label_table_name.setFont(new Font("宋体", Font.BOLD, 14));

		DButton button_1 = new DButton((String) null, (Color) null);
		panel_8.add(button_1);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selectedRow = table.getSelectedRows();
				if (selectedRow != null && selectedRow.length > 0 && sqliteTableModel != null)
					for (int i : selectedRow)
						sqliteTableModel.removeRow(table.getSelectedRow());
			}
		});
		button_1.setFont(new Font("Dialog", Font.BOLD, 13));
		button_1.setText("删除选中");

		DButton button_5 = new DButton((String) null, (Color) null);
		panel_8.add(button_5);
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sqliteTableModel != null) {
					try {

						StringBuffer sql = new StringBuffer();
						sql.append("INSERT INTO `").append(sqliteTableModel.getTable_name())
								.append("`(`" + sqliteTableModel.getColumnName(0) + "`) VALUES (null)");
						sqlite.updateSql(sql.toString());
						TableMode tMode = sqlite.selectBy("select_last_id");
						String i = tMode.getValueAt(0, 0);
						sqliteTableModel.mode.addRow(new String[] { i });
						sqliteTableModel.addRow(new String[] { i });
						table.setName(sqliteTableModel.getTable_name());
						table.setModel(sqliteTableModel);
						table.updateUI();
						label_table_name.setText("表：" + sqliteTableModel.getTable_name());

						showTable(sqliteTableModel.getTable_name(), index_i, index_len, null, down_or_up);
					} catch (Exception e2) {
					}
				}
			}
		});
		button_5.setFont(new Font("Dialog", Font.BOLD, 13));
		button_5.setText("新增");

		JPanel panel_9 = new JPanel();
		panel_4.add(panel_9, BorderLayout.EAST);

		DButton button_6 = new DButton((String) null, (Color) null);
		panel_9.add(button_6);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int showMessageDialog = DOptionPanel.showMessageDialog(SqliteManagerUI.this, "确定删除该表，将无法恢复", "警告",
						DOptionPanel.OPTION_OK_CANCEL);
				if (showMessageDialog == DOptionPanel.AGREE) {
					String string = label_table_name.getText().split("[：]")[1].trim();
					if (StringUtils.isNotBlank(string)) {
						sqlite.updateSql("DROP TABLE " + string + ";");
						loadTables();
						java.awt.Component component = table_list.getComponent(0);
						if (component != null) {
							showTable(((DButton) component).getActionCommand(), 0, index_len);
						}
					}
				}
			}
		});
		button_6.setText("删除该表");
		button_6.setFont(new Font("Dialog", Font.BOLD, 13));

		JPanel panel_7 = new JPanel();
		panel_5.add(panel_7, BorderLayout.SOUTH);

		DButton button_4 = new DButton((String) null, (Color) null);
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String string = label_table_name.getText().split("[：]")[1].trim();
				if (StringUtils.isNotBlank(string)) {
					showTable(string, 0, index_len);
				}
			}
		});
		button_4.setFont(new Font("Dialog", Font.BOLD, 13));
		button_4.setText("第一页");
		panel_7.add(button_4);

		DButton button_2 = new DButton((String) null, (Color) null);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String string = label_table_name.getText().split("[：]")[1].trim();
				if (StringUtils.isNotBlank(string)) {
					showTable(string, index_i - index_len, index_len);
				}
			}
		});
		button_2.setFont(new Font("Dialog", Font.BOLD, 13));
		button_2.setText("上一页");
		panel_7.add(button_2);

		DButton button_3 = new DButton((String) null, (Color) null);
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String string = label_table_name.getText().split("[：]")[1].trim();
				if (StringUtils.isNotBlank(string)) {
					showTable(string, index_i + index_len, index_len);
				}
			}
		});
		button_3.setFont(new Font("Dialog", Font.BOLD, 13));
		button_3.setText("下一页");
		panel_7.add(button_3);

		JPanel panel_6 = new JPanel();
		add(panel_6, BorderLayout.NORTH);

		JLabel lblSqlite = new JLabel("sqlite管理");
		panel_6.add(lblSqlite);
		lblSqlite.setFont(new Font("宋体", Font.BOLD, 15));
		lblSqlite.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel_10 = new JPanel();
		add(panel_10, BorderLayout.SOUTH);
		panel_10.setLayout(new BorderLayout(0, 0));

		JPanel panel_11 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_11.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(10);
		panel_10.add(panel_11, BorderLayout.EAST);

		JTextArea textArea = new JTextArea();
		
		DButton button_7 = new DButton((String) null, (Color) null);
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedText = textArea.getSelectedText();
				if (StringUtils.isBlank(selectedText)) {
					selectedText = textArea.getText();
				}

				if(selectedText.trim().toLowerCase().startsWith("select")){
					TableMode selectBy = sqlite.selectSql(selectedText);
					showSqliteTableModel(selectBy);
				}else if(selectedText.trim().toLowerCase().startsWith("delete")){
					boolean updateSql = sqlite.updateSql(selectedText);
					System.out.println("updatesql-> "+updateSql);
				}else if(selectedText.trim().toLowerCase().startsWith("update")) {
					boolean updateSql = sqlite.updateSql(selectedText);
					System.out.println("updatesql-> "+updateSql);
				}
				
			}
		});
		button_7.setFont(new Font("Dialog", Font.BOLD, 11));
		button_7.setText("执行");
		panel_11.add(button_7);

		JPanel panel_12 = new JPanel();
		panel_10.add(panel_12, BorderLayout.NORTH);
		panel_12.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_12.add(scrollPane_2, BorderLayout.CENTER);
		
				scrollPane_2.setViewportView(textArea);
				textArea.setFont(new Font("微软雅黑", Font.PLAIN, 25));
				textArea.setRows(4);
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textArea.addKeyListener(new KeyAdapter() {

					boolean alt = false;

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ALT) {
							alt = true;
						}
						super.keyPressed(e);
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER && alt) {
							String selectedText = textArea.getSelectedText();
							if (StringUtils.isBlank(selectedText)) {
								selectedText = textArea.getText();
							}
							if(selectedText.trim().toLowerCase().startsWith("select")){
								TableMode selectBy = sqlite.selectSql(selectedText);
								showSqliteTableModel(selectBy);
							}else if(selectedText.trim().toLowerCase().startsWith("delete")){
								boolean updateSql = sqlite.updateSql(selectedText);
								System.out.println("updatesql-> "+updateSql);
							}else if(selectedText.trim().toLowerCase().startsWith("update")) {
								boolean updateSql = sqlite.updateSql(selectedText);
								System.out.println("updatesql-> "+updateSql);
							}
						} else if (e.getKeyCode() == KeyEvent.VK_ALT) {
							alt = false;
						}
						super.keyReleased(e);
					}
				});
	}

	public void FitTableColumns(JTable myTable) { // 設置table的列寬隨內容調整

		JTableHeader header = myTable.getTableHeader();

		int rowCount = myTable.getRowCount();

		Enumeration columns = myTable.getColumnModel().getColumns();
		int zong_wight = 0;
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();

			int col = header.getColumnModel().getColumnIndex(

					column.getIdentifier());

			int width = (int) 50;
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)

						.getTableCellRendererComponent(myTable,

								myTable.getValueAt(row, col), false, false,

								row, col)
						.getPreferredSize().getWidth();

				width = Math.max(width, preferedWidth);

			}

			header.setResizingColumn(column);
			width = width + myTable.getIntercellSpacing().width;
			if (width > 200) {
				width = 200;
			}
			column.setWidth(width);
			zong_wight += width;
		}
		double b = (scrollPane_1.getWidth() * 1.0) / (zong_wight * 1.0);
		columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			column.setWidth((int) (column.getWidth() * b));
		}

	}

	private String last_comnut = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 917502287676929848L;
	private JTable table;

	@Autowired
	private Sqlite sqlite = null;
	private JPanel table_list;

	@Override
	public void init() {
		// 加载表列表
		loadTables();
	}

	private void loadTables() {
		table_list.removeAll();
		Iterator<String> iterator = sqlite.selectTableNames().iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			DButton btnName = new DButton((String) next, (Color) Color.DARK_GRAY);
			btnName.setBackground(Color.WHITE);
			table_list.add(btnName);
			btnName.addActionListener(show_table);
		}
		FitTableColumns(table);
		table_list.validate();
		table_list.repaint();
	}

	/**
	 * 显示表数据
	 */
	private ActionListener show_table = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			showTable(e.getActionCommand(), 0, index_len);
		}
	};

	private int index_i;
	private int index_len = 50;
	private JLabel label_table_name;

	SqliteTableModel sqliteTableModel = null;

	private int down_or_up = 0;
	private JScrollPane scrollPane_1;

	/**
	 * 带排序的显示表数据
	 */
	private void showTable(String table_name, int i, int j, String field_name, int down_or_up) {
		this.down_or_up = down_or_up;
		if (i < 0)
			i = 0;
		label_table_name.setText("表：" + table_name);
		TableMode selectSql = null;
		if (field_name != null && down_or_up != 0) {
			selectSql = sqlite.selectSql("select * from `" + table_name + "`   order by `" + field_name + "` "
					+ (down_or_up > 0 ? "DESC" : "ASC") + " limit " + i + " , " + index_len);
		} else {
			selectSql = sqlite.selectSql("select * from `" + table_name + "` limit " + i + " , " + index_len);
		}
		if (selectSql.getRowCount() == 0 && i != 0) {
			return;
		}
		this.index_i = i;
		this.index_len = j;

		showSqliteTableModel(selectSql);
		FitTableColumns(table);
	}

	private void showSqliteTableModel(TableMode selectSql) {
		sqliteTableModel = new SqliteTableModel(selectSql);
		sqliteTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				// 表格数据发生改变，更新这个表格数据
				System.out.println(e.getFirstRow() + " " + e.getLastRow() + " " + e.getColumn() + " " + e.getSource()
						+ " " + e.getType());
				SqliteTableModel tableModel = (SqliteTableModel) e.getSource();
				StringBuffer sql = new StringBuffer();
				switch (e.getType()) {
				case TableModelEvent.DELETE:
					sql.append("DELETE FROM `").append(tableModel.getTable_name()).append("` WHERE `")
							.append(tableModel.getColumnName(0)).append("`=?");
					sqlite.updateSql(sql.toString(), tableModel.getValueAt(e.getLastRow(), 0));
					break;
				case TableModelEvent.UPDATE:
					sql.append("UPDATE `").append(tableModel.getTable_name()).append("` SET ");
					sql.append("`").append(tableModel.getColumnName(e.getColumn())).append("`=?");
					sql.append(" WHERE `").append(tableModel.getColumnName(0)).append("`=? ");
					sqlite.updateSql(sql.toString(), tableModel.getNewValue(),
							tableModel.getValueAt(e.getLastRow(), 0));
					break;
				case TableModelEvent.INSERT:// 新增

					break;
				default:
					break;
				}
			}
		});

		table.setName(sqliteTableModel.getTable_name());
		table.setModel(sqliteTableModel);
		label_table_name.setText("表：" + sqliteTableModel.getTable_name());
		FitTableColumns(table);
		table.validate();
		table.repaint();
	}

	/**
	 * 显示表数据
	 * 
	 * @auther 木鬼
	 * @param i
	 * @param j
	 */
	private void showTable(String table_name, int i, int j) {
		showTable(table_name, i, j, null, 0);
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

}
