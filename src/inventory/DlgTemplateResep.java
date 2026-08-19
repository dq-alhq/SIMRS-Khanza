/*
 * DlgTemplateResep.java
 *
 * Created for resep template selection
 */

package inventory;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgTemplateResep extends javax.swing.JDialog {
    private final DefaultTableModel tabModeTemplate;
    private final DefaultTableModel tabModeDetail;
    private final Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private String kdgudangFilter = "";
    private DlgPeresepanDokter dialogResep;
    private final List<String[]> detailTemplate = new ArrayList<>();

    public DlgTemplateResep(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(700, 420);

        tabModeTemplate = new DefaultTableModel(null, new Object[]{
            "ID", "Nama Template", "Kd.Bangsal", "Bangsal", "Petugas", "Tanggal"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbTemplate.setModel(tabModeTemplate);
        tbTemplate.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 6; i++) {
            TableColumn column = tbTemplate.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(55);
            } else if (i == 1) {
                column.setPreferredWidth(220);
            } else if (i == 2) {
                column.setPreferredWidth(80);
            } else if (i == 3) {
                column.setPreferredWidth(180);
            } else if (i == 4) {
                column.setPreferredWidth(100);
            } else if (i == 5) {
                column.setPreferredWidth(90);
            }
        }
        tbTemplate.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetail = new DefaultTableModel(null, new Object[]{
            "Kode", "Nama Barang", "Jumlah", "Aturan Pakai"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbDetail.setModel(tabModeDetail);
        tbDetail.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 4; i++) {
            TableColumn column = tbDetail.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(80);
            } else if (i == 1) {
                column.setPreferredWidth(260);
            } else if (i == 2) {
                column.setPreferredWidth(70);
            } else if (i == 3) {
                column.setPreferredWidth(200);
            }
        }
        tbDetail.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        tbTemplate.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    tampilDetail();
                }
            }
        });
    }

    public void setDialogResep(DlgPeresepanDokter dialogResep) {
        this.dialogResep = dialogResep;
        if (dialogResep != null) {
            this.kdgudangFilter = dialogResep.getKdgudang();
        }
    }

    public void tampilTemplate() {
        tabModeTemplate.setRowCount(0);
        if (kdgudangFilter == null) {
            kdgudangFilter = "";
        }
        String sql = "select template_obat.id_template,template_obat.nama_template,template_obat.kd_bangsal," +
                "ifnull(bangsal.nm_bangsal,'') as nm_bangsal,template_obat.kd_pegawai,template_obat.tgl_buat " +
                "from template_obat left join bangsal on template_obat.kd_bangsal=bangsal.kd_bangsal " +
                "where template_obat.nama_template like ?";
        try {
            if (!kdgudangFilter.equals("")) {
                sql = sql + " and template_obat.kd_bangsal=?";
            }
            sql = sql + " order by template_obat.nama_template";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, "%" + TCari.getText().trim() + "%");
            if (!kdgudangFilter.equals("")) {
                ps.setString(2, kdgudangFilter);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeTemplate.addRow(new Object[]{
                    rs.getString("id_template"),
                    rs.getString("nama_template"),
                    rs.getString("kd_bangsal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("kd_pegawai"),
                    rs.getString("tgl_buat")
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if (ps != null) {
                try { ps.close(); } catch (Exception e) {}
            }
        }

        LCount.setText("" + tabModeTemplate.getRowCount());
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
        if (tbTemplate.getSelectedRow() == -1) {
            return;
        }
        String id = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString();
        try {
            ps = koneksi.prepareStatement(
                    "select detail_template_obat.kode_brng,ifnull(databarang.nama_brng,'') as nama_brng," +
                    "detail_template_obat.jumlah,detail_template_obat.aturan_pakai " +
                    "from detail_template_obat left join databarang " +
                    "on detail_template_obat.kode_brng=databarang.kode_brng " +
                    "where detail_template_obat.id_template=? order by databarang.nama_brng");
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                String kode = rs.getString("kode_brng");
                String nama = rs.getString("nama_brng");
                String jml = rs.getString("jumlah");
                String aturan = rs.getString("aturan_pakai");
                tabModeDetail.addRow(new Object[]{kode, nama, jml, aturan});
                detailTemplate.add(new String[]{kode, jml, aturan});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if (ps != null) {
                try { ps.close(); } catch (Exception e) {}
            }
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
        if (dialogResep == null) {
            JOptionPane.showMessageDialog(null, "Dialog resep belum tersedia...!!");
            return;
        }

        dialogResep.isiTemplateResep(detailTemplate);
        dialogResep.hitungUlangTotalResep();
        dialogResep.setVisible(true);
        dispose();
    }

    private void hapusTemplate() {
        if (tbTemplate.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, pilih dulu template obat yang ingin dihapus...!!");
            return;
        }

        String idTemplate = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString();
        String namaTemplate = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString();
        int jawab = JOptionPane.showConfirmDialog(
                null,
                "Hapus template obat " + namaTemplate + " ?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        PreparedStatement psHapusDetail = null;
        PreparedStatement psHapusTemplate = null;
        try {
            koneksi.setAutoCommit(false);

            psHapusDetail = koneksi.prepareStatement("delete from detail_template_obat where id_template=?");
            psHapusDetail.setString(1, idTemplate);
            psHapusDetail.executeUpdate();

            psHapusTemplate = koneksi.prepareStatement("delete from template_obat where id_template=?");
            psHapusTemplate.setString(1, idTemplate);
            psHapusTemplate.executeUpdate();

            koneksi.commit();
            JOptionPane.showMessageDialog(null, "Template obat berhasil dihapus...!!");
            tampilTemplate();
        } catch (Exception e) {
            try {
                koneksi.rollback();
            } catch (Exception ex) {
                System.out.println("Notifikasi : " + ex);
            }
            JOptionPane.showMessageDialog(null, "Gagal menghapus template obat...!!");
            System.out.println("Notifikasi : " + e);
        } finally {
            try {
                koneksi.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
            if (psHapusDetail != null) {
                try { psHapusDetail.close(); } catch (Exception e) {}
            }
            if (psHapusTemplate != null) {
                try { psHapusTemplate.close(); } catch (Exception e) {}
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelTabel = new javax.swing.JPanel();
        Scroll = new widget.ScrollPane();
        tbTemplate = new widget.Table();
        Scroll1 = new widget.ScrollPane();
        tbDetail = new widget.Table();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnPilih = new widget.Button();
        BtnHapus = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Template Resep ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelTabel.setLayout(new java.awt.GridLayout(2, 1, 1, 1));

        tbTemplate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTemplateMouseClicked(evt);
            }
        });
        tbTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbTemplateKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbTemplate);
        panelTabel.add(Scroll);

        Scroll1.setViewportView(tbDetail);
        panelTabel.add(Scroll1);

        internalFrame1.add(panelTabel, java.awt.BorderLayout.CENTER);

        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setPreferredSize(new java.awt.Dimension(250, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelisi3.add(BtnAll);

        BtnPilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnPilih.setText("Pilih");
        BtnPilih.setPreferredSize(new java.awt.Dimension(90, 23));
        BtnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihActionPerformed(evt);
            }
        });
        panelisi3.add(BtnPilih);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('4');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+4");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelisi3.add(BtnHapus);

        label10.setText("Record :");
        label10.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampilTemplate();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampilTemplate();
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampilTemplate();
    }

    private void BtnPilihActionPerformed(java.awt.event.ActionEvent evt) {
        pilihTemplate();
    }//GEN-LAST:event_BtnPilihActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        hapusTemplate();
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void tbTemplateMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            pilihTemplate();
        }
    }

    private void tbTemplateKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER ||
            evt.getKeyCode() == KeyEvent.VK_SPACE) {

            pilihTemplate();
        }
    }

    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPilih;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label label10;
    private widget.Label label9;
    private javax.swing.JPanel panelTabel;
    private widget.panelisi panelisi3;
    private widget.Table tbDetail;
    private widget.Table tbTemplate;
}
