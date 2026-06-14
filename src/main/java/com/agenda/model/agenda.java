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

    public agenda() {
    }

    public abstract void tandaiSelesai(int idData);

    public boolean cekBentrok(Date deadlineBaru) {
        if (this.Deadline == null || deadlineBaru == null) return false;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        return sdf.format(this.Deadline).equals(sdf.format(deadlineBaru));
    }
}
