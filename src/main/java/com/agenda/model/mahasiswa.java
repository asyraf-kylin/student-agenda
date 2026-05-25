/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

import com.agenda.util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Ajax
 */
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
        
        try (Connection conn = koneksi.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, username);
            pst.setString(2, pass);
            
            try (ResultSet rs = pst.executeQuery()) {
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

    public String getNama() {
        return Nama;
    }

    public void logout() {
        System.out.println(this.Nama + " telah keluar dari AgendaKu.");
    }
}
