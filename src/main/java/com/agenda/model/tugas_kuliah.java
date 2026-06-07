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

    @Override
    public void tandaiSelesai(int idData) {
        this.StatusSelesai = true;
        String sql = "UPDATE tabel_tugas SET StatusSelesai = true WHERE id = ?";
        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idData);
            pst.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("Error Tandai Selesai Tugas: " + e.getMessage()); 
        }
    }

    public int estimasiWaktu() {
        if (this.StatusSelesai) {
            return 0;
        }
        
        if (this.Deadline == null) {
            return 0;
        }

        try {
            java.time.LocalDate hariIni = java.time.LocalDate.now();

            java.time.LocalDate tanggalDeadline = new java.util.Date(this.Deadline.getTime())
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            long selisihHari = java.time.temporal.ChronoUnit.DAYS.between(hariIni, tanggalDeadline);

            return (int) selisihHari;

        } catch (Exception e) {
            return -999;
        }
    }

    public boolean tambahTugas(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            sql = "INSERT INTO tabel_tugas (MataKuliah, Deskripsi, Deadline, Bobot, TingkatKesulitan, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_tugas SET MataKuliah = ?, Deskripsi = ?, Deadline = ?, Bobot = ?, TingkatKesulitan = ?, Prioritas = ? WHERE id = ?";
        }

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);

            if (this.Deadline != null) {
                pst.setTimestamp(3, new java.sql.Timestamp(this.Deadline.getTime()));
            } else {
                pst.setNull(3, java.sql.Types.TIMESTAMP);
            }

            pst.setInt(4, this.Bobot);
            pst.setString(5, this.TingkatKesulitan);
            pst.setBoolean(6, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(7, false);
                pst.setString(8, username);
            } else {
                pst.setInt(7, idData);
            }

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Tugas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}