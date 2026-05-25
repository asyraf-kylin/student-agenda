/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

/**
 *
 * @author Ajax
 */
public class belajar_mandiri extends agenda {
    public String MataKuliah;
    public String Materi;
    public int Durasi;

    public void tambahMateri() {
        System.out.println("Materi " + Materi + " ditambahkan ke list belajar.");
    }

    public void catatProgress() {
        System.out.println("Progress belajar " + MataKuliah + " tercatat " + Durasi + " menit.");
    }
}
