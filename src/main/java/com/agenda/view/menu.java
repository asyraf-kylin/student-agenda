/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.agenda.view;

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
    this.setTitle("AgendaKu - Aplikasi Manajemen Agenda");
    
    loadTabelPertemuan();
    loadTabelTugas();
    
        tblPertemuan.getModel().addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    updateStatistikPertemuan();
                }
            }
        });
    
        tblTugas.getModel().addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    updateStatistikPertemuan();
                }
            }
        });
    }

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
        lblPertemuanBelum.setText(String.valueOf(belumSelesai));
        lblPertemuanSelesai.setText(String.valueOf(selesai));
    }
    
    public void updateStatistikTugas() {
        DefaultTableModel model = (DefaultTableModel) tblTugas.getModel();
        int total = model.getRowCount();
        int selesai = 0;
        int belumSelesai = 0;

        int kolomStatusIdx = 7;

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

        totTugas.setText(String.valueOf(total));
        lblTugasBelum.setText(String.valueOf(belumSelesai));
        lblTugasSelesai.setText(String.valueOf(selesai));
    }
    
    public javax.swing.JTable getTblTugas() {
    return tblTugas;
    }
    
    public javax.swing.JTable getTblPertemuan() {
    return this.tblPertemuan;
    }
    
    public void loadTabelTugas() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mata Kuliah", "Tingkat Kesulitan", "Bobot Tugas", "Deskripsi", "Estimasi", "Deadline", "Prioritas", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // PERBAIKAN 1: Indeks 4 (Estimasi) dihapus dari sini karena sekarang berisi String (teks), bukan Integer.
                // Jadi hanya indeks 2 (Bobot) yang mengembalikan Integer.class
                if (columnIndex == 2) {
                    return Integer.class;
                }
                if (columnIndex == 6 || columnIndex == 7) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        tblTugas.setModel(model);
        model.setRowCount(0);

        String sql = "SELECT id, MataKuliah, TingkatKesulitan, Bobot, Deskripsi, Deadline, Prioritas, StatusSelesai FROM tabel_tugas WHERE username = ?";

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, currentUser);

            try (java.sql.ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String matkul = rs.getString("MataKuliah");
                    String kesulitan = rs.getString("TingkatKesulitan");
                    int bobot = rs.getInt("Bobot");
                    String bobotStr = (bobot == 0) ? "-" : String.valueOf(bobot);
                    String deskripsi = rs.getString("Deskripsi");

                    boolean statusSelesai = rs.getBoolean("StatusSelesai");
                    java.sql.Timestamp deadlineTimestamp = rs.getTimestamp("Deadline");

                    // 1. Buat Objek Tugas Kuliah
                    com.agenda.model.tugas_kuliah tempTugas = new com.agenda.model.tugas_kuliah();
                    tempTugas.Deadline = deadlineTimestamp;
                    tempTugas.StatusSelesai = statusSelesai; // Masukkan status dari database ke objek

                    // 2. Panggil fungsi bawaan UML (menghasilkan angka int)
                    int sisaHari = tempTugas.estimasiWaktu();

                    // 3. MANIPULASI TAMPILAN: Ubah hasil int menjadi String teks untuk JTable
                    String estimasiStr;

                    if (statusSelesai) {
                        estimasiStr = "-";
                    } else if (deadlineTimestamp == null || sisaHari == -999) {
                        estimasiStr = "-";
                    } else if (sisaHari > 0) {
                        estimasiStr = sisaHari + " Hari Lagi";
                    } else if (sisaHari == 0) {
                        estimasiStr = "Hari Ini!";
                    } else {
                        // Hilangkan tanda minus untuk teks terlambat
                        estimasiStr = "Terlambat " + Math.abs(sisaHari) + " Hari";
                    }

                    // Format Tanggal untuk JTable
                    String tanggalTerformat = (deadlineTimestamp != null)
                            ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(deadlineTimestamp.getTime())) : "-";

                    boolean prioritas = rs.getBoolean("Prioritas");

                    // 4. Masukkan string hasil manipulasi (estimasiStr) ke model tabel
                    model.addRow(new Object[]{
                        matkul, // 0
                        kesulitan, // 1
                        bobotStr, // 2
                        deskripsi, // 3
                        estimasiStr, // 4 -> Di sini aman menerima String karena variabel di GUI sudah dikonversi
                        tanggalTerformat, // 5
                        prioritas, // 6
                        statusSelesai // 7
                    });

                    // Taktik GUI: Simpan ID data database di properti baris komponen tblTugas
                    int rowCount = model.getRowCount() - 1;
                    tblTugas.putClientProperty("id_row_" + rowCount, id);
                }

                // Update label statistik untuk Tugas
                updateStatistikTugas();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error Load Tabel Tugas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void loadTabelPertemuan() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mata Kuliah", "Lokasi", "Deskripsi", "Sesi", "Tanggal (Deadline)","Prioritas", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tetap tidak bisa di-inline edit
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Kolom Prioritas adalah indeks 5, Kolom Status adalah indeks 6
                if (columnIndex == 5 || columnIndex == 6) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        tblPertemuan.setModel(model);
        model.setRowCount(0);

        String sql = "SELECT id, MataKuliah, Deskripsi, Lokasi, Sesi, Deadline, Prioritas, StatusSelesai FROM tabel_pertemuan WHERE username = ?";

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, currentUser);

            try (java.sql.ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String matkul = rs.getString("MataKuliah");
                    String deskripsi = rs.getString("Deskripsi");
                    String lokasi = rs.getString("Lokasi");
                    String sesi = (rs.getString("Sesi") == null) ? "-" : rs.getString("Sesi");

                    // Format Tanggal
                    java.sql.Timestamp deadlineTimestamp = rs.getTimestamp("Deadline");
                    String tanggalTerformat = (deadlineTimestamp != null)
                            ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(deadlineTimestamp.getTime())) : "-";

                    boolean prioritas = rs.getBoolean("Prioritas");
                    boolean statusSelesai = rs.getBoolean("StatusSelesai");

                    // MASUKKAN KE TABEL (Urutan harus sesuai dengan header di atas)
                    model.addRow(new Object[]{
                        matkul, // 0
                        lokasi, // 1
                        deskripsi, // 2
                        sesi, // 3
                        tanggalTerformat, // 4
                        prioritas, // 5 
                        statusSelesai // 6 
                    });

                    // Taktik GUI: Simpan ID di properti baris
                    int rowCount = model.getRowCount() - 1;
                    tblPertemuan.putClientProperty("id_row_" + rowCount, id);
                }

                updateStatistikPertemuan();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error Load Tabel Pertemuan: " + e.getMessage());
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
        lblTugasSelesai = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTugasBelum = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        totTugas = new javax.swing.JLabel();
        TambahTugas = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTugas = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanelTugasUtama1 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblPertemuanSelesai = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblPertemuanBelum = new javax.swing.JLabel();
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
        btnLogout = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Sudah Selesai:");

        lblTugasSelesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblTugasSelesai.setText("0");

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
                .addComponent(lblTugasSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(lblTugasSelesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Belum Selesai:");

        lblTugasBelum.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblTugasBelum.setText("0");

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
                        .addComponent(lblTugasBelum, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addComponent(lblTugasBelum)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("Total Tugas:");

        totTugas.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        totTugas.setText("0");

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
                .addComponent(totTugas, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addComponent(totTugas)
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

        lblPertemuanSelesai.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblPertemuanSelesai.setText("0");

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
                .addComponent(lblPertemuanSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(26, 26, 26)
                .addComponent(lblPertemuanSelesai)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("Belum Selesai:");

        lblPertemuanBelum.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblPertemuanBelum.setText("0");

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
                        .addComponent(lblPertemuanBelum, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(26, 26, 26)
                .addComponent(lblPertemuanBelum)
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
                "Mata Kuliah", "Lokasi", "Deskripsi", "Sesi", "Tanggal (Deadline)", "Prioritas", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
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

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        btnExport.setText("Export");
        btnExport.addActionListener(this::btnExportActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(labelwelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addGap(34, 34, 34)
                .addComponent(labeltime)
                .addGap(30, 30, 30))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labeltime)
                    .addComponent(labelwelcome)
                    .addComponent(btnLogout)
                    .addComponent(btnExport))
                .addGap(14, 14, 14)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void TambahPembelajaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahPembelajaranActionPerformed
        
    }//GEN-LAST:event_TambahPembelajaranActionPerformed

    private void TambahKegiatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahKegiatanActionPerformed
        
    }//GEN-LAST:event_TambahKegiatanActionPerformed

    private void TambahPertemuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahPertemuanActionPerformed
        new pertemuan(this, null, -1).setVisible(true);
        
    }//GEN-LAST:event_TambahPertemuanActionPerformed

    private void TambahTugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahTugasActionPerformed
        new tugas(this, null, -1).setVisible(true);
    }//GEN-LAST:event_TambahTugasActionPerformed

    private void tblTugasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTugasMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            int row = tblTugas.getSelectedRow();
            if (row == -1) {
                return;
            }

            // Taktik GUI: Ambil ID database yang kita titipkan tadi di baris JTable
            Object idProp = tblTugas.getClientProperty("id_row_" + row);
            if (idProp == null) {
                return;
            }
            int idTerpilih = (int) idProp;

            com.agenda.model.tugas_kuliah tugasObj = null;
            // Tarik data paling *fresh* langsung dari database menggunakan ID
            String sql = "SELECT * FROM tabel_tugas WHERE id = ? AND username = ?";

            try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, idTerpilih);
                pst.setString(2, currentUser); // Menggunakan variabel session aktif di menu.java

                try (java.sql.ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        tugasObj = new com.agenda.model.tugas_kuliah();

                        // JANGAN ISI ID KE MODEL (karena ID dikelola terpisah di GUI)
                        tugasObj.MataKuliah = rs.getString("MataKuliah");
                        tugasObj.Deskripsi = rs.getString("Deskripsi");
                        tugasObj.Deadline = rs.getTimestamp("Deadline");

                        // Properti khusus tugas_kuliah
                        tugasObj.TingkatKesulitan = rs.getString("TingkatKesulitan");
                        tugasObj.Bobot = rs.getInt("Bobot"); // Menggunakan nama kolom 'Bobot' yang sudah kita perbaiki

                        tugasObj.Prioritas = rs.getBoolean("Prioritas");
                        tugasObj.StatusSelesai = rs.getBoolean("StatusSelesai");
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Error Ambil Detail Tugas: " + e.getMessage());
            }

            // PANGGIL AKSIPENGGUNA dengan 3 parameter (Induk, Model, ID)
            if (tugasObj != null) {
                new com.agenda.view.aksipengguna(this, tugasObj, idTerpilih).setVisible(true);
            }
        }
    }//GEN-LAST:event_tblTugasMouseClicked

    private void tblPertemuanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPertemuanMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

        int row = tblPertemuan.getSelectedRow();
        if (row == -1) {
            return;
        }

        // Taktik GUI: Ambil ID database yang kita titipkan tadi
        Object idProp = tblPertemuan.getClientProperty("id_row_" + row);
        if (idProp == null) {
            return;
        }
        int idTerpilih = (int) idProp;

        com.agenda.model.pertemuan_kuliah pertemuan = null;
        String sql = "SELECT * FROM tabel_pertemuan WHERE id = ? AND username = ?";

        try (java.sql.Connection conn = com.agenda.util.koneksi.getKoneksi(); 
             java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idTerpilih);
            pst.setString(2, currentUser);

            try (java.sql.ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    pertemuan = new com.agenda.model.pertemuan_kuliah();
                    // JANGAN ISI ID KE MODEL (karena ID tidak ada di UML/Model)
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

        // PANGGIL AKSIPENGGUNA dengan 3 parameter (Induk, Model, ID)
        if (pertemuan != null) {
            new com.agenda.view.aksipengguna(this, pertemuan, idTerpilih).setVisible(true);
            }
        }
    }//GEN-LAST:event_tblPertemuanMouseClicked

    private void tblKegiatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKegiatanMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblKegiatanMouseClicked

    private void tblBelajarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBelajarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblBelajarMouseClicked

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        int konfirmasi = javax.swing.JOptionPane.showConfirmDialog(this, 
        "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", 
            javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi == javax.swing.JOptionPane.YES_OPTION) {
            com.agenda.model.mahasiswa userAktif = new com.agenda.model.mahasiswa(this.currentUser);

            userAktif.logout();

            new login().setVisible(true);

            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        com.agenda.model.mahasiswa.modeEkspor = jTabbedPane1.getSelectedIndex();
        com.agenda.model.mahasiswa mhs = new com.agenda.model.mahasiswa();
        mhs.Nama = currentUser;
        mhs.simpanCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    
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
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnLogout;
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
    private javax.swing.JLabel jLabelBSelesai1;
    private javax.swing.JLabel jLabelBSelesai2;
    private javax.swing.JLabel jLabelSselesai1;
    private javax.swing.JLabel jLabelSselesai2;
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
    private javax.swing.JLabel lblPertemuanBelum;
    private javax.swing.JLabel lblPertemuanSelesai;
    private javax.swing.JLabel lblTugasBelum;
    private javax.swing.JLabel lblTugasSelesai;
    private javax.swing.JTable tblBelajar;
    private javax.swing.JTable tblKegiatan;
    private javax.swing.JTable tblPertemuan;
    private javax.swing.JTable tblTugas;
    private javax.swing.JLabel totPertemuan;
    private javax.swing.JLabel totTugas;
    // End of variables declaration//GEN-END:variables
}
