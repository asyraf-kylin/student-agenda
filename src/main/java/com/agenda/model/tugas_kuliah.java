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

public class tugas_kuliah extends agenda {
    public String MataKuliah;
    public String TingkatKesulitan;
    public int Bobot;

    public int estimasiWaktu() {
        return Bobot * 60;
    }

    // ==========================================
    // LOGIKA SQL UTK OPERASI WRITE (SIMPAN/EDIT)
    // ==========================================
    public boolean simpanKeDatabase(String username) {
        String sql = "INSERT INTO tabel_tugas (MataKuliah, Deskripsi, Deadline, Bobot, TingkatKesulitan, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setTimestamp(3, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setInt(4, this.Bobot);
            pst.setString(5, this.TingkatKesulitan);
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setString(8, username);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Tugas: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDiDatabase() {
        String sql = "UPDATE tabel_tugas SET MataKuliah=?, Deskripsi=?, Deadline=?, Bobot=?, TingkatKesulitan=?, Prioritas=?, StatusSelesai=? WHERE id=?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setTimestamp(3, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setInt(4, this.Bobot);
            pst.setString(5, this.TingkatKesulitan);
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setInt(8, this.id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Tugas: " + e.getMessage());
            return false;
        }
    }
}