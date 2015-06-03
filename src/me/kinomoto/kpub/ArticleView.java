package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Settings panel for specific to Article fields
 */
public class ArticleView extends JPanel {
	private static final long serialVersionUID = 6423744893861714093L;

	/**
	 * Current year used as max in {@link #year JSpiner}.
	 */
	public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Current year used as minimum in {@link #year JSpiner}.
	 */
	public static final int MIN_YEAR = 1900;
	private static final int MARGIN_LEFT = 10;

	private JSpinner year;
	private JTextField articleNo;
	private JTextField doi;
	private JTextField url;

	private Article article;
	private JComboBox<String> issnNum;
	private JComboBox<String> issnName;
	private JSpinner issue;

	private boolean fired = false;

	private static ArticleView ref = null;

	/**
	 * variable used to prevent double opening IssnAdder
	 */
	private boolean adder = false;

	private JPanel issnPanel;

	/**
	 * Construct
	 */
	public ArticleView(Article article) {
		ref = this;
		this.article = article;
		setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:default:grow(8)"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, }));

		this.setBackground(new Color(0, true));

		initIssnPanel();
		initIssnNum();
		initIssnName();

		initYear();
		initIssue();
		initArticleNo();
		initDoiUrl();
		initLabels();

		initData();

	}

	private void initArticleNo() {
		articleNo = new JTextField();
		articleNo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setArticleNo(articleNo.getText());
			}

		});
		articleNo.setBackground(Color.WHITE);
		add(articleNo, "4, 8, fill, default");
	}

	private void initIssue() {
		issue = new JSpinner();
		issue.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				article.setIssue((int) issue.getValue());

			}
		});
		issue.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		add(issue, "4, 6, fill, default");
	}

	private void initIssnPanel() {
		issnPanel = new JPanel();
		add(issnPanel, "2, 2, 3, 1, fill, fill");
		issnPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("max(33dlu;default):grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:359px:grow(8)"), }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("24px"), }));
		issnPanel.setBackground(new Color(0, true));
	}

	private void initYear() {
		year = new JSpinner();
		year.setModel(new SpinnerNumberModel(CURRENT_YEAR, MIN_YEAR, CURRENT_YEAR, 1));
		year.setBackground(Color.WHITE);
		year.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				article.setYear((int) year.getValue());

			}
		});
		add(year, "4, 4, fill, default");
	}

	private void initDoiUrl() {
		doi = new JTextField();
		doi.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setDOI(doi.getText());

			}
		});
		doi.setBackground(Color.WHITE);
		add(doi, "4, 10, fill, default");
		doi.setColumns(10);

		url = new JTextField();
		url.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setURL(url.getText());
			}
		});
		url.setBackground(Color.WHITE);
		add(url, "4, 12, fill, default");
		url.setColumns(10);
	}

	private void initIssnName() {
		issnName = new JComboBox<String>();
		issnName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				issnUpdateByName();
			}
		});

		issnName.setModel(new DefaultComboBoxModel<String>(IssnModel.getIssnNames().toArray(new String[0])));
		issnName.setBackground(Color.WHITE);
		issnPanel.add(issnName, "3, 3, fill, top");
		issnName.setEditable(true);
		issnName.setEditor(new BasicComboBoxEditor() {

			@Override
			protected JTextField createEditorComponent() {
				JTextField tmp = super.createEditorComponent();
				tmp.setBorder(new EmptyBorder(0, MARGIN_LEFT, 0, 0));
				tmp.setBackground(Color.WHITE);
				return tmp;
			}

		});
	}

	private void initLabels() {

		JLabel issnLabel = new JLabel("ISSN");
		issnLabel.setFont(issnLabel.getFont().deriveFont(issnLabel.getFont().getStyle() | Font.BOLD));
		issnPanel.add(issnLabel, "1, 1, center, default");

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setFont(issnLabel.getFont().deriveFont(issnLabel.getFont().getStyle() | Font.BOLD));
		issnPanel.add(nameLabel, "3, 1, center, default");

		JLabel yearLabel = new JLabel("Year");
		yearLabel.setFont(yearLabel.getFont().deriveFont(yearLabel.getFont().getStyle() | Font.BOLD));
		add(yearLabel, "2, 4, right, default");

		JLabel issueLabel = new JLabel("Issue");
		issueLabel.setFont(issueLabel.getFont().deriveFont(issueLabel.getFont().getStyle() | Font.BOLD));
		add(issueLabel, "2, 6, right, default");

		JLabel articleNoLabel = new JLabel("Article");
		articleNoLabel.setFont(yearLabel.getFont().deriveFont(yearLabel.getFont().getStyle() | Font.BOLD));
		add(articleNoLabel, "2, 8, right, default");

		JLabel doiLabel = new JLabel("DOI");
		doiLabel.setFont(yearLabel.getFont().deriveFont(yearLabel.getFont().getStyle() | Font.BOLD));
		add(doiLabel, "2, 10, right, default");

		JLabel urlLabel = new JLabel("URL");
		urlLabel.setFont(urlLabel.getFont().deriveFont(urlLabel.getFont().getStyle() | Font.BOLD));
		add(urlLabel, "2, 12, right, default");
	}

	private void initIssnNum() {
		issnNum = new JComboBox<String>();
		issnNum.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				issnUpdateByNum();
			}
		});
		issnNum.setModel(new DefaultComboBoxModel<String>(IssnModel.getIssnsString().toArray(new String[0])));
		issnNum.setBackground(Color.WHITE);
		issnPanel.add(issnNum, "1, 3, fill, top");
		issnNum.setEditable(true);
		issnNum.setEditor(new BasicComboBoxEditor() {
			@Override
			protected JTextField createEditorComponent() {
				final IssnTextEditor t = new IssnTextEditor();
				t.setBorder(new EmptyBorder(0, MARGIN_LEFT, 0, 0));
				t.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							t.setText(article.getIssn().getIssnString());
						}
						super.keyReleased(e);
					}
				});
				return t;
			}
		});
	}

	/**
	 * Fired on {@link #issnName} change. Checks if {@link Issn} with {@link #issnName}.getSelectedItem() exists, if exists updates {@link #issnNum},
	 * if not shows {@link IssnAdder} window.
	 */
	private void issnUpdateByName() {
		if (fired)
			return;
		fired = true;
		Issn tmp = IssnModel.getIssnByName((String) issnName.getSelectedItem());
		if (tmp != null) {
			issnNum.setSelectedItem(tmp.getIssnString());
			article.setIssn(tmp);
		} else {
			if (!adder) {
				updateIssn();
				IssnAdder a = new IssnAdder("", (String) issnName.getSelectedItem(), this);
				a.setVisible(true);
			}
			adder = !adder;
		}
		fired = false;
	}

	/**
	 * Fired on {@link #issnNum} change. Checks if {@link Issn} with {@link #issnNum}.getSelectedItem() exists, if exists updates {@link #issnName},
	 * if not shows {@link IssnAdder} window.
	 */
	private void issnUpdateByNum() {
		if (fired)
			return;
		fired = true;
		Issn tmp = IssnModel.getIssnByIssn((String) issnNum.getSelectedItem());
		if (tmp != null) {
			issnName.setSelectedItem(tmp.getName());
			article.setIssn(tmp);
		} else {
			if (!adder) {
				IssnAdder a = new IssnAdder((String) issnNum.getSelectedItem(), "", this);
				updateIssn();
				a.setVisible(true);
			}
			adder = !adder;
		}
		fired = false;
	}

	private void initData() {
		fired = true;
		int a = (int) year.getValue();
		int b = article.getYear();
		if (a != b)
			year.setValue(article.getYear());
		else
			year.setValue(CURRENT_YEAR);
		articleNo.setText(article.getArticleNo());
		doi.setText(article.getDOI());
		url.setText(article.getUrl());
		issue.setValue(article.getIssue());
		updateIssn();
		fired = false;
	}

	/**
	 * @return if any field is non empty/zero
	 */
	public boolean hasDataInside() {
		if (issnNum.getSelectedItem() != "0000-0000" || !((String) issnName.getSelectedItem()).isEmpty())
			return true;
		if (!articleNo.getText().isEmpty() || !doi.getText().isEmpty() || !url.getText().isEmpty())
			return true;
		if ((int) year.getValue() != 0)
			return true;
		if ((int) issue.getValue() != 0)
			return true;
		return false;
	}

	/**
	 * 
	 * @return reference to {@link ArticleView ArticleView}
	 */
	public static ArticleView getRef() {
		return ref;
	}

	/**
	 * Updates article issn and fires {@link #updateIssn() updateIssn}
	 * 
	 * @param issn
	 *            - new issn
	 */
	public void updateIssn(Issn issn) {
		article.setIssn(issn);
		updateIssn();
	}

	/**
	 * Updates ComboBox models with data from {@link IssnModel IssnModel}. Sets selected item to article issn.
	 */
	public void updateIssn() {
		fired = true;
		issnNum.setModel(new DefaultComboBoxModel<String>(IssnModel.getIssnsString().toArray(new String[0])));
		issnName.setModel(new DefaultComboBoxModel<String>(IssnModel.getIssnNames().toArray(new String[0])));

		if (article.getIssn() == null) {
			issnNum.setSelectedItem("0000-0000");
			issnName.setSelectedItem("");
		} else {
			issnNum.setSelectedItem(article.getIssn().getIssnString());
			issnName.setSelectedItem(article.getIssn().getName());
		}
		fired = false;
	}

}
