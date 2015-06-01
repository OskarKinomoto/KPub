package me.kinomoto.kpub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * Stores and manages {@link Article Articles}
 */
public class Articles {
	public static final String SQL_LANG_ID = "LangID";
	public static final String SQL_LANG_EN = "NameEn";
	public static final String SQL_LANG_PL = "NamePl";
	public static final String SQL_AUTHORS_UNIT = "AuthorsFromUnit";
	public static final String SQL_AUTHORS_NOT_UNIT = "AuthorsNotFromUnit";
	public static final String SQL_AUTHORS_NOT_COUNT = "AuthorsNotFromUnitCount";
	public static final String SQL_ARTICLE_ID = "ArticleID";
	public static final String SQL_NAME = "Name";
	public static final String SQL_URL = "Url";
	public static final String SQL_DOI = "DOI";

	public static final String EMPTY = "brak\n";

	/**
	 * {@link ArrayList} with {@link Article Articles}
	 */
	private List<Article> articles = new ArrayList<Article>();

	/**
	 * List from {@link Main#list}
	 */
	private JList<Article> list;

	/**
	 * Model of {@link Main#list}
	 */
	private DefaultListModel<Article> listModel;

	/**
	 * @param listModel
	 */
	public Articles(DefaultListModel<Article> listModel) {
		this.listModel = listModel;
	}

	/**
	 * Load data from database.
	 * 
	 * @param list
	 */
	public void loadFromDB(JList<Article> list) {
		this.list = list;
		try {
			Connection.makeConnection();
			Statement st = Connection.getStatement();

			loadIssns(st);
			LangModel.loadLang(st);
			loadArticles(st);

			st.close();
			Connection.closeConnection();
		} catch (SQLException e) {
			Main.error(e.getMessage());
		}

	}

	/**
	 * Load {@link Issn Issns} from database
	 * 
	 * @throws SQLException
	 */
	private void loadIssns(Statement st) throws SQLException {
		IssnModel.loadIssns(st);
	}

	/**
	 * Load {@link Article Articles} from database
	 * 
	 * @throws SQLException
	 */
	private void loadArticles(Statement st) throws SQLException {
		ResultSet rs = st.executeQuery("SELECT * FROM Article ORDER BY " + SQL_NAME);

		while (rs.next()) {
			List<Author> authorsFromUnit = Author.getAuthors(rs.getString(SQL_AUTHORS_UNIT));
			List<Author> authorsNotFromUnit = Author.getAuthors(rs.getString(SQL_AUTHORS_NOT_UNIT));
			switch (rs.getString("Type")) {
			case "Article":
				articles.add(new Article(rs.getInt(SQL_ARTICLE_ID), rs.getString(SQL_NAME), authorsFromUnit, authorsNotFromUnit, rs.getInt(SQL_AUTHORS_NOT_COUNT), LangModel.getLangById(rs.getInt(SQL_LANG_ID)), IssnModel.getIssn(rs.getInt("ISSN")), rs.getInt("Year"), rs.getInt("Issue"), rs
						.getString("ArticleNo"), rs.getString(SQL_DOI), rs.getString(SQL_URL)));
				break;
			case "Poster":
			case "Speech":
				articles.add(new Article(rs.getInt(SQL_ARTICLE_ID), rs.getString("Type"), rs.getString(SQL_NAME), authorsFromUnit, authorsNotFromUnit, rs.getInt(SQL_AUTHORS_NOT_COUNT), LangModel.getLangById(rs.getInt(SQL_LANG_ID)), rs.getString("ConferenceName"), rs.getString("Date"), rs
						.getString("Town"), rs.getString("Country")));
				break;
			case "None":
				articles.add(new Article(rs.getInt(SQL_ARTICLE_ID), rs.getString(SQL_NAME), authorsFromUnit, authorsNotFromUnit, rs.getInt(SQL_AUTHORS_NOT_COUNT), LangModel.getLangById(rs.getInt(SQL_LANG_ID))));
				break;
			default:
				break;
			}
		}

		rs.close();

		for (Article articleAbstract : articles) {
			listModel.addElement(articleAbstract);
		}

	}

	/**
	 * @return first {@link Article} in {@link #articles} where {@link Article#getArticleID()} is equal to <code>id</code>
	 */
	public Article getArticleById(int id) {
		for (Article article : articles)
			if (article.getArticleID() == id)
				return article;
		return null;
	}

