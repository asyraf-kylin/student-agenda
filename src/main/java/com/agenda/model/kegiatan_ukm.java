/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

/**
 *
 * @author Ajax
 */
public class kegiatan_ukm extends agenda {
    public String NamaKegiatan;
    public String Lokasi;
    public String[] Anggota;

    public void tambahKegiatan() {
        System.out.println("Kegiatan UKM: " + NamaKegiatan + " siap dilaksanakan.");
    }

    public String[] tampilkanAnggota() {
        return Anggota;
    }
}