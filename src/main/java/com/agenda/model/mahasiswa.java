/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

import com.agenda.service.dokumen;
import com.agenda.util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;

public class mahasiswa implements dokumen {
    public String Nama;
    public String Password;
    
    public static int modeEkspor = 0;
    
    public mahasiswa(String Nama) {
        this.Nama = Nama;
    }
    
    public mahasiswa() {
    }

    public boolean loginDatabase(String username, String pass) {
        String sql = "SELECT * FROM mahasiswa WHERE nama=? AND password=?";
        
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, username);
            pst.setString(2, pass);
            
            try (java.sql.ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    this.Nama = rs.getString("nama");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Login: " + e.getMessage());
        }
        return false;
    }

    public boolean hapusAgenda(agenda data, int idData) {
        String namaTabel = "";
        
        if (data instanceof pertemuan_kuliah) namaTabel = "tabel_pertemuan";

        if (namaTabel.isEmpty()) return false;

        String sql = "DELETE FROM " + namaTabel + " WHERE id = ?";
        
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idData);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Hapus Agenda: " + e.getMessage());
            return false;
        }
    }

    public String getNama() {
        return Nama;
    }

    public void logout() {
        String namaUser = (this.Nama != null) ? this.Nama : "Pengguna anonim";
        System.out.println("Sesi " + namaUser + " telah diakhiri.");
        this.Nama = null;
    }
    
    @Override
    public void simpanCSV() {
        String fileName = "";
        String sql = "";
        String header = "";
        String namaBersih = (this.Nama != null && !this.Nama.isEmpty())
                ? this.Nama.replaceAll("\\s+", "_")
                : "user";

        if (modeEkspor == 0) {
            fileName = "rekap_tugas_" + namaBersih + ".csv";
            header = "Mata Kuliah,Tingkat Kesulitan,Bobot,Deskripsi,Deadline,Prioritas,Status Selesai";
            sql = "SELECT MataKuliah, TingkatKesulitan, Bobot, Deskripsi, Deadline, Prioritas, StatusSelesai FROM tabel_tugas";
        } else if (modeEkspor == 1) {
            fileName = "rekap_pertemuan_" + namaBersih + ".csv";
            header = "Mata Kuliah,Lokasi,Deskripsi,Sesi,Deadline (Tanggal),Prioritas,Status Selesai";
            sql = "SELECT MataKuliah, Lokasi, Deskripsi, Sesi, Deadline, Prioritas, StatusSelesai FROM tabel_pertemuan";
        } else if (modeEkspor == 2) {
            fileName = "rekap_kegiatan_" + namaBersih + ".csv";
            header = "Nama Kegiatan,Lokasi,Anggota,Deskripsi,Deadline (Tanggal),Prioritas,Status Selesai";
            sql = "SELECT NamaKegiatan, Lokasi, Anggota, Deskripsi, Deadline, Prioritas, StatusSelesai FROM tabel_kegiatan";
        } else if (modeEkspor == 3) {
            fileName = "rekap_belajar_" + namaBersih + ".csv";
            header = "Mata Kuliah,Materi,Durasi,Deskripsi,Deadline (Tanggal),Prioritas,Status Selesai";
            sql = "SELECT MataKuliah, Materi, Durasi, Deskripsi, Deadline, Prioritas, StatusSelesai FROM tabel_belajar";
        }

        try (Connection conn = koneksi.getKoneksi(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql); java.io.FileWriter writer = new java.io.FileWriter(fileName)) {

            writer.append(header).append("\n");

            while (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String val = rs.getString(i);
                    writer.append(val != null ? val : "-");
                    if (i < meta.getColumnCount()) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
            writer.flush();
            javax.swing.JOptionPane.showMessageDialog(null, "Data berhasil diekspor ke: " + fileName);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error Export: " + e.getMessage());
        }
    }

    @Override
    public void simpanTxt() {
    }

    @Override
    public agenda[] lihatAgenda() {
        return new agenda[0];
    }
}
