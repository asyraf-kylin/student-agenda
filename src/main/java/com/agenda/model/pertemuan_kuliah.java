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
    public String Sesi;

    @Override
    public void tandaiSelesai(int idData) {
        this.StatusSelesai = true;
        String sql = "UPDATE tabel_pertemuan SET StatusSelesai = true WHERE id = ?";
        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idData);
            pst.executeUpdate();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    public boolean simpanKeDatabase(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            sql = "INSERT INTO tabel_pertemuan (MataKuliah, Deskripsi, Lokasi, Sesi, Deadline, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_pertemuan SET MataKuliah = ?, Deskripsi = ?, Lokasi = ?, Sesi = ?, Deadline = ?, Prioritas = ? WHERE id = ?";
        }

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setString(3, this.Lokasi);
            pst.setString(4, this.Sesi);

            if (this.Deadline != null) {
                pst.setTimestamp(5, new java.sql.Timestamp(this.Deadline.getTime()));
            } else {
                pst.setNull(5, java.sql.Types.TIMESTAMP);
            }

            pst.setBoolean(6, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(7, false);
                pst.setString(8, username);
            } else {
                pst.setInt(7, idData);
            }

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Pertemuan: " + e.getMessage());
            return false;
        }
    }
}
