package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.kinomoto.kpub.Issn.ChecksumException;
import me.kinomoto.kpub.Issn.EmptyNameException;
import me.kinomoto.kpub.Issn.IssnLenghtException;
import me.kinomoto.kpub.Issn.IssnZeroException;
import me.kinomoto.kpub.Issn.WrongTypeException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Form for creating new Issn.
 */
public class IssnAdder extends JFrame {
	private static final long serialVersionUID = -4763664506172905753L;
	private static final int BORDER = 10;
	private static final int WIDTH = 450;
	private static final int HEIGHT = 300;

	private JPanel contentPane;
	private JTextField name;
	private JComboBox<String> typeEditor;
	private JFormattedTextField issnEditor;

	private ArticleView articleView;

	/**
	 * Make empty form.
	 */
	public IssnAdder() {
		super("Add issn to database");
		initUI();
	}

	/**
	 * Make window from {@link ArticleView} with some know parameters.
	 * 
	 * @param issn
	 * @param name
	 * @param articleView
	 */
	public IssnAdder(String issn, String name, ArticleView articleView) {
		super("Add issn to database");
		this.articleView = articleView;
		initUI();
		if (!name.isEmpty())
			this.name.setText(name);
		if (!issn.isEmpty()) {
			issnEditor.setText(issn);
			issnEditor.select(0, 0);
		}

	}

	private void initUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"), FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblAddNewIssn = new JLabel("Add new ISSN");
		contentPane.add(lblAddNewIssn, "2, 2, 3, 1, center, default");

		JLabel lblIssn = new JLabel("ISSN");
		contentPane.add(lblIssn, "2, 4, right, default");

		issnEditor = new IssnTextEditor();
		issnEditor.setText("0000-0000");
		issnEditor.selectAll();
		contentPane.add(issnEditor, "4, 4, fill, default");

		JLabel lblName = new JLabel("Name");
		contentPane.add(lblName, "2, 6, right, default");

		name = new JTextField();
		name.setBackground(Color.WHITE);
		contentPane.add(name, "4, 6, fill, default");

		JLabel lblList = new JLabel("MNiSW List");
		contentPane.add(lblList, "2, 8, right, default");

		typeEditor = new JComboBox<String>();
		typeEditor = new JComboBox<String>(Issn.getIssnTypesArray());
		contentPane.add(typeEditor, "4, 8, fill, default");

		JButton btnSave = new JButton("Save");
		contentPane.add(btnSave, "4, 12");
		btnSave.addActionListener(new SaveActionListener());
	}

	private class SaveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Issn issn = IssnModel.newIssn(issnEditor.getText(), name.getText(), (String) typeEditor.getSelectedItem());
				if (articleView != null)
					articleView.updateIssn(issn);
				IssnAdder.this.dispose();
			} catch (ChecksumException e1) {
				ErrorDialog.error("Issn check sum incorect", IssnAdder.this);
			} catch (EmptyNameException e1) {
				ErrorDialog.error("Name can not be empty", IssnAdder.this);
			} catch (WrongTypeException e1) {
				ErrorDialog.error("Choose corect list type.", IssnAdder.this);
			} catch (SQLException e1) {
				ErrorDialog.error(e1.getMessage(), IssnAdder.this);
			} catch (IssnZeroException e1) {
				ErrorDialog.error("Issn can not be 0000-0000.", IssnAdder.this);
			} catch (IssnLenghtException e1) {
				ErrorDialog.error("Issn must be in 1234-567X format.", IssnAdder.this);
			}
		}
	}

}
