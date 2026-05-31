/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;

import java.util.Date;

public abstract class agenda {
    public int id; // Tambahan atribut ID untuk primary key SQL
    public Date Deadline;
    public boolean Prioritas;
    public String Deskripsi;
    public boolean StatusSelesai;

    public void tandaiSelesai() {
        this.StatusSelesai = true;
    }

    public boolean cekBentrok(Date DeadlineBaru) {
        if (this.Deadline == null || DeadlineBaru == null) {
            return false;
        }
        return this.Deadline.equals(DeadlineBaru);
    }
}
