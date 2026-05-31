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

public class belajar_mandiri extends agenda {
    public String MataKuliah;
    public String Materi;
    public int Durasi;

    // ==========================================
    // LOGIKA SQL UTK OPERASI WRITE (SIMPAN/EDIT)
    // ==========================================
    public boolean simpanKeDatabase(String username) {
        String sql = "INSERT INTO tabel_belajar (MataKuliah, Materi, Deskripsi, Deadline, Durasi, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Materi);
            pst.setString(3, this.Deskripsi);
            pst.setTimestamp(4, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setInt(5, this.Durasi);
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setString(8, username);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Belajar: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDiDatabase() {
        String sql = "UPDATE tabel_belajar SET MataKuliah=?, Materi=?, Deskripsi=?, Deadline=?, Durasi=?, Prioritas=?, StatusSelesai=? WHERE id=?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Materi);
            pst.setString(3, this.Deskripsi);
            pst.setTimestamp(4, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setInt(5, this.Durasi);
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setInt(8, this.id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Belajar: " + e.getMessage());
            return false;
        }
    }
}
