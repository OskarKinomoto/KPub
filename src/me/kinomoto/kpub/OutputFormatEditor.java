package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * {@link OutputFormat} variables editor
 */
public class OutputFormatEditor extends JFrame {
	private static final long serialVersionUID = -5042047489261585914L;
	private static final int MIN_WIDTH = 900;
	private static final int MIN_HEIGHT = 600;
	private static final int BORDER = 5;
	private static final int FONT_SIZE = 9;

	private JPanel contentPane;

	private static final OutputFormatEditor ref = new OutputFormatEditor();

	private JTextArea outputArea;
	private JTextArea posterArea;
	private JTextArea speechArea;
	private JTextArea articleArea;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	private JScrollPane scrollPane3;
	private JEditorPane txtrPosibleVariables;

	/**
	 * Create the frame.
	 */
	private OutputFormatEditor() {
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("min:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("100dlu"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("min:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("max(90dlu;min):grow(2)"), }));

		JLabel articleLabel = new JLabel("Article Output Format");
		contentPane.add(articleLabel, "2, 2, center, default");

		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "2, 4, fill, fill");

		articleArea = new JTextArea();
		scrollPane.setViewportView(articleArea);
		articleArea.setWrapStyleWord(true);
		articleArea.setLineWrap(true);

		articleArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				try {
					OutputFormat.setArticleFormat(articleArea.getText());
				} catch (SQLException e1) {
					ErrorDialog.error(e1.getMessage(), ref);
				}
			}
		});

		JLabel speechLabel = new JLabel("Speech Output Format");
		contentPane.add(speechLabel, "2, 6, center, default");

		txtrPosibleVariables = new JEditorPane("text/html", "");
		txtrPosibleVariables.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
		txtrPosibleVariables.setBackground(new Color(0, true));
		txtrPosibleVariables.setEditable(false);

		txtrPosibleVariables
				.setText("<b>All</b><br>%ISSN%<br> %ISSN_NAME%<br> %ARTICLE_NAME% %AUTHORS_FROM_UNIT% %AUTHORS_NOT_FROM_UNIT% %AUTHORS_NOT_FROM_UNIT_COUNT% %AUTHORS_FROM_UNIT_COUNT% <br> %LANG_PL%<br> %LANG_EN% <br> %TYP%<br><b>Article</b><br>%YEAR%<br> %ISSUE%<br> %ARTICLE_ID%<br> %DOI%<br> %URL%<br><b>Speech/Poster</b><br>%CONFERENCE_NAME%<br> %SPEECH_NAME%<br> %DATE%<br> %PLACE% <br><b>File</b><br>%ARTICLES_A%<br>%ARTICLES_B%<br>%ARTICLES_U%<br>%SPEECH_POSTER%<br>%SPEECH%<br>%POSTER%");
		contentPane.add(txtrPosibleVariables, "4, 4, 1, 13, fill, fill");

		scrollPane1 = new JScrollPane();
		contentPane.add(scrollPane1, "2, 8, fill, fill");

		speechArea = new JTextArea();
		scrollPane1.setViewportView(speechArea);
		speechArea.setWrapStyleWord(true);
		speechArea.setLineWrap(true);

		speechArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				try {
					OutputFormat.setSpeechFormat(speechArea.getText());
				} catch (SQLException e1) {
					ErrorDialog.error(e1.getMessage(), ref);
				}
			}
		});

		JLabel posterLabel = new JLabel("Poster Output Format");
		contentPane.add(posterLabel, "2, 10, center, default");

		scrollPane2 = new JScrollPane();
		contentPane.add(scrollPane2, "2, 12, fill, fill");

		posterArea = new JTextArea();
		scrollPane2.setViewportView(posterArea);
		posterArea.setWrapStyleWord(true);
		posterArea.setLineWrap(true);

		posterArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				try {
					OutputFormat.setPosterFormat(posterArea.getText());
				} catch (SQLException e1) {
					ErrorDialog.error(e1.getMessage(), ref);
				}
			}
		});

		JLabel outputLabel = new JLabel("File Output Format");
		contentPane.add(outputLabel, "2, 14, center, default");

		scrollPane3 = new JScrollPane();
		contentPane.add(scrollPane3, "2, 16, fill, fill");

		outputArea = new JTextArea();
		scrollPane3.setViewportView(outputArea);
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);

		outputArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				try {
					OutputFormat.setFileFormat(outputArea.getText());
				} catch (SQLException e1) {
					ErrorDialog.error(e1.getMessage(), ref);
				}
			}
		});
	}

	/**
	 * Updates fields from {@link OutputFormat}
	 */
	public static void update() {
		ref.outputArea.setText(OutputFormat.getFileFormat());
		ref.posterArea.setText(OutputFormat.getPosterFormat());
		ref.articleArea.setText(OutputFormat.getArticleFormat());
		ref.speechArea.setText(OutputFormat.getSpeechFormat());
	}

	/**
	 * {@link JFrame#setVisible() JFrame.setVisible(<code>true</code>)}
	 */
	public static void showEditor() {
		ref.setVisible(true);
	}

}
