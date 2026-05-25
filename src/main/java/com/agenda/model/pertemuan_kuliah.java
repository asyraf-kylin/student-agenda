/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

/**
 *
 * @author Ajax
 */
public class pertemuan_kuliah extends agenda {
    String Lokasi;
    String MataKuliah;

    public void tambahPertemuan() {
        System.out.println("Pertemuan matkul " + MataKuliah + " di " + Lokasi + " dicatat.");
    }
}
