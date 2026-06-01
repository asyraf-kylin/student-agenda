/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.agenda.view;

import com.agenda.util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ajax
 */
public class menu extends javax.swing.JFrame {
    private String currentUser;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(menu.class.getName());
    
    /**
     * Creates new form login
     */
    public menu(String namaUser) {
    this.currentUser = namaUser;
    initComponents();
    this.setLocationRelativeTo(null);
    setAppIcon();
    labelwelcome.setText("Selamat datang di AgendaKu, " + namaUser + " 👋");
    tampilkanWaktu();
    
    loadTabelTugas();
    loadTabelPertemuan();
    loadTabelKegiatan();
    loadTabelBelajar();
    
        tblPertemuan.getModel().addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                // Jalankan hitung ulang statistik jika ada data kolom yang berubah
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    updateStatistikPertemuan();
                }
            }
        });
    }

// Menyediakan akses username aktif untuk form input (addtugas, addpertemuan, dll)
    public String getCurrentUser() {
        return currentUser;
    }
    
    private void setAppIcon() {
        try {
            java.net.URL iconURL = getClass().getResource("/com/agenda/assets/icon.png");
            if (iconURL != null) {
                java.awt.Image icon = new javax.swing.ImageIcon(iconURL).getImage();
                this.setIconImage(icon);
            }
        } catch (Exception e) {
            System.out.println("Ikon tidak ditemukan: " + e.getMessage());
        }
    }
    
    private void tampilkanWaktu() {
    java.util.Timer timer = new java.util.Timer();
    new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            java.util.Locale lokal = new java.util.Locale("id", "ID");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", lokal);
            
            String waktuSekarang = sdf.format(new java.util.Date());
            labeltime.setText(waktuSekarang);
        }
    }).start();
    }
    
    public void updateStatistikPertemuan() {
        DefaultTableModel model = (DefaultTableModel) tblPertemuan.getModel();
        int total = model.getRowCount();
        int selesai = 0;
        int belumSelesai = 0;

        int kolomStatusIdx = 6;

        for (int i = 0; i < total; i++) {
            Object value = model.getValueAt(i, kolomStatusIdx);

            if (value instanceof Boolean) {
                boolean isSelesai = (Boolean) value;
                if (isSelesai) {
                    selesai++;
                } else {
                    belumSelesai++;
                }
            }
        }

        totPertemuan.setText(String.valueOf(total));
        lblBelumSelesai.setText(String.valueOf(belumSelesai));
        lblSelesai.setText(String.valueOf(selesai));
    }
    
    public void loadTabelTugas() {
        DefaultTableModel model = (DefaultTableModel) tblTugas.getModel();
        model.setRowCount(0); // Reset tabel agar tidak menumpuk

        String sql = "SELECT * FROM tabel_tugas WHERE username = ?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql)) {
        
            pst.setString(1, currentUser);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                // 1. Bungkus data relasional database ke dalam Objek OOP
                    com.agenda.model.tugas_kuliah tugas = new com.agenda.model.tugas_kuliah();
                    tugas.id = rs.getInt("id");
                    tugas.MataKuliah = rs.getString("MataKuliah");
                    tugas.Deskripsi = rs.getString("Deskripsi");
                    tugas.Deadline = rs.getTimestamp("Deadline");
                    tugas.Bobot = rs.getInt("Bobot");
                    tugas.TingkatKesulitan = rs.getString("TingkatKesulitan");
                    tugas.Prioritas = rs.getBoolean("Prioritas");
                    tugas.StatusSelesai = rs.getBoolean("StatusSelesai");

                // 2. Tampilkan properti objek ke dalam baris JTable GUI
                    Object[] row = {
                        tugas.Deskripsi,
                        tugas.MataKuliah,
                        tugas.Deadline,
                        tugas.Bobot,
                        tugas.TingkatKesulitan,
                        tugas.Prioritas ? "Tinggi" : "Biasa",
                        tugas.StatusSelesai ? "Selesai" : "Belum Selesai"
                    };
                
                // Menyimpan objek aslinya ke dalam row properties atau bisa diidentifikasi nanti
                    model.addRow(row);
               
                // Trik Menyimpan ID Tersembunyi: Kita bisa manfaatkan custom array penyimpanan jika dibutuhkan, 
                // atau mencarinya kembali berdasarkan deskripsi saat diklik.
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Load Tabel Tugas: " + e.getMessage());
        }
    }
    
    public void loadTabelPertemuan() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mata Kuliah", "Lokasi", "Deskripsi", "Sesi", "Prioritas", "Tanggal (Deadline)", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 🔒 Semua kolom (termasuk checkbox) TIDAK BISA di-inline edit lewat double click
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Supaya kolom Prioritas (4) dan Status (6) tetap muncul sebagai kotak Checkbox (Boolean)
                if (columnIndex == 4 || columnIndex == 6) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        tblPertemuan.setModel(model);
        model.setRowCount(0); // Bersihkan tabel terlebih dahulu

        String sql = "SELECT id, MataKuliah, Deskripsi, Lokasi, Sesi, Deadline, Prioritas, StatusSelesai FROM tabel_pertemuan WHERE username = ?";

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, currentUser);

            try (java.sql.ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {

                    int id = rs.getInt("id");

                    String matkul = rs.getString("MataKuliah");
                    String deskripsi = rs.getString("Deskripsi");
                    String lokasi = rs.getString("Lokasi");
                    String sesi = rs.getString("Sesi");
                    if (sesi == null) {
                        sesi = "-";
                    }

                    // ✔️ PENYESUAIAN: Ambil data DATETIME dari database sebagai Timestamp
                    java.sql.Timestamp deadlineTimestamp = rs.getTimestamp("Deadline");
                    String tanggalTerformat = "-";

                    // ✔️ Jika data di database tidak null, format ke bentuk "dd/MM/yyyy" untuk UI JTable
                    if (deadlineTimestamp != null) {
                        java.text.SimpleDateFormat formatTabel = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        tanggalTerformat = formatTabel.format(new java.util.Date(deadlineTimestamp.getTime()));
                    }

                    boolean prioritas = rs.getBoolean("Prioritas");
                    boolean statusSelesai = rs.getBoolean("StatusSelesai");

                    // MASUKKAN KE TABEL
                    model.addRow(new Object[]{
                        matkul, // Kolom 0: Mata Kuliah
                        lokasi, // Kolom 1: Lokasi
                        deskripsi, // Kolom 2: Deskripsi
                        sesi, // Kolom 4: Sesi
                        prioritas,
                        tanggalTerformat,// Kolom 6: Prioritas
                        statusSelesai // Kolom 7: Status (Checkbox)
                    });

                    int rowCount = model.getRowCount() - 1;
                    tblPertemuan.putClientProperty("id_row_" + rowCount, id);
                }
                
                updateStatistikPertemuan();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error Load Tabel Pertemuan: " + e.getMessage());
        }
    }
    
    public void loadTabelKegiatan() {
        DefaultTableModel model = (DefaultTableModel) tblKegiatan.getModel();
        model.setRowCount(0);

        String sql = "SELECT * FROM tabel_kegiatan WHERE username = ?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql)) {
        
            pst.setString(1, currentUser);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    com.agenda.model.kegiatan_ukm k = new com.agenda.model.kegiatan_ukm();
                    k.id = rs.getInt("id");
                    k.NamaKegiatan = rs.getString("NamaKegiatan");
                    k.Deskripsi = rs.getString("Deskripsi");
                    k.Deadline = rs.getTimestamp("Deadline");
                    k.Lokasi = rs.getString("Lokasi");
                    String dbAnggota = rs.getString("Anggota");
                    k.Anggota = dbAnggota != null ? dbAnggota.split(",\\s*") : new String[0];
                    k.Prioritas = rs.getBoolean("Prioritas");
                    k.StatusSelesai = rs.getBoolean("StatusSelesai");

                    Object[] row = {
                        k.Deskripsi,
                        k.NamaKegiatan,
                        k.Deadline,
                        k.Lokasi,
                        String.join(", ", k.Anggota),
                        k.Prioritas ? "Tinggi" : "Biasa",
                        k.StatusSelesai ? "Selesai" : "Belum Selesai"
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Load Tabel Kegiatan: " + e.getMessage());
        }
    }
    
    public void loadTabelBelajar() {
        DefaultTableModel model = (DefaultTableModel) tblBelajar.getModel();
        model.setRowCount(0);

        String sql = "SELECT * FROM tabel_belajar WHERE username = ?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql)) {
        
            pst.setString(1, currentUser);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    com.agenda.model.belajar_mandiri b = new com.agenda.model.belajar_mandiri();
                    b.id = rs.getInt("id");
                    b.MataKuliah = rs.getString("MataKuliah");
                    b.Materi = rs.getString("Materi");
                    b.Deskripsi = rs.getString("Deskripsi");
                    b.Deadline = rs.getTimestamp("Deadline");
                    b.Durasi = rs.getInt("Durasi");
                    b.Prioritas = rs.getBoolean("Prioritas");
                    b.StatusSelesai = rs.getBoolean("StatusSelesai");

                    Object[] row = {
                        b.Deskripsi,
                        b.MataKuliah,
                        b.Materi,
                        b.Deadline,
                        b.Durasi + " Menit",
                        b.Prioritas ? "Tinggi" : "Biasa",
                        b.StatusSelesai ? "Selesai" : "Belum Selesai"
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Load Tabel Belajar: " + e.getMessage());
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanelTugasUtama = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelSselesai = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelBSelesai = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelToTugas = new javax.swing.JLabel();
        TambahTugas = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTugas = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanelTugasUtama1 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblSelesai = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblBelumSelesai = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        totPertemuan = new javax.swing.JLabel();
        TambahPertemuan = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblPertemuan = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabelSselesai2 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabelBSelesai2 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabelToTugas2 = new javax.swing.JLabel();
        TambahKegiatan = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblKegiatan = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        TambahPembelajaran = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBelajar = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabelSselesai1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabelBSelesai1 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabelToTugas1 = new javax.swing.JLabel();
        labeltime = new javax.swing.JLabel();
        labelwelcome = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Sudah Selesai:");

        jLabelSselesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelSselesai.setText("0");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jLabelSselesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jLabelSselesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Belum Selesai:");

        jLabelBSelesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelBSelesai.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabelBSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addComponent(jLabelBSelesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("Total Tugas:");

        jLabelToTugas.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelToTugas.setText("0");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(jLabelToTugas, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addComponent(jLabelToTugas)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(252, 252, 252)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        TambahTugas.setText("Tambah Tugas +");
        TambahTugas.addActionListener(this::TambahTugasActionPerformed);

        tblTugas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mata Kuliah", "Tingkat Kesulitan", "Bobot Tugas", "Deskripsi", "Estimasi", "Deadline", "Prioritas", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTugas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTugasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTugas);

        javax.swing.GroupLayout jPanelTugasUtamaLayout = new javax.swing.GroupLayout(jPanelTugasUtama);
        jPanelTugasUtama.setLayout(jPanelTugasUtamaLayout);
        jPanelTugasUtamaLayout.setHorizontalGroup(
            jPanelTugasUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addComponent(TambahTugas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelTugasUtamaLayout.setVerticalGroup(
            jPanelTugasUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTugasUtamaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TambahTugas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        TambahTugas.getAccessibleContext().setAccessibleName("Tambah Tugas");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTugasUtama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTugasUtama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Tugas", jPanel2);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("Sudah Selesai:");

        lblSelesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblSelesai.setText("0");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(lblSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(26, 26, 26)
                .addComponent(lblSelesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("Belum Selesai:");

        lblBelumSelesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblBelumSelesai.setText("0");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(lblBelumSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(26, 26, 26)
                .addComponent(lblBelumSelesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("Total Pertemuan:");

        totPertemuan.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        totPertemuan.setText("0");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(totPertemuan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(26, 26, 26)
                .addComponent(totPertemuan)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(252, 252, 252)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        TambahPertemuan.setLabel("Tambah Pertemuan +");
        TambahPertemuan.addActionListener(this::TambahPertemuanActionPerformed);

        tblPertemuan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mata Kuliah", "Lokasi", "Deskripsi", "Sesi", "Prioritas", "Tanggal (Deadline)", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPertemuan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPertemuanMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblPertemuan);

        javax.swing.GroupLayout jPanelTugasUtama1Layout = new javax.swing.GroupLayout(jPanelTugasUtama1);
        jPanelTugasUtama1.setLayout(jPanelTugasUtama1Layout);
        jPanelTugasUtama1Layout.setHorizontalGroup(
            jPanelTugasUtama1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(TambahPertemuan, javax.swing.GroupLayout.DEFAULT_SIZE, 1173, Short.MAX_VALUE)
            .addComponent(jScrollPane6)
        );
        jPanelTugasUtama1Layout.setVerticalGroup(
            jPanelTugasUtama1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTugasUtama1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TambahPertemuan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTugasUtama1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTugasUtama1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Pertemuan", jPanel3);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("Sudah Selesai:");

        jLabelSselesai2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelSselesai2.setText("0");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jLabelSselesai2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(26, 26, 26)
                .addComponent(jLabelSselesai2)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setText("Belum Selesai:");

        jLabelBSelesai2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelBSelesai2.setText("0");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabelBSelesai2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(26, 26, 26)
                .addComponent(jLabelBSelesai2)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("Total Kegiatan:");

        jLabelToTugas2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelToTugas2.setText("0");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(jLabelToTugas2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(26, 26, 26)
                .addComponent(jLabelToTugas2)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(252, 252, 252)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        TambahKegiatan.setText("Tambah Kegiatan +");
        TambahKegiatan.addActionListener(this::TambahKegiatanActionPerformed);

        tblKegiatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kegiatan", "Lokasi", "Anggota", "Deskripsi", "Tanggal", "Jam", "Deadline", "Prioritas", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKegiatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKegiatanMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblKegiatan);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(TambahKegiatan, javax.swing.GroupLayout.DEFAULT_SIZE, 1173, Short.MAX_VALUE)
            .addComponent(jScrollPane5)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TambahKegiatan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Kegiatan", jPanel4);

        TambahPembelajaran.setText("Tambah Pembelajaran +");
        TambahPembelajaran.addActionListener(this::TambahPembelajaranActionPerformed);

        tblBelajar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mata Kuliah", "Materi", "Durasi", "Deskripsi", "Tanggal", "Jam", "Deadline", "Prioritas", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBelajar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBelajarMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblBelajar);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("Sudah Selesai:");

        jLabelSselesai1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelSselesai1.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jLabelSselesai1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(26, 26, 26)
                .addComponent(jLabelSselesai1)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("Belum Selesai:");

        jLabelBSelesai1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelBSelesai1.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabelBSelesai1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(26, 26, 26)
                .addComponent(jLabelBSelesai1)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Total Pembelajaran:");

        jLabelToTugas1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabelToTugas1.setText("0");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(jLabelToTugas1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(26, 26, 26)
                .addComponent(jLabelToTugas1)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(252, 252, 252)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addComponent(TambahPembelajaran, javax.swing.GroupLayout.DEFAULT_SIZE, 1173, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TambahPembelajaran, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Belajar", jPanel5);

        labeltime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labeltime.setText("DateTime");

        labelwelcome.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelwelcome.setText("AgendaKu");

        jButton1.setText("jButton1");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(labelwelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(34, 34, 34)
                .addComponent(labeltime)
                .addGap(30, 30, 30))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jButton1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labeltime)
                    .addComponent(labelwelcome)
                    .addComponent(btnLogout))
                .addGap(14, 14, 14)
                .addComponent(jTabbedPane1))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jButton1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void TambahPembelajaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahPembelajaranActionPerformed
        new addbelajar(this, null).setVisible(true);
    }//GEN-LAST:event_TambahPembelajaranActionPerformed

    private void TambahKegiatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahKegiatanActionPerformed
        new addkegiatan(this, null).setVisible(true);
    }//GEN-LAST:event_TambahKegiatanActionPerformed

    private void TambahPertemuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahPertemuanActionPerformed
        new addpertemuan(this, null).setVisible(true);
        
    }//GEN-LAST:event_TambahPertemuanActionPerformed

    private void TambahTugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahTugasActionPerformed
        new addtugas(this, null).setVisible(true);
    }//GEN-LAST:event_TambahTugasActionPerformed

    private void tblTugasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTugasMouseClicked
        // TODO add your handling code here:
        int row = tblTugas.getSelectedRow();
        if (row == -1) return;

        String deskripsiTerpilih = tblTugas.getValueAt(row, 0).toString();

    // Cari data lengkap dari database lalu bungkus ke dalam objek OOP
        com.agenda.model.tugas_kuliah tugas = null;
        String sql = "SELECT * FROM tabel_tugas WHERE Deskripsi = ? AND username = ?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, deskripsiTerpilih);
            pst.setString(2, currentUser);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    tugas = new com.agenda.model.tugas_kuliah();
                    tugas.id = rs.getInt("id");
                    tugas.MataKuliah = rs.getString("MataKuliah");
                    tugas.Deskripsi = rs.getString("Deskripsi");
                    tugas.Deadline = rs.getTimestamp("Deadline");
                    tugas.Bobot = rs.getInt("Bobot");
                    tugas.TingkatKesulitan = rs.getString("TingkatKesulitan");
                    tugas.Prioritas = rs.getBoolean("Prioritas");
                    tugas.StatusSelesai = rs.getBoolean("StatusSelesai");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Ambil Detail Tugas: " + e.getMessage());
        }

        if (tugas != null) {
        // Buka form aksipengguna dengan melempar Objek OOP hasil bentukan tadi
            new aksipengguna(this, tugas).setVisible(true);
        }
    }//GEN-LAST:event_tblTugasMouseClicked

    private void tblPertemuanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPertemuanMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            int row = tblPertemuan.getSelectedRow();
            if (row == -1) {
                return;
            }

            // Ambil ID database dari baris yang di-double click
            Object idProp = tblPertemuan.getClientProperty("id_row_" + row);
            if (idProp == null) {
                return;
            }
            int idTerpilih = (int) idProp;

            com.agenda.model.pertemuan_kuliah pertemuan = null;
            String sql = "SELECT * FROM tabel_pertemuan WHERE id = ? AND username = ?";

            try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, idTerpilih);
                pst.setString(2, currentUser);

                try (java.sql.ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        pertemuan = new com.agenda.model.pertemuan_kuliah();
                        pertemuan.id = rs.getInt("id");
                        pertemuan.MataKuliah = rs.getString("MataKuliah");
                        pertemuan.Deskripsi = rs.getString("Deskripsi");
                        pertemuan.Deadline = rs.getTimestamp("Deadline");
                        pertemuan.Lokasi = rs.getString("Lokasi");
                        pertemuan.Sesi = rs.getString("Sesi");
                        pertemuan.Prioritas = rs.getBoolean("Prioritas");
                        pertemuan.StatusSelesai = rs.getBoolean("StatusSelesai");
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Error Ambil Detail Pertemuan: " + e.getMessage());
            }

            // Buka frame aksipengguna
            if (pertemuan != null) {
                new com.agenda.view.aksipengguna(this, pertemuan).setVisible(true);
            }
        }
    }//GEN-LAST:event_tblPertemuanMouseClicked

    private void tblKegiatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKegiatanMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            // 🛠️ PERBAIKAN: Ambil baris yang dipilih dari tblKegiatan (Bukan tblPertemuan)
            int row = tblKegiatan.getSelectedRow();
            if (row == -1) {
                return;
            }

            // 🛠️ PERBAIKAN: Ambil properti ID dari tblKegiatan (Bukan tblPertemuan)
            Object idProp = tblKegiatan.getClientProperty("id_row_" + row);
            if (idProp == null) {
                return;
            }
            int idTerpilih = (int) idProp;

            com.agenda.model.pertemuan_kuliah pertemuan = null;

            String sql = "SELECT * FROM tabel_pertemuan WHERE id = ? AND username = ?";

            try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, idTerpilih);
                pst.setString(2, currentUser);

                try (java.sql.ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        pertemuan = new com.agenda.model.pertemuan_kuliah();
                        pertemuan.id = rs.getInt("id");
                        pertemuan.MataKuliah = rs.getString("MataKuliah");
                        pertemuan.Deskripsi = rs.getString("Deskripsi");
                        pertemuan.Deadline = rs.getTimestamp("Deadline");
                        pertemuan.Lokasi = rs.getString("Lokasi");
                        pertemuan.Sesi = rs.getString("Sesi");
                        pertemuan.Prioritas = rs.getBoolean("Prioritas");
                        pertemuan.StatusSelesai = rs.getBoolean("StatusSelesai");
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Error Ambil Detail Pertemuan: " + e.getMessage());
            }

            if (pertemuan != null) {
                // Buka frame aksipengguna dan kirim data objeknya
                new com.agenda.view.aksipengguna(this, pertemuan).setVisible(true);
            }
        }
    }//GEN-LAST:event_tblKegiatanMouseClicked

    private void tblBelajarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBelajarMouseClicked
        // TODO add your handling code here:
        int row = tblBelajar.getSelectedRow();
        if (row == -1) {
            return;
        }

        String deskripsiTerpilih = tblBelajar.getValueAt(row, 0).toString();

        com.agenda.model.belajar_mandiri belajar = null;
        String sql = "SELECT * FROM tabel_belajar WHERE Deskripsi = ? AND username = ?";

        try (Connection conn = com.agenda.util.koneksi.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, deskripsiTerpilih); // Atur parameter WHERE deskripsi
            pst.setString(2, currentUser);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    belajar = new com.agenda.model.belajar_mandiri();
                    belajar.id = rs.getInt("id");
                    belajar.MataKuliah = rs.getString("MataKuliah");
                    belajar.Materi = rs.getString("Materi");
                    belajar.Deskripsi = rs.getString("Deskripsi");
                    belajar.Deadline = rs.getTimestamp("Deadline");
                    belajar.Durasi = rs.getInt("Durasi");
                    belajar.Prioritas = rs.getBoolean("Prioritas");
                    belajar.StatusSelesai = rs.getBoolean("StatusSelesai");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Ambil Detail Belajar Mandiri: " + e.getMessage());
        }

        if (belajar != null) {
            // Buka form aksipengguna dan lempar objek belajar mandiri
            new aksipengguna(this, belajar).setVisible(true);
        }
    }//GEN-LAST:event_tblBelajarMouseClicked

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        // Tampilkan konfirmasi pilihan biar tidak sengaja tertekan
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin keluar dari dashboard?",
                "Konfirmasi Logout",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            // 1. Buka kembali frame Login
            login loginForm = new login(); // Sesuaikan dengan nama class login.java kamu (perhatikan huruf kapitalnya)
            loginForm.setVisible(true);

            // 2. Tutup frame Menu Utama saat ini
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    
    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {                                         
    // Menutup jendela JDialog
        this.dispose();
    }
        
    private void btnbatalActionPerformed(java.awt.event.ActionEvent evt) {                                         
    // Menutup jendela JDialog
        this.dispose();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
        // Cara panggil yang lebih aman (Generic FlatLaf)
            com.formdev.flatlaf.FlatLightLaf.setup();
        
        // Atau jika ingin tetap Cupertino, pastikan penulisan case-sensitive nya benar:
        // com.formdev.flatlaf.themes.FlatCupertinoLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Gagal memuat FlatLaf");
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new menu("Tester").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton TambahKegiatan;
    private javax.swing.JButton TambahPembelajaran;
    private javax.swing.JButton TambahPertemuan;
    private javax.swing.JButton TambahTugas;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBSelesai;
    private javax.swing.JLabel jLabelBSelesai1;
    private javax.swing.JLabel jLabelBSelesai2;
    private javax.swing.JLabel jLabelSselesai;
    private javax.swing.JLabel jLabelSselesai1;
    private javax.swing.JLabel jLabelSselesai2;
    private javax.swing.JLabel jLabelToTugas;
    private javax.swing.JLabel jLabelToTugas1;
    private javax.swing.JLabel jLabelToTugas2;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelTugasUtama;
    private javax.swing.JPanel jPanelTugasUtama1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labeltime;
    private javax.swing.JLabel labelwelcome;
    private javax.swing.JLabel lblBelumSelesai;
    private javax.swing.JLabel lblSelesai;
    private javax.swing.JTable tblBelajar;
    private javax.swing.JTable tblKegiatan;
    private javax.swing.JTable tblPertemuan;
    private javax.swing.JTable tblTugas;
    private javax.swing.JLabel totPertemuan;
    // End of variables declaration//GEN-END:variables
}
