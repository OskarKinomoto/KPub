package me.kinomoto.kpub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores data from database about article, poster or speech.
 */
public class Article {
	/**
	 * Type of work
	 */
	public enum WORK_TYPE {
		ARTICLE, POSTER, SPEECH
	}

	/**
	 * Database unique key
	 */
	private int articleID;

	/**
	 * Type of work
	 */
	private WORK_TYPE type;

	/**
	 * 'Pointer' to {@link Lang} from {@link LangModel}
	 */
	private Lang lang;

	/**
	 * Article/Poster/Speech title
	 */
	private String name;

	/**
	 * {@link ArrayList} of {@link Author} for authors from unit
	 */
	private List<Author> authorsFromUnit;

	/**
	 * {@link ArrayList} of {@link Author} for authors not from unit
	 */
	private List<Author> authorsNotFromUnit;

	/**
	 * Count of authors not from unit. If set to 0 count will be automatically calculated.
	 */
	private int authorsNotFromUnitCount;

	/**
	 * 'Pointer' to {@link Issn}, ARTICLE only
	 */
	private Issn issn;

	/**
	 * Publish year, ARTICLE only
	 */
	private int year;

	/**
	 * Issue journal number, ARTICLE only
	 */
	private int issue;

	/**
	 * Article number/pages, ARTICLE only
	 */
	private String articleNo;

	/**
	 * @see <a href="https://en.wikipedia.org/wiki/Digital_object_identifier">Wikipedia</a>
	 */
	private String doi;

	/**
	 * URL to {@link #doi} page.
	 */
	private String url;

	/**
	 * Conference name
	 */
	private String conferenceName;
	/**
	 * Date of conference
	 */
	private String date;
	/**
	 * Town, where conference was
	 */
	private String town;
	/**
	 * Country, where conference was
	 */
	private String country;

