package custom;

import inventory.DlgPeresepanDokter;
import javax.swing.SwingUtilities;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // misal Anda punya variabel noRawat dari form registrasi / form rekam medis
            DlgPeresepanDokter dlg = new DlgPeresepanDokter(null,true);
            dlg.setSize(1000, 1000);
            dlg.setVisible(true);
        });
    }
}