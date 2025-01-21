package com.mugui.sqlite.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.mugui.sql.TableMode;
import com.mugui.sqlite.bean.SqliteColumeBean;
import com.mugui.sqlite.dao.Sqlite;

import lombok.Getter;

public class SqliteTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8334368076255812013L;
	TableMode mode = null;

	private Sqlite sqlite = new Sqlite();

	@Getter
	private String table_name = null;

	public SqliteTableModel(TableMode selectSql) {
		this.mode = selectSql;
		this.table_name = mode.getTableName();
		if (this.mode.getRowCount() <= 0) {
			ArrayList<String> arrayList = new ArrayList<>();
			TableMode selectSql2 = sqlite.selectSql("PRAGMA table_info('" + table_name + "')");
			selectSql2.getData().iterator().forEachRemaining(e -> {
				SqliteColumeBean newBean = SqliteColumeBean.newBean(SqliteColumeBean.class, e);
				arrayList.add(newBean.getName());
			});
			this.mode.setColumnNames(arrayList);
		}
	}

	@Override
	public int getRowCount() {
		return mode.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return mode.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return mode.getColumnName(columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return mode.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {

			object = aValue;
			fireTableCellUpdated(rowIndex, columnIndex);
			mode.setValueAt(aValue, rowIndex, columnIndex);
		} catch (Exception e) {
		}
	}

	Object object = null;

	public Object getNewValue() {
		return object;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeRow(int row) {
		fireTableRowsDeleted(row, row);
		mode.removeRow(row);
	}

	public void addRow(Object[] object) {
		fireTableRowsInserted(mode.getRowCount(), mode.getRowCount());
	}
}