	/**
	 * Makes new article in database with {@link #type} = {@link NullPointerException} and {@link #name} = "New"
	 * 
	 * @throws SQLException
	 */
	public Article() throws SQLException {
		type = null;
		authorsFromUnit = new ArrayList<>();
		authorsNotFromUnit = new ArrayList<>();
		name = "New";

		Connection.makeConnection();
		Statement st = Connection.getStatement();
		st.executeUpdate("INSERT INTO Article (ArticleID, Name) VALUES (NULL, 'New')", Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = st.getGeneratedKeys();
		rs.next();
		articleID = rs.getInt(1);
		rs.close();
		st.close();
		Connection.closeConnection();
	}

	/**
	 * Constructs {@link Article} ARTICLE
	 */
	public Article(int articleID, String name, List<Author> authorsFromUnit, List<Author> authorsNotFromUnit, int authorsNotFromUnitCount, Lang lang, Issn issn, int year, int issue, String articleNo, String doi, String url) {
		this.articleID = articleID;
		this.type = WORK_TYPE.ARTICLE;
		this.name = name;
		this.authorsFromUnit = authorsFromUnit;
		this.authorsNotFromUnit = authorsNotFromUnit;
		this.authorsNotFromUnitCount = authorsNotFromUnitCount;
		this.lang = lang;
		this.issn = issn;
		this.year = year;
		this.issue = issue;
		this.articleNo = articleNo;
		this.doi = doi;
		this.url = url;
	}

	/**
	 * Constructs {@link Article} POSTER/SPEECH
	 */
	public Article(int articleID, String type, String name, List<Author> authorsFromUnit, List<Author> authorsNotFromUnit, int authorsNotFromUnitCount, Lang lang, String conferenceName, String date, String town, String country) {
		this.articleID = articleID;
		this.type = WORK_TYPE.valueOf(type.toUpperCase());
		this.name = name;
		this.authorsFromUnit = authorsFromUnit;
		this.authorsNotFromUnit = authorsNotFromUnit;
		this.authorsNotFromUnitCount = authorsNotFromUnitCount;
		this.lang = lang;
		this.conferenceName = conferenceName;
		this.date = date;
		this.town = town;
		this.country = country;
	}

	/**
	 * Constructs {@link Article} <code>null</code> {@link #type}
	 */
	public Article(int articleID, String name, List<Author> authorsFromUnit, List<Author> authorsNotFromUnit, int authorsNotFromUnitCount, Lang lang) {
		this.articleID = articleID;
		this.type = null;
		this.name = name;
		this.authorsFromUnit = authorsFromUnit;
		this.authorsNotFromUnit = authorsNotFromUnit;
		this.authorsNotFromUnitCount = authorsNotFromUnitCount;
		this.lang = lang;
	}

	/**
	 * @return {@link #articleID}
	 */
	public int getArticleID() {
		return articleID;
	}

	/**
	 * @return {@link #type}
	 */
	public WORK_TYPE getType() {
		return type;
	}

	/**
	 * @return "Article", "Speech" or "Poster"
	 */
	public String getTypeString() {
		if (type == null)
			return "";
		String t = type.toString().toLowerCase();
		return t.substring(0, 1).toUpperCase() + t.substring(1);
	}

	/**
	 * @return {@link #name} or "NO NAME" if empty
	 */
	public String getName() {
		if (name.isEmpty())
			return "NO NAME";
		return name;
	}

	/**
	 * @return {@link #authorsFromUnit}
	 */
	public List<Author> getAuthorsFromUnit() {
		return authorsFromUnit;
	}

	/**
	 * @return {@link #authorsNotFromUnit}
	 */
	public List<Author> getAuthorsNotFromUnit() {
		return authorsNotFromUnit;
	}

	/**
	 * @return {@link String} from {@link #authorsFromUnit} uses comma as separator
	 */
	public String getAuthorsFromUnitString() {
		String tmp = new String();
		for (Author author : authorsFromUnit) {
			tmp += author.getName() + ", ";
		}
		if (tmp.length() > 1)
			tmp = tmp.substring(0, tmp.length() - 2);
		return tmp;
	}

	/**
	 * @return {@link String} from {@link #authorsNotFromUnit} uses comma as separator
	 */
	public String getAuthorsNotFromUnitString() {
		String tmp = new String();
		for (Author author : authorsNotFromUnit) {
			tmp += author.getName() + ", ";
		}
		if (tmp.length() > 1)
			tmp = tmp.substring(0, tmp.length() - 2);
		return tmp;
	}

	/**
	 * @return {@link #authorsNotFromUnitCount} or if it was equal to zero {@link #authorsNotFromUnit}.size()
	 */
	public int getAuthorsNotFromUnitCount() {
		if (authorsNotFromUnitCount == 0)
			return authorsNotFromUnit.size();
		return authorsNotFromUnitCount;
	}

	/**
	 * @return {@link #authorsFromUnit}.size()
	 */
	public int getAuthorsFromUnitCount() {
		return authorsFromUnit.size();
	}

	/**
	 * @return {@link #lang}
	 */
	public Lang getLang() {
		return lang;
	}

	/**
	 * Update database and {@link #type} uses {@link WORK_TYPE#valueOf(String)}
	 * 
	 * @throws SQLException
	 */
	public void setWorkType(String type) {
		setWorkType(WORK_TYPE.valueOf(type));
	}

	/**
	 * Update database and {@link #type}
	 * 
	 * @throws SQLException
	 */
	public void setWorkType(final WORK_TYPE type) {
		if (type != this.type) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Type = ? WHERE ArticleID = " + articleID);
						st.setString(1, type.toString());
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.type = type;
		}
	}

