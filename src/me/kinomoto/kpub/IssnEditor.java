package me.kinomoto.kpub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import me.kinomoto.kpub.Issn.IssnLenghtException;
import me.kinomoto.kpub.Issn.IssnZeroException;

/**
 * Excel like editor for {@link Issn Issns} from {@link IssnModel}. Use {@link #getRef()}.
 */
public class IssnEditor extends JFrame {
	private static final long serialVersionUID = -3082230747229726339L;

	private static final int MIN_WIDTH = 600;
	private static final int MIN_HEIGHT = 400;
	private static final int ROW_HEIGHT = 30;
	private static final int ISSN_COLUMN_WIDTH = 100;
	private static final int TYP_COLUMN_WIDTH = 100;
	private static final int BORDER = 5;

	private static final int ISSN_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private static final int TYPE_COLUMN = 2;

	private static final IssnEditor ref = new IssnEditor();

	private JPanel contentPane;
	private JTable table;
	private TableModel model;

	private IssnEditor() {
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JComboBox<String> typeEditor = new JComboBox<String>();
		typeEditor = new JComboBox<String>(Issn.getIssnTypesArray());

		model = new TableModel();
		table = new JTable(model);
		table.setRowHeight(ROW_HEIGHT);

		TableColumn columnType = table.getColumnModel().getColumn(TYPE_COLUMN);
		columnType.setCellEditor(new DefaultCellEditor(typeEditor));
		columnType.setMaxWidth(TYP_COLUMN_WIDTH);
		columnType.setMinWidth(TYP_COLUMN_WIDTH);

		TableColumn columnIssn = table.getColumnModel().getColumn(ISSN_COLUMN);
		columnIssn.setCellEditor(new DefaultCellEditor(new IssnTextEditor()));
		columnIssn.setMaxWidth(ISSN_COLUMN_WIDTH);
		columnIssn.setMinWidth(ISSN_COLUMN_WIDTH);

		//contentPane.add(table, BorderLayout.CENTER);
		
		JScrollPane pane = new JScrollPane(table);
		contentPane.add(pane, BorderLayout.CENTER);
		
		initToolBar();
	}

	private void initToolBar() {
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(toolBar, BorderLayout.NORTH);
		JButton newIssn = new JButton("Add new ISSN");
		toolBar.add(newIssn);

		newIssn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IssnAdder adder = new IssnAdder();
				adder.setVisible(true);
			}
		});

		JButton rmIssn = new JButton("Delete ISSN");
		toolBar.add(rmIssn);

		rmIssn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1 && JOptionPane.showConfirmDialog(null, "Do you realy want to delete selected issn?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					model.removeRow(table.getSelectedRow());
				}
			}
		});
	}

	/**
	 * Updates issn data from {@link IssnModel#getModelStringArray()}.
	 */
	public void updateTable() {
		model.updateData(IssnModel.getModelStringArray());
	}

	private class TableModel extends AbstractTableModel implements TableModelListener {
		private static final long serialVersionUID = -2567708186819392248L;

		private String[] columnNames = { "ISSN", "ISSN Name", "Type" };
		private Object[][] data = new Object[0][columnNames.length];

		private boolean changing = false;
		private String changeIssn = "";

		/**
		 * Makes new {@link TableModel}.
		 */
		public TableModel() {
			addTableModelListener(this);
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
		@Override
		public String getColumnName(int index) {
			return columnNames[index];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			changeIssn = (String) data[rowIndex][0];
			data[rowIndex][columnIndex] = aValue;
			fireTableCellUpdated(rowIndex, columnIndex);
		}

		private void setValueAtNoChanging(String value, int row, int col) {
			changing = true;
			data[row][col] = value;
			fireTableCellUpdated(row, col);
			changing = false;
		}

		/**
		 * Updates data in table. Used by {@link IssnEditor#updateTable()}.
		 * 
		 * @param data
		 */
		public void updateData(Object[][] data) {
			changing = true;
			this.data = data;
			fireTableDataChanged();
			changing = false;
		}

		private void issnChanged(String value, Issn issn, int row) {
			try {
				if (!Issn.checkIssn(value))
					throw new Issn.ChecksumException();
				issn.setIssn(value);
			} catch (SQLException e1) {
				this.setValueAtNoChanging(issn.getIssnString(), row, ISSN_COLUMN);
				ErrorDialog.error(e1.getMessage(), ref);
			} catch (Issn.ChecksumException e1) {
				this.setValueAtNoChanging(issn.getIssnString(), row, ISSN_COLUMN);
				ErrorDialog.error("Issn check sum error", ref);
			} catch (NumberFormatException e1) {
				this.setValueAtNoChanging(issn.getIssnString(), row, ISSN_COLUMN);
			} catch (IssnZeroException e) {
				this.setValueAtNoChanging(issn.getIssnString(), row, ISSN_COLUMN);
				ErrorDialog.error("Issn can not be 0000-0000", ref);
			} catch (IssnLenghtException e) {
				this.setValueAtNoChanging(issn.getIssnString(), row, ISSN_COLUMN);
				ErrorDialog.error("Issn must be in format 1234-567X", ref);
			}
		}

		private void typeChanged(String value, Issn issn, int row) {
			try {
				issn.setType(value);
			} catch (SQLException e1) {
				this.setValueAtNoChanging(issn.getTypeString(), row, TYPE_COLUMN);
				Main.error(e1.getMessage());
			}
		}

		private void nameChanged(String value, Issn issn, int row) {
			try {
				issn.setName(value);
			} catch (SQLException e1) {
				this.setValueAtNoChanging(issn.getName(), row, NAME_COLUMN);
				Main.error(e1.getMessage());
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (changing)
				return;
			int row = e.getFirstRow();
			int column = e.getColumn();
			String changedTo = (String) getValueAt(row, column);
			Issn issn = IssnModel.getIssnByIssn(changeIssn);
			if (column == ISSN_COLUMN) {
				issnChanged(changedTo, issn, row);
			} else if (column == TYPE_COLUMN) {
				typeChanged(changedTo, issn, row);
			} else if (column == NAME_COLUMN) {
				nameChanged(changedTo, issn, row);
			}
		}

		/**
		 * Deletes issn stored in <b>row</b>, uses {@link IssnModel#removeIssn(Issn)}
		 * 
		 * @param row
		 */
		public void removeRow(int row) {
			Issn issn = IssnModel.getIssnByIssn((String) data[row][ISSN_COLUMN]);
			try {
				IssnModel.removeIssn(issn);
			} catch (SQLException e) {
				ErrorDialog.error(e.getMessage(), IssnEditor.this);
			}
		}

	}

	/**
	 * 
	 * @return
	 *         Reference to static {@link IssnEditor}.
	 */
	public static IssnEditor getRef() {
		return ref;
	}

}
