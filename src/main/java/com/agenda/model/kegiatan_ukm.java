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

    // Helper bawaan untuk merubah Array String menjadi satu string dipisah koma untuk SQL
    public String getAnggotaSebagaiString() {
        if (this.Anggota == null) return "";
        return String.join(", ", this.Anggota);
    }

    // Gantikan simpanKeDatabase dan updateDiDatabase dengan metode tunggal ini
    public boolean tambahKegiatan(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            sql = "INSERT INTO tabel_kegiatan (NamaKegiatan, Deskripsi, Deadline, Lokasi, Anggota, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_kegiatan SET NamaKegiatan = ?, Deskripsi = ?, Deadline = ?, Lokasi = ?, Anggota = ?, Prioritas = ? WHERE id = ?";
        }

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.NamaKegiatan);
            pst.setString(2, this.Deskripsi);

            // Penanganan Deadline (Konsisten dengan model lain)
            if (this.Deadline != null) {
                pst.setDate(3, new java.sql.Date(this.Deadline.getTime()));
            } else {
                pst.setNull(3, java.sql.Types.DATE);
            }

            pst.setString(4, this.Lokasi);
            pst.setString(5, getAnggotaSebagaiString());
            pst.setBoolean(6, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(7, false); // Default StatusSelesai
                pst.setString(8, username);
            } else {
                pst.setInt(7, idData); // Parameter WHERE id = ?
            }

            return pst.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error Simpan Kegiatan: " + e.getMessage());
            return false;
        }
    }

    // Wajib di-override karena menginduk ke kelas abstrak "agenda"
    @Override
    public void tandaiSelesai(int idData) {
        this.StatusSelesai = true;
        String sql = "UPDATE tabel_kegiatan SET StatusSelesai = true WHERE id = ?";
        try (Connection conn = koneksi.getKoneksi(); 
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idData);
            pst.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("Error Tandai Selesai Kegiatan: " + e.getMessage()); 
        }
    }
}