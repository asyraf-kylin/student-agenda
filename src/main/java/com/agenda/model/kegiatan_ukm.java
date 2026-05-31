/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

/**
 *
 * @author Ajax
 */
import com.agenda.util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class kegiatan_ukm extends agenda {
    public String NamaKegiatan;
    public String Lokasi;
    public String[] Anggota;

    // Helper untuk merubah Array String OOP menjadi satu string untuk SQL Database
    public String getAnggotaSebagaiString() {
        if (this.Anggota == null) return "";
        return String.join(", ", this.Anggota);
    }

    // ==========================================
    // LOGIKA SQL UTK OPERASI WRITE (SIMPAN/EDIT)
    // ==========================================
    public boolean simpanKeDatabase(String username) {
        String sql = "INSERT INTO tabel_kegiatan (NamaKegiatan, Deskripsi, Deadline, Lokasi, Anggota, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.NamaKegiatan);
            pst.setString(2, this.Deskripsi);
            pst.setTimestamp(3, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setString(4, this.Lokasi);
            pst.setString(5, getAnggotaSebagaiString());
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setString(8, username);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Kegiatan: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDiDatabase() {
        String sql = "UPDATE tabel_kegiatan SET NamaKegiatan=?, Deskripsi=?, Deadline=?, Lokasi=?, Anggota=?, Prioritas=?, StatusSelesai=? WHERE id=?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.NamaKegiatan);
            pst.setString(2, this.Deskripsi);
            pst.setTimestamp(3, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setString(4, this.Lokasi);
            pst.setString(5, getAnggotaSebagaiString());
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setInt(8, this.id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Kegiatan: " + e.getMessage());
            return false;
        }
    }
}