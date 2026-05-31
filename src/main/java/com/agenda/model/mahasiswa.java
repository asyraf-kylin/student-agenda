/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

import com.agenda.util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class mahasiswa {
    public String Nama;
    public String Password;
    
    public mahasiswa(String Nama) {
        this.Nama = Nama;
    }
    
    public mahasiswa() {
    }

    public boolean loginDatabase(String username, String pass) {
        String sql = "SELECT * FROM mahasiswa WHERE nama=? AND password=?";
        
        try (Connection conn = koneksi.getKoneksi(); // Pakai getKoneksi() biar sinkron dengan utilitasmu
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

    // ========================================================
    // REFAKTOR POLIMORFISME: Hapus Data Agenda Apapun Dari Sini
    // ========================================================
    public boolean hapusAgenda(agenda data) {
        String namaTabel = "";
        if (data instanceof tugas_kuliah) namaTabel = "tabel_tugas";
        else if (data instanceof pertemuan_kuliah) namaTabel = "tabel_pertemuan";
        else if (data instanceof kegiatan_ukm) namaTabel = "tabel_kegiatan";
        else if (data instanceof belajar_mandiri) namaTabel = "tabel_belajar";

        String sql = "DELETE FROM " + namaTabel + " WHERE id = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, data.id);
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
        System.out.println(this.Nama + " telah keluar dari AgendaKu.");
    }
}