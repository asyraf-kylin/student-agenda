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

    // ✔️ DIUBAH: Menggunakan java.util.Date agar selaras dengan addpertemuan.java
    // Catatan: Jika di superclass 'agenda' sudah ada properti 'Deadline' dengan nama yang sama,
    // kamu bisa hapus baris di bawah ini agar memanfaatkan konsep inheritance (pewarisan) murni.
    public java.util.Date Deadline;

    // ====================================================================
    // SATU METHOD UTAMA UNTUK WRITE (INSERT / UPDATE) - SELESAI & RAPI
    // ====================================================================
    public boolean simpanKeDatabase(boolean isEdit, String username, int idData) {
        String sql;
        if (!isEdit) {
            // Kita masukkan Sesi dan Deadline ke dalam database
            sql = "INSERT INTO tabel_pertemuan (MataKuliah, Deskripsi, Lokasi, Sesi, Deadline, Prioritas, StatusSelesai, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tabel_pertemuan SET MataKuliah = ?, Deskripsi = ?, Lokasi = ?, Sesi = ?, Deadline = ?, Prioritas = ? WHERE id = ?";
        }

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {

            // Atribut diambil langsung dari object OOP ini sendiri (this)
            pst.setString(1, this.MataKuliah);
            pst.setString(2, this.Deskripsi);
            pst.setString(3, this.Lokasi);
            pst.setString(4, this.Sesi);

            // ✔️ DIUBAH: Konversi java.util.Date ke java.sql.Timestamp untuk tipe DATETIME database MySQL
            if (this.Deadline != null) {
                pst.setTimestamp(5, new java.sql.Timestamp(this.Deadline.getTime()));
            } else {
                pst.setNull(5, java.sql.Types.TIMESTAMP);
            }

            pst.setBoolean(6, this.Prioritas);

            if (!isEdit) {
                pst.setBoolean(7, false);     // StatusSelesai default false saat tambah data baru
                pst.setString(8, username);
            } else {
                pst.setInt(7, idData);        // WHERE id = ? untuk proses update
            }

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Simpan Pertemuan (OOP): " + e.getMessage());
            return false;
        }
    }
}
