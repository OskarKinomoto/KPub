package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import me.kinomoto.kpub.Article.WORK_TYPE;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/**
 * JPanel with form for common fields in {@link Article}
 */
public class ArticleMainView extends JPanel {
	private static final long serialVersionUID = -5596026179693560183L;

	private static final int AUTHOR_COUNT_WIDTH = 100;
	private static final int AUTHOR_COUNT_HEIGHT = 20;

	private static final String AUTHORS_FORM_STRING = "Authors from unit";
	private static final String AUTHORS_NOT_FORM_STRING = "Authors not from unit";

	/**
	 * Current {@link Article}
	 */
	private Article article = null;

	/**
	 * Used to stop change listeners
	 */
	private boolean changingArticle = false;

	private JComboBox<String> workType;
	private String lastWorkType;
	private JTextArea authorsNotFromUnitArea;
	private JTextArea authorsFromUnitArea;
	private JTextArea nameArea;
	private JLabel countOfAuthorsLabel;
	private JPanel authorsNotFromUnitCountPanel;
	private JSpinner authorsCount;
	private JLabel autoLabel;
	private JPanel workLangPanel;
	private JComboBox<String> langSelect;

	private JLabel lblAuthorsNotFrom;
	private JLabel lblAuthorsFromUnit;

	/**
	 * Variable for {@link ArticleMainView} or {@link SpeechPosterView}
	 */
	private JPanel customPanel;

	/**
	 * Used to fire {@link Articles#updateList()}
	 */
	private Articles articles;

	/**
	 * Used to update issn chombobox
	 */
	private static ArticleMainView ref = null;

