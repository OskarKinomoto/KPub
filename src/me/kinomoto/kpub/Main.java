package me.kinomoto.kpub;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Base class for all application.
 */
public class Main extends JFrame {
	private static final long serialVersionUID = 1326086032805751936L;
	private static final int MIN_WIDTH = 700;
	private static final int MIN_HEIGHT = 630;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 630;
	private static final int LIST_WIDTH = 200;

	private DefaultListModel<Article> listModel = new DefaultListModel<Article>();

	private JList<Article> list;

	private Articles articles = new Articles(listModel);

	private ArticleMainView articleView = new ArticleMainView(articles);
	private final Action newArticleAction = new NewArticleAction();
	private final Action deleteArticleAction = new DeleteArticleAction();
	private final Action exportDataAction = new ExportAction();
	private JTextField title;
	private JTextField authors;

	private JComboBox<String> filterWorkType;

	private static Main mainRef = null;

	private JComboBox<String> fromYear;

	private JComboBox<String> toYear;

	private JPanel northContainer;
	private JButton issnEditor;
	private JButton langEditor;
	private JButton formatEditor;

	/**
	 * Makes first window and inits everything.
	 */
	public Main() {
		super("KPub");
		mainRef = this;

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			// nth to be done
		}

		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		initUI();
		initDB();
	}

	private void initDB() {
		try {
			OutputFormat.load();
		} catch (SQLException e) {
			ErrorDialog.error(e.getMessage(), mainRef);
		}
		articles.loadFromDB(list);
		articleView.updateLangs();
	}

	private void initNorthContainer() {
		northContainer = new JPanel();
		getContentPane().add(northContainer, BorderLayout.NORTH);
		northContainer.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, }));

		initButtons();
		initFilters();
	}

	private void initButtons() {
		JPanel toolBar = new JPanel();
		northContainer.add(toolBar, "1, 2, fill, fill");
		toolBar.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,  FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

		JButton btnNewArticle = new JButton("New article");
		toolBar.add(btnNewArticle, "1, 1, left, top");
		btnNewArticle.setAction(newArticleAction);

		JButton btnDeleteArticle = new JButton("Delete Article");
		toolBar.add(btnDeleteArticle, "3, 1");
		btnDeleteArticle.setAction(deleteArticleAction);

		JButton btnSaveToFile = new JButton("Save to file");
		toolBar.add(btnSaveToFile, "5, 1");
		btnSaveToFile.setAction(exportDataAction);

		issnEditor = new JButton("Issn Editor");
		toolBar.add(issnEditor, "7, 1");
		issnEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IssnEditor.getRef().setVisible(true);
			}
		});

		langEditor = new JButton("Lang Editor");
		toolBar.add(langEditor, "9, 1");
		langEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LangEditor.ref.setVisible(true);
			}
		});

		formatEditor = new JButton("Format Editor");
		toolBar.add(formatEditor, "11, 1");
		formatEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OutputFormatEditor.showEditor();
			}
		});
		
		JButton credEditor = new JButton("Cred");
		toolBar.add(credEditor, "13, 1");
		credEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CredentialsEditor.showDialog(true);
			}
		});
	}

	private void initFilters() {
		JPanel filtersPanel = new JPanel();
		northContainer.add(filtersPanel, "1, 4, fill, fill");
		filtersPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow(2)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow(2)"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		filterWorkType = new JComboBox<>();
		filterWorkType.setModel(new DefaultComboBoxModel<String>(new String[] { "All", "Article", "Poster", "Speach" }));
		filtersPanel.add(filterWorkType, "2, 2, fill, default");
		filterWorkType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Filters.setWorkType((String) filterWorkType.getSelectedItem());
				articles.updateList();
			}
		});

		title = new JTextField();
		title.setBackground(Color.WHITE);
		filtersPanel.add(title, "4, 2, fill, default");

		title.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					title.setText("");
				}
				Filters.setTitle(title.getText());
				articles.updateList();
				super.keyReleased(e);
			}

		});

		String[] years = new String[ArticleView.CURRENT_YEAR - ArticleView.MIN_YEAR + 2];
		years[0] = "none";
		for (int i = ArticleView.CURRENT_YEAR; i >= ArticleView.MIN_YEAR; --i) {
			years[ArticleView.CURRENT_YEAR - i + 1] = String.valueOf(i);
		}

		fromYear = new JComboBox<String>();
		fromYear.setModel(new DefaultComboBoxModel<String>(years));
		filtersPanel.add(fromYear, "6, 2, fill, default");
		fromYear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Filters.setYearStart((String) fromYear.getSelectedItem());
				articles.updateList();
			}
		});

		JLabel label = new JLabel("-");
		filtersPanel.add(label, "8, 2, right, default");

		toYear = new JComboBox<String>();
		toYear.setModel(new DefaultComboBoxModel<String>(years));
		filtersPanel.add(toYear, "10, 2, fill, default");
		toYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Filters.setYearStop((String) toYear.getSelectedItem());
				articles.updateList();
			}
		});

		authors = new JTextField();
		authors.setBackground(Color.WHITE);
		filtersPanel.add(authors, "12, 2, fill, default");

		authors.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					authors.setText("");
				}
				Filters.setAuthorsFromUnit(authors.getText());
				articles.updateList();
				super.keyReleased(e);
			}

		});
	}

	private void initUI() {
		getContentPane().setLayout(new BorderLayout());

		list = new JList<Article>(listModel);

		list.setPreferredSize(new Dimension(LIST_WIDTH, 0));
		list.setSelectedIndex(1);
		getContentPane().add(list, BorderLayout.WEST);

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					articleView.setArticle(list.getSelectedValue());
					Main.this.revalidate();
				}
			}
		});
		getContentPane().add(articleView, BorderLayout.CENTER);

		initNorthContainer();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Starts application and makes {@link Main#Main() Main()}.
	 */
	public static void main(String[] args) {
		startApp();
	}
	
	public static void startApp() {
		//check connection
		while(!Connection.testConnection())
			if(ConnectionErrorDialog.showDialog())
				CredentialsEditor.showDialog(false);
			
		@SuppressWarnings("unused")
		Main frame = new Main();
		
	}
	
	public static void restartApp() {
		mainRef.dispose();
		Connection.restart();
		startApp();
	}

	private class NewArticleAction extends AbstractAction {
		private static final long serialVersionUID = 3883769052975678937L;

		/**
		 * Creates {@link NewArticleAction}
		 */
		public NewArticleAction() {
			putValue(NAME, "New Article");
			putValue(SHORT_DESCRIPTION, "Adds new article");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Article tmp = articles.newArticle();
				list.setSelectedIndex(list.getModel().getSize() - 1);
				articleView.setArticle(tmp);
			} catch (SQLException e1) {
				Main.error(e1.getMessage());
			}
		}
	}

	private class DeleteArticleAction extends AbstractAction {
		private static final long serialVersionUID = -6128367701275073345L;

		/**
		 * Creates {@link DeleteArticleAction}
		 */
		public DeleteArticleAction() {
			putValue(NAME, "Delete Article");
			putValue(SHORT_DESCRIPTION, "Removes current article");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(null, "Do you realy want to delete selected article?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				articles.deleteArticle(list.getSelectedValue());
				list.setSelectedIndex(list.getModel().getSize() - 1);
				articleView.setArticle(list.getSelectedValue());
				Main.this.revalidate();
			}
		}
	}

	private class ExportAction extends AbstractAction {
		private static final long serialVersionUID = 6007516265222818209L;

		/**
		 * Creates {@link ExportAction}
		 */
		public ExportAction() {
			putValue(NAME, "Export data");
			putValue(SHORT_DESCRIPTION, "Export data to file");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
				try {
					articles.exportTo(chooser.getSelectedFile());
				} catch (IOException e1) {
					ErrorDialog.error(e1.getMessage(), mainRef);
				}
			}
		}
	}

	/**
	 * Opens {@link ErrorDialog}
	 * 
	 * @param msg
	 */
	public static void error(String msg) {
		ErrorDialog.error(msg, mainRef);
	}

}
