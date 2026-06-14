/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.util;

/**
 *
 * @author Ajax
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/db_agendaku";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getKoneksi() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Koneksi Berhasil!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
        }
        return conn;
    }
}