	/**
	 * Create and add to databae new {@link Article}
	 * 
	 * @return created new Article
	 * @throws SQLException
	 */
	public Article newArticle() throws SQLException {
		Article tmp = new Article();
		articles.add(tmp);
		listModel.addElement(tmp);
		return tmp;
	}

	/**
	 * Updates Model and JList with {@link #articles}
	 */
	public void updateList() {
		Collections.sort(articles, new Comparator<Article>() {
			@Override
			public int compare(Article a1, Article a2) {
				return a1.getName().compareTo(a2.getName());
			}
		});

		List<Article> listed = new ArrayList<Article>();
		Article selected = list.getSelectedValue();
		listModel.removeAllElements();
		for (Article article : articles) {
			if (Filters.passesFilters(article)) {
				listModel.addElement(article);
				listed.add(article);
			}

		}
		if (listed.contains(selected))
			list.setSelectedValue(selected, true);
		else
			ArticleMainView.getRef().setArticle(null);
	}

	/**
	 * Deletes <b>article</b> from database and {@link #articles}. Than fires {@link #updateList}.
	 */
	public void deleteArticle(Article article) {
		try {
			Connection.makeConnection();
			Statement st = Connection.getStatement();
			st.execute("DELETE FROM Article WHERE ArticleID = " + article.getArticleID());
			articles.remove(article);
			Connection.closeConnection();
		} catch (Exception e) {
			Main.error(e.getMessage());
		}
		updateList();
	}

	/**
	 * Exports data to file. If {@link File#getAbsolutePath()} conatins ".docx" saves as docx file otherwise as simple text file.
	 * 
	 * @throws IOException
	 */
	public void exportTo(File file) throws IOException {

		if (!file.exists()) {
			file.createNewFile();
		}

		String articleA = "";
		String articleB = "";
		String articleU = "";

		String poster = "";
		String speech = "";
		String speechPoster = "";

		for (Article article : articles) {
			if (article.getType() == null)
				continue;

			switch (article.getType()) {
			case ARTICLE:
				if (article.getIssn() == null)
					continue;
				switch (article.getIssn().getType()) {
				case LIST_A:
					articleA += article.getOutput(OutputFormat.getArticleFormat()) + "\n";
					break;
				case LIST_B:
					articleB += article.getOutput(OutputFormat.getArticleFormat()) + "\n";
					break;
				case UNLISTED:
					articleU += article.getOutput(OutputFormat.getArticleFormat()) + "\n";
					break;
				default:
					break;
				}
				break;
			case POSTER:
				poster += article.getOutput(OutputFormat.getPosterFormat()) + "\n";
				speechPoster += article.getOutput(OutputFormat.getPosterFormat()) + "\n";
				break;
			case SPEECH:
				speech += article.getOutput(OutputFormat.getSpeechFormat()) + "\n";
				speechPoster += article.getOutput(OutputFormat.getSpeechFormat()) + "\n";
				break;
			default:
				break;
			}
		}
		if (articleA.isEmpty())
			articleA = EMPTY;
		if (articleB.isEmpty())
			articleB = EMPTY;
		if (articleU.isEmpty())
			articleU = EMPTY;
		String out = OutputFormat.getFileFormat();
		out = out.replaceAll("%ARTICLES_A%", articleA);
		out = out.replaceAll("%ARTICLES_B%", articleB);
		out = out.replaceAll("%ARTICLES_U%", articleU);
		out = out.replaceAll("%SPEECH_POSTER%", speechPoster);
		out = out.replaceAll("%POSTER%", poster);
		out = out.replaceAll("%SPEECH%", speech);

		if (!file.getPath().contains(".docx")) {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			fw.write(out);
			fw.close();

		} else {
			WordprocessingMLPackage wordMLPackage;
			try {
				wordMLPackage = WordprocessingMLPackage.createPackage();
				wordMLPackage.getMainDocumentPart().addParagraphOfText(out);
				wordMLPackage.save(new File("/home/oskar/test.docx"));
			} catch (InvalidFormatException e) {
				Main.error(e.getMessage());
			} catch (Docx4JException e) {
				Main.error(e.getMessage());
			}
		}
	}

}