	/**
	 * Construct {@link ArticleMainView}
	 */
	public ArticleMainView(Articles articles) {
		ref = this;
		this.articles = articles;
		setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.GLUE_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("30dlu", false), Sizes.constant("30dlu", false)), 0), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("30dlu", false), Sizes.constant("30dlu", false)), 0), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("30dlu", false), Sizes.constant("30dlu", false)), 0), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		initWorkLangPanel();
		initLang();
		intWorkType();
		initNameArea();
		initAuthorsFromUnit();
		initAuthorsNotFromUnit();
		initAuthorsNotFromUnitCount();
		initLabels();

		setVisible(false);
	}

	private void initLabels() {

		JLabel lblName = new JLabel("Name");
		add(lblName, "4, 4, center, default");

		lblAuthorsFromUnit = new JLabel(AUTHORS_FORM_STRING);
		add(lblAuthorsFromUnit, "4, 8, center, default");

		lblAuthorsNotFrom = new JLabel(AUTHORS_NOT_FORM_STRING);
		add(lblAuthorsNotFrom, "4, 12, center, default");

		autoLabel = new JLabel("0 - auto");
		authorsNotFromUnitCountPanel.add(autoLabel, "4, 1, right, center");

	}

	private void initAuthorsNotFromUnitCount() {
		authorsNotFromUnitCountPanel = new JPanel();
		add(authorsNotFromUnitCountPanel, "4, 16, fill, fill");
		authorsNotFromUnitCountPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("min:grow"), ColumnSpec.decode("right:54px"), }, new RowSpec[] { RowSpec.decode("20px"), }));

		countOfAuthorsLabel = new JLabel("Count of authors not from unit");
		authorsNotFromUnitCountPanel.add(countOfAuthorsLabel, "1, 1");

		authorsCount = new JSpinner();
		authorsCount.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (changingArticle)
					return;
				try {
					article.setAuthorsCount((int) authorsCount.getValue());
					updateAuthorsCount();
				} catch (SQLException e1) {
					Main.error(e1.getMessage());
				}
			}
		});
		authorsCount.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		authorsCount.setMinimumSize(new Dimension(AUTHOR_COUNT_WIDTH, AUTHOR_COUNT_HEIGHT));
		authorsNotFromUnitCountPanel.add(authorsCount, "3, 1, center, center");
	}

	private void initAuthorsNotFromUnit() {
		authorsNotFromUnitArea = new JTextArea();
		authorsNotFromUnitArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (changingArticle)
					return;
				try {
					article.setAuthorsNotFromUnit(authorsNotFromUnitArea.getText());
					updateAuthorsCount();
				} catch (SQLException e1) {
					Main.error(e1.getMessage());
				}
			}
		});
		authorsNotFromUnitArea.setToolTipText("Comma is separator");
		authorsNotFromUnitArea.setWrapStyleWord(true);
		authorsNotFromUnitArea.setLineWrap(true);
		add(authorsNotFromUnitArea, "4, 14, fill, fill");
	}

	private void initAuthorsFromUnit() {
		authorsFromUnitArea = new JTextArea();
		authorsFromUnitArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (changingArticle)
					return;
				try {
					article.setAuthorsFromUnit(authorsFromUnitArea.getText());
					updateAuthorsCount();
				} catch (SQLException e1) {
					Main.error(e1.getMessage());
				}
			}
		});
		authorsFromUnitArea.setToolTipText("Comma is separator");
		authorsFromUnitArea.setWrapStyleWord(true);
		authorsFromUnitArea.setLineWrap(true);
		add(authorsFromUnitArea, "4, 10, fill, fill");
	}

	private void initNameArea() {

		nameArea = new JTextArea();
		nameArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (changingArticle)
					return;
				try {
					article.setName(nameArea.getText());
					articles.updateList();
				} catch (SQLException e1) {
					Main.error(e1.getMessage());
				}
			}
		});
		nameArea.setWrapStyleWord(true);
		nameArea.setLineWrap(true);
		add(nameArea, "4, 6, fill, fill");

	}

	private void intWorkType() {
		workType = new JComboBox<String>();
		workType.setBackground(Color.WHITE);
		workLangPanel.add(workType, "1, 1, fill, top");
		String[] a = new String[WORK_TYPE.values().length + 1];
		a[0] = "";
		int i = 1;
		for (WORK_TYPE type : WORK_TYPE.values()) {
			String tmp = type.toString().toLowerCase();
			a[i++] = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
		}
		workType.setModel(new DefaultComboBoxModel<String>(a));
		workType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (changingArticle)
					return;
				if ((lastWorkType == "Article" && ((ArticleView) customPanel).hasDataInside() && JOptionPane.showConfirmDialog(null, "Some data will be destroyed, do you want to continue?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
						|| workType.getSelectedItem().toString().isEmpty()) {
					workType.setSelectedItem(lastWorkType);
					return;
				}
				try {
					article.setWorkType(WORK_TYPE.valueOf(workType.getSelectedItem().toString().toUpperCase()));
					updateCustomPanel();
					lastWorkType = workType.getSelectedItem().toString();
				} catch (SQLException e) {
					Main.error(e.getMessage());
				}

			}
		});

	}

	private void initLang() {
		langSelect = new JComboBox<>();
		langSelect.setBackground(Color.WHITE);
		langSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (changingArticle)
					return;
				try {
					article.setLang(LangModel.getLangByNameEN((String) langSelect.getSelectedItem()));
				} catch (SQLException e) {
					Main.error(e.getMessage());
				} catch (NullPointerException e) {
					// nth to be done
				}
			}
		});

		workLangPanel.add(langSelect, "3, 1, fill, default");
	}

	private void initWorkLangPanel() {
		workLangPanel = new JPanel();
		add(workLangPanel, "4, 2, fill, fill");
		workLangPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(150px;default)"), }, new RowSpec[] { RowSpec.decode("24px"), }));
	}

	/**
	 * Sets <b>selectedArticle</b> as {@link #article} and updates data in form.
	 */
	public void setArticle(Article selectedArticle) {
		article = selectedArticle;
		if (article == null) {
			setVisible(false);
			return;
		}
		changingArticle = true;
		setVisible(true);
		workType.setSelectedItem(article.getTypeString());
		lastWorkType = article.getTypeString();
		nameArea.setText(article.getName());
		authorsFromUnitArea.setText(article.getAuthorsFromUnitString());
		authorsNotFromUnitArea.setText(article.getAuthorsNotFromUnitString());
		authorsCount.setValue(article.getAuthorsNotFromUnitCountNonCalc());
		if (article.getLang() != null)
			langSelect.setSelectedItem(article.getLang().getNameEN());
		else {
			langSelect.setSelectedItem("");
		}
		updateAuthorsCount();
		updateCustomPanel();
		changingArticle = false;
	}

	private void updateAuthorsCount() {
		lblAuthorsNotFrom.setText(AUTHORS_NOT_FORM_STRING + " [" + article.getAuthorsNotFromUnitCount() + "]");
		lblAuthorsFromUnit.setText(AUTHORS_FORM_STRING + " [" + article.getAuthorsFromUnitCount() + "]");
	}

	/**
	 * Set new {@link #customPanel}
	 */
	private void updateCustomPanel() {
		if (customPanel != null)
			remove(customPanel);
		customPanel = null;
		if (article.getType() == WORK_TYPE.ARTICLE) {
			customPanel = new ArticleView(article);
			add(customPanel, "4, 22, fill, fill");
		} else if (article.getType() != null) {
			customPanel = new SpeechPosterView(article);
			add(customPanel, "4, 22, fill, fill");
		}
		revalidate();
		repaint();
	}

	/**
	 * Updates {@link #langSelect} model using data from {@link LangModel LangModel}
	 */
	public void updateLangs() {
		langSelect.setModel(new DefaultComboBoxModel<String>((String[]) LangModel.getLangsList().toArray(new String[0])));
		if (article != null)
			if (article.getLang() != null) {
				String tmp = article.getLang().getNameEN();
				langSelect.setSelectedItem(tmp);
			} else {
				langSelect.setSelectedItem("");
			}
	}

	/**
	 * @return reference to itself
	 */
	public static ArticleMainView getRef() {
		return ref;
	}
}
