package me.kinomoto.kpub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;

/**
 * Excel like editor for {@link LangModel}.
 */
public class LangEditor extends JFrame {
	private static final long serialVersionUID = 6285716650775656283L;
	private static final int BORDER = 5;
	private static final int MIN_WIDTH = 600;
	private static final int MIN_HEIGHT = 400;

	private JPanel contentPane;

	public static final LangEditor ref = new LangEditor();
	private JTable table;
	private TableModel model = null;

	/**
	 * Create the frame.
	 */
	private LangEditor() {
		super("KPub – Language editor");
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JButton btnNewLang = new JButton("New");
		btnNewLang.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newLang();
			}
		});
		panel.add(btnNewLang);

		JButton btnDeleteLanguage = new JButton("Delete");
		panel.add(btnDeleteLanguage);
		btnDeleteLanguage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					try {
						LangModel.removeLang(LangModel.getLangById(model.getLangID(row)));
						updateTable();
					} catch (SQLException e1) {
						ErrorDialog.error(e1.getMessage(), ref);
					}
				}
			}
		});

		model = new TableModel();
		table = new JTable(model);
		
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Update table uses {@link LangModel#getModelStringArray()}
	 */
	public void updateTable() {
		model.updateData(LangModel.getModelStringArray());
	}

	private void newLang() {
		try {
			LangModel.newLang();
			this.updateTable();
		} catch (SQLException e) {
			ErrorDialog.error(e.getMessage(), this);
		}
	}

	private class TableModel extends AbstractTableModel implements TableModelListener {
		private static final long serialVersionUID = -1513726941424966020L;

		private String[] columnNames = { "LangID", "English name", "Polish name" };
		private Object[][] data = new Object[0][columnNames.length];

		private boolean changing = false;

		/**
		 * Makes new {@link TableModel}.
		 */
		public TableModel() {
			addTableModelListener(this);
		}

		@Override
		public String getColumnName(int index) {
			return columnNames[index + 1];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length - 1;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex + 1];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			data[rowIndex][columnIndex + 1] = aValue;
			fireTableCellUpdated(rowIndex, columnIndex);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (changing)
				return;
			int langID = Integer.valueOf((String) data[e.getFirstRow()][0]);
			Lang lang = LangModel.getLangById(langID);

			try {
				if (e.getColumn() == 0) {
					lang.setNameEN((String) data[e.getFirstRow()][1]);
				} else {
					lang.setNamePL((String) data[e.getFirstRow()][2]);
				}

			} catch (SQLException e1) {
				ErrorDialog.error(e1.getMessage(), LangEditor.this);
			}

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

		/**
		 * returns langID in showed n-th row
		 */
		public int getLangID(int n) {
			return Integer.valueOf((String) data[n][0]);
		}
	}
}
