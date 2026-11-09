package custom;

import java.util.Date;

public class PilihPemeriksaan {
    private String noRawat;
    private Date   tglPerawatan;
    private String jamRawat;
    private String suhuTubuh;
    private String tensi;
    private String nadi;
    private String respirasi;
    private String tinggi;
    private String berat;
    private String spo2;
    private String gcs;
    private String kesadaran;
    private String keluhan;
    private String pemeriksaan;
    private String alergi;
    private String lingkarPerut;
    private String rtl;
    private String penilaian;
    private String instruksi;
    private String evaluasi;
    private String nip;

    // getter & setter (singkat, buat sendiri lewat IDE: Alt+Insert / Generate)
    public String getNoRawat()       { return noRawat; }
    public void   setNoRawat(String v) { this.noRawat = v; }

    public Date   getTglPerawatan()  { return tglPerawatan; }
    public void   setTglPerawatan(Date v) { this.tglPerawatan = v; }

    public String getJamRawat()      { return jamRawat; }
    public void   setJamRawat(String v) { this.jamRawat = v; }

    public String getSuhuTubuh()     { return suhuTubuh != null ? suhuTubuh : ""; }
    public void   setSuhuTubuh(String v) { this.suhuTubuh = v; }

    public String getTensi()         { return tensi; }
    public void   setTensi(String v) { this.tensi = v; }

    public String getNadi()          { return nadi != null ? nadi : ""; }
    public void   setNadi(String v)  { this.nadi = v; }

    public String getRespirasi()     { return respirasi != null ? respirasi : ""; }
    public void   setRespirasi(String v) { this.respirasi = v; }

    public String getTinggi()        { return tinggi != null ? tinggi : ""; }
    public void   setTinggi(String v){ this.tinggi = v; }

    public String getBerat()         { return berat != null ? berat : ""; }
    public void   setBerat(String v) { this.berat = v; }

    public String getSpo2()          { return spo2; }
    public void   setSpo2(String v)  { this.spo2 = v; }

    public String getGcs()           { return gcs; }
    public void   setGcs(String v)   { this.gcs = v; }

    public String getKesadaran()     { return kesadaran; }
    public void   setKesadaran(String v) { this.kesadaran = v; }

    public String getKeluhan()       { return keluhan != null ? keluhan : ""; }
    public void   setKeluhan(String v) { this.keluhan = v; }

    public String getPemeriksaan()   { return pemeriksaan != null ? pemeriksaan : ""; }
    public void   setPemeriksaan(String v) { this.pemeriksaan = v; }

    public String getAlergi()        { return alergi != null ? alergi : ""; }
    public void   setAlergi(String v){ this.alergi = v; }

    public String getLingkarPerut()  { return lingkarPerut != null ? lingkarPerut : ""; }
    public void   setLingkarPerut(String v) { this.lingkarPerut = v; }

    public String getRtl()           { return rtl != null ? rtl : ""; }
    public void   setRtl(String v)   { this.rtl = v; }

    public String getPenilaian()     { return penilaian != null ? penilaian : ""; }
    public void   setPenilaian(String v) { this.penilaian = v; }

    public String getInstruksi()     { return instruksi != null ? instruksi : ""; }
    public void   setInstruksi(String v) { this.instruksi = v; }

    public String getEvaluasi()      { return evaluasi != null ? evaluasi : ""; }
    public void   setEvaluasi(String v) { this.evaluasi = v; }

    public String getNip()           { return nip; }
    public void   setNip(String v)   { this.nip = v; }
}