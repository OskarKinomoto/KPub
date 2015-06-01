package me.kinomoto.kpub;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

/**
 * {@link JFormattedTextField} with mask for {@link Issn} "####-###*" where '*' can be 0-9X. Small-case 'x' can be typed and it will be transformed to
 * upper-case 'X'.
 */
public class IssnTextEditor extends JFormattedTextField {
	private static final long serialVersionUID = -2305065685135737870L;

	/**
	 * @see IssnTextEditor
	 */
	public IssnTextEditor() {
		this.setBackground(Color.WHITE);
		MaskFormatter issnFormat = null;
		try {
			issnFormat = new MaskFormatter("####-###*");
			issnFormat.setValidCharacters("1234567890X");
		} catch (ParseException e) {
			// nth to be done
			ErrorDialog.error(e.getMessage(), null);
		}
		this.setFormatter(issnFormat);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'x')
					e.setKeyChar('X');
				super.keyTyped(e);
			}
		});
	}

}
