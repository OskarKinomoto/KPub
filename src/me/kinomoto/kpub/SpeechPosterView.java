package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.kinomoto.kpub.Article.WORK_TYPE;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Settings panel for specific to Poster/Speech fields
 */
public class SpeechPosterView extends JPanel {
	private static final long serialVersionUID = 2610352144991196257L;

	private JTextField conferenceName;
	private JTextField date;
	private JTextField town;
	private JTextField country;

	/**
	 * Create new the panel with {@link Article#getType()} equal to {@link WORK_TYPE#SPEECH} or to {@link WORK_TYPE#POSTER}
	 */
	public SpeechPosterView(final Article article) {
		setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblConferenceName = new JLabel(article.getType() == WORK_TYPE.SPEECH ? "Conference name" : "Poster name");
		add(lblConferenceName, "2, 4, right, default");

		conferenceName = new JTextField(article.getConferenceName());
		conferenceName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setConferenceName(conferenceName.getText());
			}
		});
		conferenceName.setBackground(Color.WHITE);
		add(conferenceName, "4, 4, fill, default");

		JLabel lblDate = new JLabel("Date");
		add(lblDate, "2, 6, right, default");

		date = new JTextField(article.getDate());
		date.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setDate(date.getText());
			}
		});
		date.setBackground(Color.WHITE);
		add(date, "4, 6, fill, default");

		JLabel lblTown = new JLabel("Town");
		add(lblTown, "2, 8, right, default");

		town = new JTextField(article.getTown());
		town.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setTown(town.getText());
			}
		});
		town.setBackground(Color.WHITE);
		add(town, "4, 8, fill, default");

		JLabel lblCountry = new JLabel("Country");
		add(lblCountry, "2, 10, right, default");

		country = new JTextField(article.getCountry());
		country.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				article.setCountry(country.getText());
			}
		});
		country.setBackground(Color.WHITE);
		add(country, "4, 10, fill, default");

	}
}
