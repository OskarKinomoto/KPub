package me.kinomoto.kpub;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JButton;

public class CredentialsEditor extends JDialog {
	private static final long serialVersionUID = -2413682226242006619L;

	private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 200;
	private static final int BORDER = 5;

	private JPanel contentPane;
	private JTextField name;
	private JTextField pass;
	private JTextField db;
	private JTextField host;
	private JButton btnClose;

	private static boolean changed = false;

	/**
	 * Create the frame.
	 */
	private CredentialsEditor() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblUserName = new JLabel("User name");
		contentPane.add(lblUserName, "2, 2, right, default");

		name = new JTextField(Connection.getName());
		contentPane.add(name, "4, 2, fill, default");
		name.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyTyped(e);
				changed = true;
				Connection.setName(name.getText());
			}
		});

		JLabel lblUserPass = new JLabel("User pass");
		contentPane.add(lblUserPass, "2, 4, right, default");

		pass = new JTextField(Connection.getPass());
		contentPane.add(pass, "4, 4, fill, default");
		pass.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyTyped(e);
				changed = true;
				Connection.setPass(pass.getText());
			}
		});

		JLabel lblDatabaseName = new JLabel("Database name");
		contentPane.add(lblDatabaseName, "2, 6, right, default");

		db = new JTextField(Connection.getDB());
		contentPane.add(db, "4, 6, fill, default");
		db.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyTyped(e);
				changed = true;
				Connection.setDB(db.getText());
			}
		});		

		JLabel lblHostLocation = new JLabel("Host location");
		contentPane.add(lblHostLocation, "2, 8, right, default");

		host = new JTextField(Connection.getHost());
		contentPane.add(host, "4, 8, fill, default");
		host.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyTyped(e);
				changed = true;
				Connection.setHost(host.getText());
			}
		});

		btnClose = new JButton("Close");
		contentPane.add(btnClose, "2, 10, 3, 1, fill, default");
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}

	public static void showDialog(boolean restart) {
		changed = false;
		CredentialsEditor editor = new CredentialsEditor();
		editor.setModalityType(ModalityType.APPLICATION_MODAL);
		editor.setVisible(true);
		if (restart && changed) {
			Main.restartApp();
		}
	}

}
