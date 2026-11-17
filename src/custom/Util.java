package custom;

import java.awt.Dimension;
import java.net.URL;
import javax.swing.ImageIcon; 

public class Util {    
    public static widget.Button buatTombol(
            String iconPath,
            String text,
            String tooltip
    ) {
        widget.Button btn = new widget.Button();

        URL iconUrl = Util.class.getResource(iconPath);

        if (iconUrl != null) {
            btn.setIcon(new ImageIcon(iconUrl));
        }

        if (text != null) {
            btn.setText(text);
        }

        if (tooltip != null) {
            btn.setToolTipText(tooltip);
        }

        btn.setPreferredSize(new Dimension(28, 23));

        return btn;
    }
}
