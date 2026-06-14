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

    public boolean tambahBelajar(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            sql = "INSERT INTO tabel_belajar (MataKuliah, Materi, Durasi, Deskripsi, Deadline, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_belajar SET MataKuliah = ?, Materi = ?, Durasi = ?, Deskripsi = ?, Deadline = ?, Prioritas = ? WHERE id = ?";
        }

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Materi);
            pst.setInt(3, this.Durasi);
            pst.setString(4, this.Deskripsi);

            if (this.Deadline != null) {
                pst.setDate(5, new java.sql.Date(this.Deadline.getTime()));
            } else {
                pst.setNull(5, java.sql.Types.DATE);
            }

            pst.setBoolean(6, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(7, false);
                pst.setString(8, username);
            } else {
                pst.setInt(7, idData);
            }

            return pst.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error Simpan Belajar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void tandaiSelesai(int idData) {
        this.StatusSelesai = true;
        String sql = "UPDATE tabel_belajar SET StatusSelesai = true WHERE id = ?";
        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idData);
            pst.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("Error Tandai Selesai Belajar: " + e.getMessage()); 
        }
    }
}