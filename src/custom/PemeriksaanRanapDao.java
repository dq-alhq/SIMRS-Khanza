package custom;

import fungsi.koneksiDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PemeriksaanRanapDao {
    private final Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    
    public List<PilihPemeriksaan> getByNoRawat(String noRawat) throws SQLException {
        List<PilihPemeriksaan> list = new ArrayList<>();
        String sql = "SELECT pemeriksaan_ranap.*, pegawai.nama " +
                     "FROM pemeriksaan_ranap " +
                     "JOIN pegawai ON pegawai.nik = pemeriksaan_ranap.nip " +
                     "WHERE no_rawat = ? " +
                     "ORDER BY tgl_perawatan DESC, jam_rawat DESC";

        try {
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return list;
    }

    private PilihPemeriksaan map(ResultSet rs) throws SQLException {
        PilihPemeriksaan p = new PilihPemeriksaan();
        p.setNoRawat     (rs.getString("no_rawat"));
        p.setTglPerawatan(rs.getDate("tgl_perawatan"));
        p.setJamRawat    (rs.getString("jam_rawat"));
        p.setSuhuTubuh   (rs.getString("suhu_tubuh"));
        p.setTensi       (rs.getString("tensi"));
        p.setNadi        (rs.getString("nadi"));
        p.setRespirasi   (rs.getString("respirasi"));
        p.setTinggi      (rs.getString("tinggi"));
        p.setBerat       (rs.getString("berat"));
        p.setSpo2        (rs.getString("spo2"));
        p.setGcs         (rs.getString("gcs"));
        p.setKesadaran   (rs.getString("kesadaran"));
        p.setKeluhan     (rs.getString("keluhan"));
        p.setPemeriksaan (rs.getString("pemeriksaan"));
        p.setAlergi      (rs.getString("alergi"));
        p.setLingkarPerut(rs.getString("lingkar_perut"));
        p.setRtl         (rs.getString("rtl"));
        p.setPenilaian   (rs.getString("penilaian"));
        p.setInstruksi   (rs.getString("instruksi"));
        p.setEvaluasi    (rs.getString("evaluasi"));
        p.setNip         (rs.getString("nama"));
        return p;
    }
}