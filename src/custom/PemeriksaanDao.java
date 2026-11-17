
package custom;

import fungsi.koneksiDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PemeriksaanDao {

    private final Connection koneksi = koneksiDB.condb();

    public List<PilihPemeriksaan> getByNoRawat(String noRawat) throws SQLException {

        List<PilihPemeriksaan> list = new ArrayList<>();

        String sql = """
            SELECT
                pemeriksaan.no_rawat,
                pemeriksaan.tgl_perawatan,
                pemeriksaan.jam_rawat,
                pemeriksaan.suhu_tubuh,
                pemeriksaan.tensi,
                pemeriksaan.nadi,
                pemeriksaan.respirasi,
                pemeriksaan.tinggi,
                pemeriksaan.berat,
                pemeriksaan.spo2,
                pemeriksaan.gcs,
                pemeriksaan.kesadaran,
                pemeriksaan.keluhan,
                pemeriksaan.pemeriksaan,
                pemeriksaan.alergi,
                pemeriksaan.lingkar_perut,
                pemeriksaan.rtl,
                pemeriksaan.penilaian,
                pemeriksaan.instruksi,
                pemeriksaan.evaluasi,
                pemeriksaan.nip,
                pegawai.nama,
                'Ralan' AS jenis_pemeriksaan
            FROM pemeriksaan_ralan pemeriksaan
            LEFT JOIN pegawai
                ON pegawai.nik = pemeriksaan.nip
            WHERE pemeriksaan.no_rawat = ?

            UNION ALL

            SELECT
                pemeriksaan.no_rawat,
                pemeriksaan.tgl_perawatan,
                pemeriksaan.jam_rawat,
                pemeriksaan.suhu_tubuh,
                pemeriksaan.tensi,
                pemeriksaan.nadi,
                pemeriksaan.respirasi,
                pemeriksaan.tinggi,
                pemeriksaan.berat,
                pemeriksaan.spo2,
                pemeriksaan.gcs,
                pemeriksaan.kesadaran,
                pemeriksaan.keluhan,
                pemeriksaan.pemeriksaan,
                pemeriksaan.alergi,
                NULL AS lingkar_perut,
                pemeriksaan.rtl,
                pemeriksaan.penilaian,
                pemeriksaan.instruksi,
                pemeriksaan.evaluasi,
                pemeriksaan.nip,
                pegawai.nama,
                'Ranap' AS jenis_pemeriksaan
            FROM pemeriksaan_ranap pemeriksaan
            LEFT JOIN pegawai
                ON pegawai.nik = pemeriksaan.nip
            WHERE pemeriksaan.no_rawat = ?

            ORDER BY tgl_perawatan DESC, jam_rawat DESC
        """;

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {

            ps.setString(1, noRawat);
            ps.setString(2, noRawat);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }

        return list;
    }

    private PilihPemeriksaan map(ResultSet rs) throws SQLException {

        PilihPemeriksaan p = new PilihPemeriksaan();

        p.setNoRawat(rs.getString("no_rawat"));
        p.setTglPerawatan(rs.getDate("tgl_perawatan"));
        p.setJamRawat(rs.getString("jam_rawat"));

        p.setSuhuTubuh(rs.getString("suhu_tubuh"));
        p.setTensi(rs.getString("tensi"));
        p.setNadi(rs.getString("nadi"));
        p.setRespirasi(rs.getString("respirasi"));
        p.setTinggi(rs.getString("tinggi"));
        p.setBerat(rs.getString("berat"));
        p.setSpo2(rs.getString("spo2"));
        p.setGcs(rs.getString("gcs"));
        p.setKesadaran(rs.getString("kesadaran"));

        p.setKeluhan(rs.getString("keluhan"));
        p.setPemeriksaan(rs.getString("pemeriksaan"));
        p.setAlergi(rs.getString("alergi"));
        p.setLingkarPerut(rs.getString("lingkar_perut"));

        p.setRtl(rs.getString("rtl"));
        p.setPenilaian(rs.getString("penilaian"));
        p.setInstruksi(rs.getString("instruksi"));
        p.setEvaluasi(rs.getString("evaluasi"));

        p.setNip(rs.getString("nama"));
        p.setJenisPemeriksaan(rs.getString("jenis_pemeriksaan"));

        return p;
    }
}
