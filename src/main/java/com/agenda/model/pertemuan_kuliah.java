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

public class pertemuan_kuliah extends agenda {
    public String Lokasi;
    public String MataKuliah;
    public String Sesi; // Tambahkan penampung atribut Sesi dari GUI

    // ==========================================
    // LOGIKA SQL UTK OPERASI WRITE (SIMPAN/EDIT)
    // ==========================================
    public boolean simpanKeDatabase(String username) {
        String sql = "INSERT INTO tabel_pertemuan (MataKuliah, Deskripsi, Deadline, Lokasi, Sesi, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setTimestamp(3, this.Deadline != null ? new java.sql.Timestamp(this.Deadline.getTime()) : null);
            pst.setString(4, this.Lokasi);
            pst.setString(5, this.Sesi);
            pst.setBoolean(6, this.Prioritas);
            pst.setBoolean(7, this.StatusSelesai);
            pst.setString(8, username);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Pertemuan: " + e.getMessage());
            return false;
        }
    }

    public boolean simpanKeDatabase(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            sql = "INSERT INTO tabel_pertemuan (MataKuliah, Deskripsi, Lokasi, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_pertemuan SET MataKuliah = ?, Deskripsi = ?, Lokasi = ?, Prioritas = ? WHERE id = ?";
        }

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            // Atribut diambil langsung dari object OOP ini sendiri (this)
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setString(3, this.Lokasi);
            pst.setBoolean(4, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(5, false);
                pst.setString(6, username);
            } else {
                pst.setInt(5, idData);
            }

            return pst.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error Simpan Pertemuan (OOP): " + e.getMessage());
            return false;
        }
    }
}
