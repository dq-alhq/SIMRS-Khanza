package custom;

import fungsi.WarnaTable;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.JTableHeader;

public class DlgPilihPemeriksaanRalan extends JDialog {

    private final String noRawat;
    private final PemeriksaanRalanDao dao = new PemeriksaanRalanDao();

    private widget.Table table;
    private PemeriksaanTableModel tableModel;
    private PilihPemeriksaan selectedItem;
    private widget.Button BtnPilih;
    private widget.Button BtnKeluar;
    
    private final SimpleDateFormat tglFmt = new SimpleDateFormat("dd-MM-yyyy");

    public DlgPilihPemeriksaanRalan(Window owner, String noRawat) {
        super(owner, "Riwayat Pemeriksaan Ralan - " + noRawat, Dialog.ModalityType.APPLICATION_MODAL);
        this.noRawat = noRawat;
        initComponents();
        loadData();
        pack();
        setupListeners();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        // Header info
        JLabel lblInfo = new JLabel("No. Rawat: " + noRawat);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        add(lblInfo, BorderLayout.NORTH);

        tableModel = new PemeriksaanTableModel();
        table = new widget.Table();
        table.setModel(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // biar bisa scroll horizontal
        styleTable(table);

        // atur lebar kolom
        int[] widths = {90, 70, 60, 70, 60, 60, 60, 60, 60, 90, 200, 200, 100, 70, 200, 200, 200, 200, 80};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(900, 380));
        add(scroll, BorderLayout.CENTER);

        // Tombol
        BtnKeluar = Util.buatTombol("/picture/x.png", "Tutup", "Alt+F4");
        BtnKeluar.setPreferredSize(new Dimension(90,23));
        BtnPilih = Util.buatTombol("/picture/tag.png", "Pilih", null);
        BtnPilih.setPreferredSize(new Dimension(90,23));

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.add(BtnPilih);
        panelBtn.add(BtnKeluar);
        add(panelBtn, BorderLayout.SOUTH);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        selectedItem = tableModel.getAt(row);
                        dispose();
                    }
                }
            }
        });
    }

    private void loadData() {
        try {
            List<PilihPemeriksaan> list = dao.getByNoRawat(noRawat);
            tableModel.setData(list);
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data pemeriksaan untuk no_rawat: " + noRawat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal load data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
            "Tanggal", "Jam", "Suhu", "Tensi", "Nadi", "Resp",
            "Tinggi", "Berat", "SpO2", "GCS", "Kesadaran",
            "Keluhan", "Pemeriksaan", "Alergi", "Lingkar Perut",
            "RTL", "Penilaian", "Instruksi", "Evaluasi", "Petugas"
        };
        private List<PilihPemeriksaan> data = new ArrayList<>();

        public void setData(List<PilihPemeriksaan> data) {
            this.data = data;
            fireTableDataChanged();
        }

        public PilihPemeriksaan getAt(int row) { return data.get(row); }

        @Override public int getRowCount()    { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int row, int col) {
            PilihPemeriksaan p = data.get(row);
            switch (col) {
                case 0:  return p.getTglPerawatan() == null ? "" : tglFmt.format(p.getTglPerawatan());
                case 1:  return p.getJamRawat();
                case 2:  return p.getSuhuTubuh();
                case 3:  return p.getTensi();
                case 4:  return p.getNadi();
                case 5:  return p.getRespirasi();
                case 6:  return p.getTinggi();
                case 7:  return p.getBerat();
                case 8:  return p.getSpo2();
                case 9:  return p.getGcs();
                case 10: return p.getKesadaran();
                case 11: return p.getKeluhan();
                case 12: return p.getPemeriksaan();
                case 13: return p.getAlergi();
                case 14: return p.getLingkarPerut();
                case 15: return p.getRtl();
                case 16: return p.getPenilaian();
                case 17: return p.getInstruksi();
                case 18: return p.getEvaluasi();
                case 19: return p.getNip();
                default: return null;
            }
        }
    }
    
    
    private void styleTable(JTable table) {
        table.setRowHeight(28);                        // baris lega
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);             // hilangkan garis vertikal
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Zebra striping
        table.setDefaultRenderer(Object.class, new WarnaTable());

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(new Color(60, 60, 60));
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);

        // Lebar kolom
        int[] widths = {90, 60, 60, 70, 55, 55, 60, 60, 55, 80, 110,
                        200, 200, 100, 70, 200, 200, 200, 200, 80};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
    
     private void setupListeners() {
        // Tabel template: double-click → pilih
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    pilihData();
                }
            }
        });

        // Tabel template: Enter/Space → pilih
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER
                        || evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    pilihData();
                }
            }
        });

      
        BtnPilih.addActionListener((ActionEvent evt) -> pilihData());
        BtnKeluar.addActionListener((ActionEvent evt) -> dispose());
    }
     
     
    private void pilihData() {
        if (table.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu datanya...!!");
            return;
        }
        
        // Simpan hasil pilihan
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        selectedItem = tableModel.getAt(row);

        dispose();
    }

}