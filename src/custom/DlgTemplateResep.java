package custom;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgTemplateResep extends JDialog {

      // ─────────────────────────────────────────────────────────────
    // Field: Data
    // ─────────────────────────────────────────────────────────────
    private final Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private String kdgudangFilter = "";
    private final List<String[]> detailTemplate = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────
    // Field: Model Tabel
    // ─────────────────────────────────────────────────────────────
    private final DefaultTableModel tabModeTemplate;
    private final DefaultTableModel tabModeDetail;

    // ─────────────────────────────────────────────────────────────
    // Field: Komponen UI
    // ─────────────────────────────────────────────────────────────
    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane scrollTemplate;
    private widget.ScrollPane scrollDetail;
    private widget.Table tbTemplate;
    private widget.Table tbDetail;

    private widget.Label labelKeyWord;
    private widget.Label labelRecord;
    private widget.Label LCount;

    private widget.TextBox TCari;

    private widget.Button BtnCari;
    private widget.Button BtnAll;
    private widget.Button BtnPilih;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;

    // ─────────────────────────────────────────────────────────────
    // Konstanta Layout
    // ─────────────────────────────────────────────────────────────
    private static final int WINDOW_WIDTH  = 700;
    private static final int WINDOW_HEIGHT = 420;
    private static final int WINDOW_X      = 10;
    private static final int WINDOW_Y      = 2;
    private static final int TOOLBAR_HEIGHT = 43;
    private static final int TOOLBAR_GAP_H  = 4;
    private static final int TOOLBAR_GAP_V  = 9;
    
    // Field hasil pilihan
    private boolean dipilih = false;
    private String idTemplateTerpilih;
    private String namaTemplateTerpilih;
    private final List<String[]> detailTerpilih = new ArrayList<>();
    
    public boolean isDipilih() {
        return dipilih;
    }

    public String getIdTemplateTerpilih() {
        return idTemplateTerpilih;
    }

    public String getNamaTemplateTerpilih() {
        return namaTemplateTerpilih;
    }

    public List<String[]> getDetailTerpilih() {
        return detailTerpilih;
    }

    // ═════════════════════════════════════════════════════════════
    // Constructor
    // ═════════════════════════════════════════════════════════════
    public DlgTemplateResep(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // ✅ WAJIB di paling awal, sebelum apapun yang bikin displayable
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Baru bangun UI
        tabModeTemplate = buatModelTemplate();
        tabModeDetail   = buatModelDetail();

        initUI();
        layoutUI();              // pack() di sini aman
        setupTableProperties();
        setupListeners();

        // Setelah pack(), baru set posisi & ukuran
        setLocation(WINDOW_X, WINDOW_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    // ═════════════════════════════════════════════════════════════
    // Model Tabel
    // ═════════════════════════════════════════════════════════════
    private DefaultTableModel buatModelTemplate() {
        return new DefaultTableModel(null, new Object[]{
            "ID", "Nama Template", "Pembuat", "Tanggal"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
    }

    private DefaultTableModel buatModelDetail() {
        return new DefaultTableModel(null, new Object[]{
            "Kode", "Nama Barang", "Jumlah", "Aturan Pakai", "Stok"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
    }

    // ═════════════════════════════════════════════════════════════
    // Init UI — buat komponen
    // ═════════════════════════════════════════════════════════════
    private void initUI() {
        // Internal frame
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Template Resep ]::",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)));

        // Tabel
        tbTemplate = new widget.Table();
        tbDetail   = new widget.Table();
        tbTemplate.setModel(tabModeTemplate);
        tbDetail.setModel(tabModeDetail);

        // Scroll
        scrollTemplate = new widget.ScrollPane();
        scrollDetail   = new widget.ScrollPane();
        scrollTemplate.setViewportView(tbTemplate);
        scrollDetail.setViewportView(tbDetail);

        // Toolbar components
        labelKeyWord = new widget.Label();
        labelKeyWord.setText("Key Word :");
        labelKeyWord.setPreferredSize(new Dimension(68, 23));

        TCari = new widget.TextBox();
        TCari.setPreferredSize(new Dimension(250, 23));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        BtnCari = Util.buatTombol("/picture/accept.png", null, null);
        BtnAll  = Util.buatTombol("/picture/Search-16x16.png", null, null);

        BtnPilih = Util.buatTombol("/picture/category.png", "Pilih", null);
        BtnPilih.setPreferredSize(new Dimension(90, 23));

        BtnHapus = Util.buatTombol("/picture/delete-16x16.png", "Hapus", "Alt+4");
        BtnHapus.setMnemonic('4');
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new Dimension(90,23));

        labelRecord = new widget.Label();
        labelRecord.setText("Record :");
        labelRecord.setPreferredSize(new Dimension(50, 23));

        LCount = new widget.Label();
        LCount.setText("0");
        LCount.setHorizontalAlignment(SwingConstants.LEFT);
        LCount.setPreferredSize(new Dimension(50, 23));

        BtnKeluar = Util.buatTombol("/picture/exit.png", null, null);
        BtnKeluar.setPreferredSize(new Dimension(90, 23));
    }

    // ═════════════════════════════════════════════════════════════
    // Layout UI — susun komponen
    // ═════════════════════════════════════════════════════════════
    private void layoutUI() {
         internalFrame1.setLayout(new BorderLayout(1, 1));

         // Panel tengah
         JPanel panelTabel = new JPanel(new GridLayout(2, 1, 1, 1));
         panelTabel.add(scrollTemplate);
         panelTabel.add(scrollDetail);

         internalFrame1.add(panelTabel, BorderLayout.CENTER);

         // Panel bawah
         widget.panelisi panelToolbar = new widget.panelisi();

         // Jangan kasih width 100
         panelToolbar.setPreferredSize(new Dimension(0, TOOLBAR_HEIGHT));

         panelToolbar.setLayout(new FlowLayout(
                 FlowLayout.LEFT,
                 TOOLBAR_GAP_H,
                 TOOLBAR_GAP_V
         ));

         panelToolbar.add(labelKeyWord);
         panelToolbar.add(TCari);
         panelToolbar.add(BtnCari);
         panelToolbar.add(BtnAll);
         panelToolbar.add(BtnPilih);
         panelToolbar.add(BtnHapus);
         panelToolbar.add(labelRecord);
         panelToolbar.add(LCount);
         panelToolbar.add(BtnKeluar);

         internalFrame1.add(panelToolbar, BorderLayout.SOUTH);

         getContentPane().add(internalFrame1, BorderLayout.CENTER);

         pack();

         // kalau perlu ukuran minimum
         setMinimumSize(new Dimension(800, 500));
     }


    // ═════════════════════════════════════════════════════════════
    // Setup Properti Tabel
    // ═════════════════════════════════════════════════════════════
    private void setupTableProperties() {
        // Tabel template
        tbTemplate.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setLebarKolom(tbTemplate, new int[]{0, 300, 200, 100});
        tbTemplate.setDefaultRenderer(Object.class, new WarnaTable());
        tbTemplate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tabel detail
        tbDetail.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetail.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setLebarKolom(tbDetail, new int[]{80, 250, 60, 200, 60});
        tbDetail.setDefaultRenderer(Object.class, new WarnaTable());
        tbDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setLebarKolom(JTable table, int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
    }

    // ═════════════════════════════════════════════════════════════
    // Setup Listeners
    // ═════════════════════════════════════════════════════════════
    private void setupListeners() {
        // Window opened → load data
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evt) {
                tampilTemplate();
            }
        });

        // Tabel template: selection → load detail
        tbTemplate.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    tampilDetail();
                }
            }
        });

        // Tabel template: double-click → pilih
        tbTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    pilihTemplate();
                }
            }
        });

        // Tabel template: Enter/Space → pilih
        tbTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER
                        || evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    pilihTemplate();
                }
            }
        });

        // TCari: Enter → cari
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    tampilTemplate();
                }
            }
        });

        // Tombol-tombol
        BtnCari.addActionListener((ActionEvent evt) -> tampilTemplate());
        BtnAll.addActionListener((ActionEvent evt) -> {
            TCari.setText("");
            tampilTemplate();
        });
        BtnPilih.addActionListener((ActionEvent evt) -> pilihTemplate());
        BtnHapus.addActionListener((ActionEvent evt) -> hapusTemplate());
        BtnKeluar.addActionListener((ActionEvent evt) -> dispose());
    }

    // ═════════════════════════════════════════════════════════════
    // Business Logic
    // ═════════════════════════════════════════════════════════════
    public void tampilTemplate() {
        tabModeTemplate.setRowCount(0);

        if (kdgudangFilter == null) {
            kdgudangFilter = "";
        }

        String sql =
                "SELECT "
                + "    template_obat.id_template, "
                + "    template_obat.nama_template, "
                + "    pegawai.nama , "
                + "    template_obat.tgl_buat "
                + "FROM template_obat "
                + "LEFT JOIN pegawai "
                + "    ON template_obat.kd_pegawai = pegawai.nik "
                + "WHERE template_obat.nama_template LIKE ? ";

        sql += "ORDER BY template_obat.nama_template";

        try {
            ps = koneksi.prepareStatement(sql);

            int param = 1;

            // Filter pencarian
            ps.setString(param++, "%" + TCari.getText().trim() + "%");

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeTemplate.addRow(new Object[]{
                    rs.getString("id_template"),
                    rs.getString("nama_template"),
                    rs.getString("nama"),
                    rs.getString("tgl_buat")
                });
            }

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            tutupResource();
        }

        LCount.setText(String.valueOf(tabModeTemplate.getRowCount()));

        if (tabModeTemplate.getRowCount() > 0) {
            tbTemplate.setRowSelectionInterval(0, 0);
            tampilDetail();
        } else {
            tabModeDetail.setRowCount(0);
            detailTemplate.clear();
        }
    }


    private void tampilDetail() {
        tabModeDetail.setRowCount(0);
        detailTemplate.clear();

        int selectedRow = tbTemplate.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        String id = tbTemplate.getValueAt(selectedRow, 0).toString();

        String sql =
                "SELECT "
                + "    detail_template_obat.kode_brng, "
                + "    IFNULL(databarang.nama_brng, '') AS nama_brng, "
                + "    detail_template_obat.jumlah, "
                + "    gudangbarang.stok, "
                + "    detail_template_obat.aturan_pakai "
                + "FROM detail_template_obat "
                + "LEFT JOIN databarang "
                + "    ON detail_template_obat.kode_brng = databarang.kode_brng "
                + "LEFT JOIN gudangbarang "
                + "    ON detail_template_obat.kode_brng = gudangbarang.kode_brng "
                + "    AND gudangbarang.kd_bangsal = 'FARM'"
                + "WHERE detail_template_obat.id_template = ? "
                + "ORDER BY databarang.nama_brng";

        try {
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, id);

            rs = ps.executeQuery();

            while (rs.next()) {
                String kode = rs.getString("kode_brng");
                String nama = rs.getString("nama_brng");
                String jumlah = rs.getString("jumlah");
                String stok = rs.getString("stok");
                String aturan = rs.getString("aturan_pakai");

                tabModeDetail.addRow(new Object[]{
                    kode,
                    nama,
                    jumlah,
                    aturan,
                    stok
                });

                detailTemplate.add(new String[]{
                    kode,
                    jumlah,
                    aturan
                });
            }

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            tutupResource();
        }
    }



    private void pilihTemplate() {
        if (tbTemplate.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, pilih dulu template resep...!!");
            return;
        }
        if (detailTemplate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail template kosong...!!");
            return;
        }
        
        // Simpan hasil pilihan
        int row = tbTemplate.getSelectedRow();
        idTemplateTerpilih   = tbTemplate.getValueAt(row, 0).toString();
        namaTemplateTerpilih = tbTemplate.getValueAt(row, 1).toString();

        detailTerpilih.clear();
        for (int i = 0; i < tbDetail.getRowCount(); i++) {
            detailTerpilih.add(new String[]{
                tbDetail.getValueAt(i, 0).toString(), // kode_brng
                tbDetail.getValueAt(i, 1).toString(), // nama_brng
                tbDetail.getValueAt(i, 2).toString(), // jumlah
                tbDetail.getValueAt(i, 3).toString()  // aturan_pakai
            });
        }
        dipilih = true;

        dispose();
    }

    private void hapusTemplate() {
        if (tbTemplate.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null,
                    "Maaf, pilih dulu template obat yang ingin dihapus...!!");
            return;
        }

        String idTemplate   = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString();
        String namaTemplate = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString();

        int jawab = JOptionPane.showConfirmDialog(null,
                "Hapus template obat " + namaTemplate + " ?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        PreparedStatement psHapusDetail = null;
        PreparedStatement psHapusTemplate = null;
        try {
            koneksi.setAutoCommit(false);

            psHapusDetail = koneksi.prepareStatement(
                    "delete from detail_template_obat where id_template = ?");
            psHapusDetail.setString(1, idTemplate);
            psHapusDetail.executeUpdate();

            psHapusTemplate = koneksi.prepareStatement(
                    "delete from template_obat where id_template = ?");
            psHapusTemplate.setString(1, idTemplate);
            psHapusTemplate.executeUpdate();

            koneksi.commit();
            JOptionPane.showMessageDialog(null, "Template obat berhasil dihapus...!!");
            tampilTemplate();
        } catch (Exception e) {
            rollbackQuietly();
            JOptionPane.showMessageDialog(null, "Gagal menghapus template obat...!!");
            System.out.println("Notifikasi : " + e);
        } finally {
            setAutoCommitQuietly(true);
            closeQuietly(psHapusDetail);
            closeQuietly(psHapusTemplate);
        }
    }

    // ═════════════════════════════════════════════════════════════
    // Utility: Resource Management
    // ═════════════════════════════════════════════════════════════
    private void tutupResource() {
        closeQuietly(rs);
        closeQuietly(ps);
        rs = null;
        ps = null;
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // silent
            }
        }
    }

    private void rollbackQuietly() {
        try {
            koneksi.rollback();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void setAutoCommitQuietly(boolean value) {
        try {
            koneksi.setAutoCommit(value);
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
}