/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agenda.model;
/**
 *
 * @author Ajax
 */
import java.util.Date;

public abstract class agenda {
    public Date Deadline;
    public boolean Prioritas;
    public String Deskripsi;
    public boolean StatusSelesai;

    public void tandaiSelesai() {
        this.StatusSelesai = true;
    }

    public boolean cekBentrok(Date DeadlineBaru) {
        return this.Deadline.equals(DeadlineBaru);
    }
}