	/**
	 * Update database and {@link #name}
	 * 
	 * @throws SQLException
	 */
	public void setName(final String name) {
		if (this.name != name) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Name = ? WHERE ArticleId = " + articleID);
						st.setString(1, name);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.name = name;
		}
	}

	/**
	 * Update database and {@link #authorsFromUnit} uses {@link Author#getAuthors(String)}
	 * 
	 * @throws SQLException
	 */
	public void setAuthorsFromUnit(final String authors) {
		if (getAuthorsFromUnitString() != authors) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET AuthorsFromUnit = ? WHERE ArticleId = " + articleID);
						st.setString(1, authors);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.authorsFromUnit = Author.getAuthors(authors);
		}
	}

	/**
	 * Update database and {@link #authorsNotFromUnit} uses {@link Author#getAuthors(String)}
	 * 
	 * @throws SQLException
	 */
	public void setAuthorsNotFromUnit(final String authors) {
		if (getAuthorsNotFromUnitString() != authors) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET AuthorsNotFromUnit = ? WHERE ArticleId = " + articleID);
						st.setString(1, authors);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}

				}
			});

			Article.this.authorsNotFromUnit = Author.getAuthors(authors);
		}

	}

	/**
	 * Update database and {@link #authorsNotFromUnit}
	 * 
	 * @throws SQLException
	 */
	public void setAuthorsCount(final int count) {
		if (this.authorsNotFromUnitCount != count) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET AuthorsNotFromUnitCount = ? WHERE ArticleId = " + articleID);
						st.setInt(1, count);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			authorsNotFromUnitCount = count;
		}
	}

	/**
	 * Update database and {@link #lang}
	 * 
	 * @throws SQLException
	 */
	public void setLang(final Lang lang) {
		if (this.lang != lang) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET LangID = ? WHERE ArticleId = " + articleID);
						if (lang == null)
							st.setInt(1, 0);
						else
							st.setInt(1, lang.getLangID());
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.lang = lang;
		}
	}

	/**
	 * @return {@link #issn}
	 */
	public Issn getIssn() {
		return issn;
	}

	/**
	 * @return {@link #year} if {@link #type} equals {@link WORK_TYPE#ARTICLE} or first founded 4 digit number in {@link #date}.
	 */
	public int getYear() {
		if (type == WORK_TYPE.ARTICLE)
			return year;
		else {

			if (date == null || date.isEmpty())
				return 0;
			Matcher m = Pattern.compile("[1-2][0-9][0-9][0-9]").matcher(date);
			if (m.find()) {
				return Integer.valueOf(date.substring(m.start(), m.end()));
			}
			return 0;
		}

	}

	/**
	 * @return {@link #issue}
	 */
	public int getIssue() {
		return issue;
	}

	/**
	 * @return {@link #articleNo}
	 */
	public String getArticleNo() {
		return articleNo;
	}

	/**
	 * @return {@link #doi}
	 */
	public String getDOI() {
		return doi;
	}

	/**
	 * @return {@link #url}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Update database and {@link #year}
	 * 
	 * @throws SQLException
	 */
	public void setYear(final int year) {
		if (this.year != year) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Year = ? WHERE ArticleId = " + articleID);
						st.setInt(1, year);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.year = year;
		}

	}

	/**
	 * Update database and {@link #issue}
	 * 
	 * @throws SQLException
	 */
	public void setIssue(final int issue) {
		if (this.issue != issue) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Issue = ? WHERE ArticleId = " + articleID);
						st.setInt(1, issue);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.issue = issue;
		}

	}

	/**
	 * Updata database and {@link #articleNo}
	 * 
	 * @param articleNo
	 * @throws SQLException
	 */
	public void setArticleNo(final String articleNo) {
		if (this.name != articleNo) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET ArticleNo = ? WHERE ArticleId = " + articleID);
						st.setString(1, articleNo);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.articleNo = articleNo;
		}
	}

	/**
	 * Update database and {@link #doi}
	 * 
	 * @throws SQLException
	 */
	public void setDOI(final String doi) {
		if (this.name != doi) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET DOI = ? WHERE ArticleId = " + articleID);
						st.setString(1, doi);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.doi = doi;
		}
	}

	/**
	 * Update database and {@link #url}
	 * 
	 * @param url
	 * @throws SQLException
	 */
	public void setURL(final String url) {
		if (this.name != url) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Url = ? WHERE ArticleId = " + articleID);
						st.setString(1, url);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.url = url;
		}
	}

	/**
	 * Update database and {@link #issn}
	 * 
	 * @param issn
	 * @throws SQLException
	 */
	public void setIssn(final Issn issn) {
		if (this.issn != issn) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET ISSN = ? WHERE ArticleId = " + articleID);
						st.setInt(1, issn.getIssn7());
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.issn = issn;
		}
	}

	/**
	 * @return {@link #conferenceName}
	 */
	public String getConferenceName() {
		return conferenceName;
	}

	/**
	 * Update database and {@link #conferenceName}
	 * 
	 * @param conferenceName
	 * @throws SQLException
	 */
	public void setConferenceName(final String conferenceName) {
		if (this.conferenceName != conferenceName) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET ConferenceName = ? WHERE ArticleId = " + articleID);
						st.setString(1, conferenceName);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.conferenceName = conferenceName;
		}
	}

	/**
	 * @return {@link #date}
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Update database and {@link #date}
	 * 
	 * @param date
	 * @throws SQLException
	 */
	public void setDate(final String date) {
		if (this.date != date) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Date = ? WHERE ArticleId = " + articleID);
						st.setString(1, date);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.date = date;
		}
	}

	/**
	 * @return {@link #town}
	 */
	public String getTown() {
		return town;
	}

	/**
	 * Update database and {@link #town}
	 * 
	 * @throws SQLException
	 */
	public void setTown(final String town) {
		if (this.town != town) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Town = ? WHERE ArticleId = " + articleID);
						st.setString(1, town);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.town = town;
		}
	}

	/**
	 * @return {@link #country}
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Update database and {@link #country}
	 * 
	 * @param country
	 * @throws SQLException
	 */
	public void setCountry(final String country) {
		if (this.country != country) {
			Main.es.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection.makeConnection();
						PreparedStatement st = Connection.prepareStatement("UPDATE Article SET Country = ? WHERE ArticleId = " + articleID);
						st.setString(1, country);
						st.execute();
						st.close();
						Connection.closeConnection();
					} catch (SQLException e) {
						Main.conError(e.getMessage());
					}
				}
			});
			this.country = country;
		}
	}

	/**
	 * Make output for file uses <code>format</code>
	 */
	public String getOutput(String format) {
		String out = format;

		if (issn != null) {
			out = out.replaceAll("%ISSN%", issn.getIssnString());
			out = out.replaceAll("%ISSN_NAME%", issn.getName());
		}
		if (name != null)
			out = out.replaceAll("%ARTICLE_NAME%", name.trim());
		out = out.replaceAll("%AUTHORS_FROM_UNIT%", getAuthorsFromUnitString());
		out = out.replaceAll("%AUTHORS_NOT_FROM_UNIT%", getAuthorsNotFromUnitString());
		out = out.replaceAll("%AUTHORS_NOT_FROM_UNIT_COUNT%", String.valueOf(getAuthorsNotFromUnitCount()));
		out = out.replaceAll("%AUTHORS_FROM_UNIT_COUNT%", String.valueOf(getAuthorsFromUnitCount()));

		out = out.replaceAll("%YEAR%", String.valueOf(year));
		out = out.replaceAll("%ISSUE%", String.valueOf(issue));
		out = out.replaceAll("%ARTICLE_ID%", articleNo);

		if (lang != null) {
			out = out.replaceAll("%LANG_PL%", lang.getNamePL());
			out = out.replaceAll("%LANG_EN%", lang.getNameEN());
		}

		out = out.replaceAll("%DOI%", doi);
		out = out.replaceAll("%URL%", url);

		out = out.replaceAll("%CONFERENCE_NAME%", conferenceName);
		out = out.replaceAll("%SPEECH_NAME%", name);
		out = out.replaceAll("%DATE%", date);
		out = out.replaceAll("%PLACE%", country + ", " + town);

		out = out.replaceAll("%TYP%", workTypeToPolish(type));

		return out;
	}

	/**
	 * @return {@link WORK_TYPE} in Polish {@link String} "artykuł", "plakat", "wystąpienie ustne" or "nieznane"
	 */
	public static String workTypeToPolish(WORK_TYPE type) {
		switch (type) {
		case ARTICLE:
			return "artukuł";
		case POSTER:
			return "plakat";
		case SPEECH:
			return "wystąpienie ustne";
		default:
			return "nieznane";
		}
	}

	/**
	 * @return {@link #authorsNotFromUnitCount}
	 */
	public int getAuthorsNotFromUnitCountNonCalc() {
		return authorsNotFromUnitCount;
	}

	@Override
	public String toString() {
		return getName();
	}

}
