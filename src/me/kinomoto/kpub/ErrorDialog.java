package me.kinomoto.kpub;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * All error messages goes thru this class.
 */
public class ErrorDialog extends JDialog {
	private static final long serialVersionUID = 8669773446077998026L;

	private static final int BORDER = 10;
	private static final int WIDTH = 450;
	private static final int HEIGHT = 300;
	private static final int OK_WIDTH = 100;
	private static final int OK_HEIGHT = 50;

	private final JPanel contentPanel = new JPanel();

	private JTextPane message;

	private StyledDocument doc;

	private SimpleAttributeSet center;

	private JButton okButton;

	/**
	 * Create the dialog.
	 */
	private ErrorDialog(JFrame m) {
		super(m);
		setResizable(false);
		setTitle("Error");
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		contentPanel.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		contentPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		contentPanel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		contentPanel.setSize(WIDTH, HEIGHT);
		getContentPane().add(contentPanel, BorderLayout.WEST);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:min:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"), }));

		JLabel label = new JLabel(":(");
		contentPanel.add(label, "2, 2, center, default");

		JLabel lblError = new JLabel("Error");
		contentPanel.add(lblError, "2, 4, center, default");

		message = new JTextPane();
		message.setEditable(false);
		message.setText("Lorem Ipsum Dolor est amet");
		message.setBackground(new Color(0, true));
		contentPanel.add(message, "2, 6, fill, fill");

		doc = message.getStyledDocument();
		center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("min:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("25px"), }));

		okButton = new JButton("OK");
		okButton.setMinimumSize(new Dimension(OK_WIDTH, OK_HEIGHT));
		okButton.setMnemonic('O');
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ErrorDialog.this.dispose();
			}
		});
		buttonPane.add(okButton, "1, 2, center, top");
		getRootPane().setDefaultButton(okButton);
	}

	private void setMessage(String msg) {
		message.setText(msg);
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}

	/**
	 * Make error dialog with <b>msg</b>.
	 * 
	 * @param msg
	 * @param ref
	 */
	public static void error(String msg, JFrame ref) {
		ErrorDialog dial = new ErrorDialog(ref);
		dial.setMessage(msg);
		dial.setVisible(true);
		dial.okButton.requestFocusInWindow();
	}
}
