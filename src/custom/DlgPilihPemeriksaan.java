
package custom;

import fungsi.WarnaTable;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DlgPilihPemeriksaan extends JDialog {

    private final String noRawat;
    private final PemeriksaanDao dao = new PemeriksaanDao();

    private widget.Table table;
    private PemeriksaanTableModel tableModel;
    private PilihPemeriksaan selectedItem;

    private widget.Button BtnPilih;
    private widget.Button BtnKeluar;

    private final SimpleDateFormat tglFmt =
            new SimpleDateFormat("dd-MM-yyyy");

    public DlgPilihPemeriksaan(Window owner, String noRawat) {
        super(
            owner,
            "Riwayat Pemeriksaan - " + noRawat,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        this.noRawat = noRawat;

        initComponents();
        setupListeners();
        loadData();

        pack();
        setMinimumSize(new Dimension(900, 450));
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        // Header informasi
        JLabel lblInfo = new JLabel(
                "No. Rawat: " + noRawat
        );

        lblInfo.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 0, 8)
        );

        add(lblInfo, BorderLayout.NORTH);

        // Table
        tableModel = new PemeriksaanTableModel();

        table = new widget.Table();
        table.setModel(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(1100, 380));

        add(scroll, BorderLayout.CENTER);

        // Tombol
        BtnKeluar = Util.buatTombol(
                "/picture/x.png",
                "Tutup",
                "Alt+F4"
        );

        BtnKeluar.setPreferredSize(
                new Dimension(90, 23)
        );

        BtnPilih = Util.buatTombol(
                "/picture/tag.png",
                "Pilih",
                null
        );

        BtnPilih.setPreferredSize(
                new Dimension(90, 23)
        );

        JPanel panelBtn = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBtn.add(BtnPilih);
        panelBtn.add(BtnKeluar);

        add(panelBtn, BorderLayout.SOUTH);

        // Double click untuk memilih data
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    pilihData();
                }
            }
        });
    }

    private void loadData() {
        try {
            List<PilihPemeriksaan> list =
                    dao.getByNoRawat(noRawat);

            tableModel.setData(list);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tidak ada data pemeriksaan untuk no_rawat: "
                                + noRawat,
                        "Informasi",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal load data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    public PilihPemeriksaan getSelectedItem() {
        return selectedItem;
    }

    // ============================================================
    // TABLE MODEL
    // ============================================================

    class PemeriksaanTableModel extends AbstractTableModel {

        private final String[] cols = {
            "Jenis",
            "Tanggal",
            "Jam",
            "Suhu",
            "Tensi",
            "Nadi",
            "Resp",
            "Tinggi",
            "Berat",
            "SpO2",
            "GCS",
            "Kesadaran",
            "Keluhan",
            "Pemeriksaan",
            "Alergi",
            "Lingkar Perut",
            "RTL",
            "Penilaian",
            "Instruksi",
            "Evaluasi",
            "Petugas"
        };

        private List<PilihPemeriksaan> data =
                new ArrayList<>();

        public void setData(List<PilihPemeriksaan> data) {
            this.data = data;
            fireTableDataChanged();
        }

        public PilihPemeriksaan getAt(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int col) {
            return cols[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            PilihPemeriksaan p = data.get(row);

            switch (col) {
                case 0:
                    return p.getJenisPemeriksaan();

                case 1:
                    return p.getTglPerawatan() == null
                            ? ""
                            : tglFmt.format(p.getTglPerawatan());

                case 2:
                    return p.getJamRawat();

                case 3:
                    return p.getSuhuTubuh();

                case 4:
                    return p.getTensi();

                case 5:
                    return p.getNadi();

                case 6:
                    return p.getRespirasi();

                case 7:
                    return p.getTinggi();

                case 8:
                    return p.getBerat();

                case 9:
                    return p.getSpo2();

                case 10:
                    return p.getGcs();

                case 11:
                    return p.getKesadaran();

                case 12:
                    return p.getKeluhan();

                case 13:
                    return p.getPemeriksaan();

                case 14:
                    return p.getAlergi();

                case 15:
                    return p.getLingkarPerut();

                case 16:
                    return p.getRtl();

                case 17:
                    return p.getPenilaian();

                case 18:
                    return p.getInstruksi();

                case 19:
                    return p.getEvaluasi();

                case 20:
                    return p.getNip();

                default:
                    return null;
            }
        }
    }

    // ============================================================
    // TABLE STYLE
    // ============================================================

    private void styleTable(JTable table) {

        table.setRowHeight(28);
        table.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(
                new Color(230, 230, 230)
        );

        table.setIntercellSpacing(
                new Dimension(0, 1)
        );

        table.setFillsViewportHeight(true);

        // Zebra striping
        table.setDefaultRenderer(
                Object.class,
                new WarnaTable()
        );

        // Header
        JTableHeader header = table.getTableHeader();

        header.setFont(
                header.getFont().deriveFont(Font.BOLD)
        );

        header.setBackground(
                new Color(245, 245, 245)
        );

        header.setForeground(
                new Color(60, 60, 60)
        );

        header.setPreferredSize(
                new Dimension(0, 34)
        );

        header.setReorderingAllowed(false);

        // Lebar kolom
        int[] widths = {
            75, 90, 60, 60, 70, 55, 55,
            60, 60, 55, 80, 110, 200,
            200, 100, 100, 200, 200,
            200, 200, 120
        };

        for (int i = 0;
                i < widths.length && i < table.getColumnCount();
                i++) {

            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }
    }

    // ============================================================
    // LISTENERS
    // ============================================================

    private void setupListeners() {

        // Enter untuk memilih data
        table.getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        ).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "pilihData"
        );

        table.getActionMap().put(
                "pilihData",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pilihData();
                    }
                }
        );

        // Tombol
        BtnPilih.addActionListener(
                (ActionEvent evt) -> pilihData()
        );

        BtnKeluar.addActionListener(
                (ActionEvent evt) -> dispose()
        );

        // ESC untuk menutup dialog
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // ============================================================
    // PILIH DATA
    // ============================================================

    private void pilihData() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih dulu datanya...!!",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);

        selectedItem = tableModel.getAt(row);

        dispose();
    }
}
