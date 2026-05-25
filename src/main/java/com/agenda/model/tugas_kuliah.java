/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

/**
 *
 * @author Ajax
 */
public class tugas_kuliah extends agenda {
    public String MataKuliah;
    public String TingkatKesulitan;
    public int Bobot;

    public int estimasiWaktu() {
        return Bobot * 60;
    }

    public void tambahTugas() {
        System.out.println("Tugas " + MataKuliah + " ditambahkan.");
    }
}