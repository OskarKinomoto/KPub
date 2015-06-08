package me.kinomoto.kpub;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ConnectionErrorDialog extends JDialog {
	private static final long serialVersionUID = -378279921300270927L;
	private final JPanel contentPanel = new JPanel();
	

	private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 200;
	private static final int BORDER = 5;

	private static boolean result;

	/**
	 * Create the dialog.
	 */
	private ConnectionErrorDialog() {
		setTitle("Connection error");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"), }));

		JEditorPane message = new JEditorPane("text/html", "");
		message.setText("<center><h2>Can't connect to database</h2>\nTry again or change credentials");
		message.setEditable(false);
		message.setBackground(new Color(0, true));
		contentPanel.add(message, "2, 2, fill, fill");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton tryButton = new JButton("Try again");
		tryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				result = false;
				dispose();
			}
		});
		buttonPane.add(tryButton);

		JButton editButton = new JButton("Edit credentials");
		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				result = true;
				dispose();
			}
		});
		buttonPane.add(editButton);

		getRootPane().setDefaultButton(tryButton);
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(ABORT);
			}
		});
		buttonPane.add(exitButton);
	}

	public static boolean showDialog() {
		ConnectionErrorDialog dialog = new ConnectionErrorDialog();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);

		return result;
	}

}
