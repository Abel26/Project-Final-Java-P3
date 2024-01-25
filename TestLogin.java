import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;  
import java.util.Calendar;  


public class TestLogin {

    private static final String JDBC_URL = "jdbc:sqlite:toko.db";

    public static void main(String[] args) throws Exception{
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            // Untuk mendaftarkan driver JDBC untuk SQLite. Ini perlu dilakukan sebelum membuat koneksi
            Class.forName("org.sqlite.JDBC");
            // Membuat objek
            Statement stat = connection.createStatement();
            PreparedStatement prep = connection.prepareStatement("insert into pelanggan values (?, ?, ? );");
            Scanner terminalInput = new Scanner(System.in);
            //ID Transaksi
            String uniqueCode = generateUniqueCode();
            
            boolean isLanjutkan = true;
            while(isLanjutkan){
                System.out.print("Id Sales: ");
                String id_sales = terminalInput.nextLine();
    
                System.out.print("Nama Sales: ");
                String nama_sales = terminalInput.nextLine();
    
                if (checkLogin(connection, id_sales, nama_sales)) {
                    System.out.println("Login berhasil");
                    
                    System.out.println("");
                    
                    System.out.print("Ingin berbelanja online / offline ? (online / offline): ");
                    String pertanyaan = terminalInput.nextLine();
                    
                    if(pertanyaan.equalsIgnoreCase("online") && pertanyaan.equalsIgnoreCase("online")){
                        // Input data pelanggan
                    System.out.print("Nama Pelanggan: ");
                    String nama_pelanggan = terminalInput.nextLine();
                    
                    System.out.print("No Hp Aktif: ");
                    String nomor_hp = terminalInput.nextLine();
                    
                    prep.setString(1,nama_pelanggan);
                    prep.setString(2,nomor_hp);
                    
                    System.out.println("\nSILAHKAN INPUT SALES YANG SEDANG BERTUGAS");
                    
                    System.out.print("Id Sales: ");
                    String id_sales2 = terminalInput.nextLine();
        
                    System.out.print("Nama Sales: ");
                    String nama_sales2 = terminalInput.nextLine();
                    
                        if(checkLogin(connection, id_sales2, nama_sales2)){
                            System.out.println("\nSELAMAT BERTUGAS " + nama_sales2);
                            // Input data pemesanan
                            TestLogin instance = new TestLogin();
                            instance.inputDataPemesanan(id_sales2, uniqueCode, nomor_hp);
                            
                            // Cetak Resi
                            cetakResi(uniqueCode);
                        }else{
                            System.out.println("id_sales " + id_sales2 + "Tidak terdaftar");
                        }
                      
                    }else{
                        System.out.println("Inputan salah!!!");
                    }
                                    
                } else {
                    System.out.println("Id/Nama salah. Login gagal.");
                }  
                    
                    
                isLanjutkan = getYesorNo("Input ulang? (y/n): ");
            }
            connection.setAutoCommit(false);
            prep.executeBatch();
            connection.setAutoCommit(true);
                    
            // Menutup ResultSet, Statement, dan Koneksi
            // test.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method Cetak Resi 
     * 
     */
    
    public static void cetakResi(String id_group_transaksi)throws Exception{
        Date date = Calendar.getInstance().getTime();  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
        String strDate = dateFormat.format(date);  
        
        String nama_pelanggan= "";
        String no_hp_pelanggan = "";
        String nama_kasir = "";
        String biaya_pengiriman = "";
        String total_bayar = "";
        String nama_pembayaran = "";
        String query = "select b.nama_pelanggan,b.nomor_hp,c.nama_sales,a.biaya_pengiriman,d.nama_pembayaran from kasir a join pelanggan b on a.id_pelanggan = b.id_pelanggan join sales c on a.id_sales = c.id_sales join jenis_pembayaran d on a.id_pembayaran = d.id_pembayaran where a.id_group_transaksi = ?";
        try(Connection connection = DriverManager.getConnection(JDBC_URL)){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, id_group_transaksi);
                 try(ResultSet resultSet = preparedStatement.executeQuery()){
                 if(resultSet.next()){
                     nama_pelanggan = resultSet.getString("nama_pelanggan");
                     no_hp_pelanggan = resultSet.getString("nomor_hp");
                     nama_kasir = resultSet.getString("nama_sales");
                     biaya_pengiriman = resultSet.getString("biaya_pengiriman");
                     total_bayar = resultSet.getString("total_bayar");
                     nama_pembayaran = resultSet.getString("nama_pembayaran");
                 }
             }
            }
        }
        
        System.out.println("                TOKO ABC                    ");
        System.out.println(" Jalan ke bali No 2, Jakarta Selatan 723    ");
        System.out.println("ID Transaksi : "+id_group_transaksi);
        System.out.println("Nama Pelanggan: "+nama_pelanggan);
        System.out.println("No Hp Pelanggan : "+no_hp_pelanggan);
        System.out.println("Tanggal : "+strDate);
        System.out.println("Kasir : "+nama_kasir);
        System.out.println("--------------------------------------------");
        System.out.println(" Cetak Barang / nanti dibenerin             ");
        System.out.println("--------------------------------------------");
        System.out.println("Total Belanja : ");
        System.out.println("Biaya Pengiriman : "+biaya_pengiriman);
        System.out.println("Total Diskon : ");
        System.out.println("Total Pembayaran : "+total_bayar);
        System.out.println("");
        System.out.println("Metode Pembayaran : "+nama_pembayaran);
        System.out.println("");
        System.out.println("  Terimakasih Telat Berbelanja di Toko ABC  ");
        System.out.println("              Kritik dan Saran              ");
        System.out.println("       081212341234/anjay@tokoabc.com       ");
    }
    
    public static String generateUniqueCode() {
        // Get current timestamp
        long timestamp = System.currentTimeMillis();

        // Format timestamp as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestampString = dateFormat.format(new Date(timestamp));

        return timestampString;
    }
    
    public class Item {
        
        private String KodeBarang;
        private int Qty;
        private String Promosi;
        private double totalHargaPromosi;
        
        public Item(String KodeBarang, int Qty, String Promosi, double totalHargaPromosi){
            this.KodeBarang=KodeBarang;
            this.Qty=Qty;
            this.Promosi=Promosi;
            this.totalHargaPromosi=totalHargaPromosi;
        }
        
        
        public String getKodeBarang() {
            return KodeBarang;
        }
        
        public int getQty(){
            return Qty;
        }
        
        public String getPromosi(){
            return Promosi;
        }
        
        public Double getTotalHargaPromosi(){
            return totalHargaPromosi;
        }
    }
    
    /**
     * Method untuk input data pesanan
     */
    public void inputDataPemesanan(String SalesId, String UniqueCode, String nomor_hp)throws Exception{
        boolean isLanjutkan = true;
        Scanner terminalInput = new Scanner(System.in);
        Connection connection = DriverManager.getConnection(JDBC_URL);
        
        List<Item> myObjList =  new ArrayList<Item>();
        double totalBelanja = 0;
        
        while(isLanjutkan){
                    System.out.println("\nINPUT DATA PEMESANAN");
                    
                    System.out.print("Input id barang: ");
                    String id_barang = terminalInput.nextLine();
                    
                    System.out.print("Input QTY barang: ");
                    int qty = terminalInput.nextInt();
                    
                    String id_promosi = getIdPromosiBarang(connection,id_barang);
                    double nilai_promosi = getPromosiBarang(connection,id_barang);
                    double harga_barang = getHargaBarang(connection,id_barang);
                    double totalHargaPromosi = nilai_promosi * qty;
                    double totalHargaBarang = (harga_barang * qty) - totalHargaPromosi;
                    
                    totalBelanja += totalHargaBarang;
                    
                    //Simpan Data Ke Array List
                    myObjList.add(new Item(id_barang,qty,id_promosi,totalHargaPromosi)); 
                    
                    isLanjutkan = getYesorNo("Tambah Barang? (y/n): ");
                    terminalInput.nextLine();
        }
            
        System.out.println("Pilih metode pengiriman: ");
        System.out.println("1. Kurir Toko");
        System.out.println("2. Ambil Sendiri");
        System.out.println("3. Ojek Online");
        String id_ekspedisi = terminalInput.nextLine();
        double biaya_pengiriman = 0;
        switch(id_ekspedisi){
            case "1" :
                // Memanggil metode getHargaEkspedisi untuk mendapatkan harga berdasarkan ID 1
                try (connection) {
                    String id_ekspedisi2 = "1"; // ID yang sesuai dengan data di database
                    biaya_pengiriman = getHargaEkspedisi(connection, id_ekspedisi);
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle exception appropriately based on your application's requirements
                }
                // biaya_pengiriman = 10000;
                break;
            case "2" :
                // Memanggil metode getHargaEkspedisi untuk mendapatkan harga berdasarkan ID 2
                try (connection) {
                    String id_ekspedisi2 = "2"; // ID yang sesuai dengan data di database
                    biaya_pengiriman = getHargaEkspedisi(connection, id_ekspedisi);
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle exception appropriately based on your application's requirements
                }
                break;
            case "3" :
                // Memanggil metode getHargaEkspedisi untuk mendapatkan harga berdasarkan ID 3
                try (connection) {
                    String id_ekspedisi2 = "3"; // ID yang sesuai dengan data di database
                    biaya_pengiriman = getHargaEkspedisi(connection, id_ekspedisi);
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle exception appropriately based on your application's requirements
                }
                break;
                default:
                System.out.println("ERROR");
        }
        
        double totalBayar = totalBelanja + biaya_pengiriman;
        String id_pembayaran = pembayaran();
        System.out.println("Total Belanja:" + totalBayar);
        String id_pelanggan = getIdPelanggan(nomor_hp);
        String Jenis_pengiriman = getJenisPengiriman(id_ekspedisi);
        
        System.out.println("Apakah pelanggan sudah melakukan pembayaran? (y/n)");
        String konfirmasi_pembayaran = terminalInput.nextLine();
        
        if (konfirmasi_pembayaran.toLowerCase() == "y"){
            Random random = new Random();
            int randNum = random.nextInt(1000000000);
        
            Connection c = DriverManager.getConnection(JDBC_URL);
            String query = "INSERT INTO kasir (id_transaksi,id_barang,id_promosi,total_belanja,biaya_pengiriman,total_diskon,total_bayar,id_pembayaran,id_sales,id_ekspedisi,id_pelanggan,id_group_transaksi) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = c.prepareStatement(query);
            for(Item model : myObjList){
                ps.setString(1, Integer.toString(randNum));
                ps.setString(2, model.getKodeBarang());
                ps.setString(3, model.getPromosi());
                ps.setDouble(4, totalBelanja);
                ps.setDouble(5, biaya_pengiriman);
                ps.setDouble(6, model.getTotalHargaPromosi());
                ps.setDouble(7, totalBayar);
                ps.setString(8, id_pembayaran);
                ps.setString(9, SalesId);
                ps.setString(10, Jenis_pengiriman);
                ps.setString(11, id_pelanggan); 
                ps.setString(12, UniqueCode);
                ps.addBatch();
                randNum++;
                ps.executeBatch(); 
            }   
            
            
        } else {
            System.out.println("Batalkan Transaksi? (y/n)");
            String transaksi_batal = terminalInput.nextLine();
            if (transaksi_batal.toLowerCase() == "y"){
                System.out.println("Transaski Batal");
            }
        }
    }
    
    /**
     * Method get jenis_pengiriman
     */
    private static String getJenisPengiriman(String id_ekspedisi)throws SQLException{
        String nama_ekspedisi = "";
        
        String query = "SELECT nama_ekspedisi FROM ekspedisi WHERE id_ekspedisi = ?";
        try(Connection connection = DriverManager.getConnection(JDBC_URL)){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, id_ekspedisi);
                 try(ResultSet resultSet = preparedStatement.executeQuery()){
                 if(resultSet.next()){
                     nama_ekspedisi = resultSet.getString("nama_ekspedisi");
                 }
             }
            }
        }
        return nama_ekspedisi;
    }
    
    
    /**
     * 
     * Method get ID Pelanggan
     */
    
    public static String getIdPelanggan(String NomorHP) throws SQLException {
        
        String IDPelanggan = "Non Member";
        String query = "SELECT id_pelanggan FROM pelanggan WHERE nomor_hp = ?";
                try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, NomorHP);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Jika terdapat ekspedisi, tampilkan jenis_pengiriman
                            IDPelanggan = resultSet.getString("id_pelanggan");
                        }
                    }
                }
            }
        return IDPelanggan;
    }
    
    
    
    /**
     * 
     * Method ID Pembayaran
     */
    
    public static String pembayaran() throws SQLException {
        Scanner terminalInput = new Scanner(System.in);
        
        System.out.println("Metode Pembayaran: ");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. QRIS");
        
        System.out.println("Pilih metode Pembayaran: ");
        String metode = terminalInput.nextLine();
        
        double biaya_pengiriman = 0;
        String JenisPembayaran = "";
        String IDPembayaran = "";
        String query = "";
        
        switch(metode){
            case "1" :
                JenisPembayaran = "cash";
                query = "SELECT id_pembayaran FROM jenis_pembayaran WHERE nama_pembayaran = ?";
                try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, JenisPembayaran);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Jika terdapat ekspedisi, tampilkan jenis_pengiriman
                            IDPembayaran = resultSet.getString("id_pembayaran");
                        }
                    }
                }}
                break;
            case "2" :
                JenisPembayaran = "card";
                query = "SELECT id_pembayaran FROM jenis_pembayaran WHERE nama_pembayaran = ?";
                try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, JenisPembayaran);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Jika terdapat ekspedisi, tampilkan jenis_pengiriman
                            IDPembayaran = resultSet.getString("id_pembayaran");
                        }
                    }
                }}
                break;
            case "3" :
                JenisPembayaran = "qris";
                query = "SELECT id_pembayaran FROM jenis_pembayaran WHERE nama_pembayaran = ?";
                try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, JenisPembayaran);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Jika terdapat ekspedisi, tampilkan jenis_pengiriman
                            IDPembayaran = resultSet.getString("id_pembayaran");
                        }
                    }
                }
            }
        }
        
        return IDPembayaran;
    }
    
    
    /**
     * Method insert DB 
     */
    private static void insertDBTransaksi(String id_transaksi,String id_barang, String id_promosi, double total_belanja,
        double biaya_pengiriman, double total_diskon, double total_bayar, String id_pembayaran, String id_sales, String id_ekpedisi
        ,String id_pelanggan) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)){
            String query = "INSERT INTO kasir (id_transaksi,id_barang,id_promosi,total_belanja,biaya_pengiriman,total_diskon,total_bayar,id_pembayaran,id_sales,id_ekspedisi,id_pelanggan) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id_transaksi);
                preparedStatement.setString(2, id_barang);
                preparedStatement.setString(3, id_promosi);
                preparedStatement.setDouble(4, total_belanja);
                preparedStatement.setDouble(5, biaya_pengiriman);
                preparedStatement.setDouble(6, total_diskon);
                preparedStatement.setDouble(7, total_bayar);
                preparedStatement.setString(8, id_pembayaran);
                preparedStatement.setString(9, id_sales);
                preparedStatement.setString(10, id_ekpedisi);
                preparedStatement.setString(11, id_pelanggan);
            
            // Execute the insert query
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaksi Berhasil Tersimpan");
            } else {
                System.out.println("Gagal Simpan Transaksi Ke Database");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }}        
    }
    
    
    /**
     * Method get id_ekspedisi
     */
    private static String jenisKirim(Connection connection, String id_ekspedisi3) throws SQLException {
        String id_ekspedisi2 =""; 

        String query = "SELECT id_ekspedisi FROM ekspedisi WHERE nama_ekspedisi = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id_ekspedisi2);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Jika terdapat ekspedisi, tampilkan jenis_pengiriman
                    id_ekspedisi2 = resultSet.getString("id_ekspedisi2");
                }
            }
        }
        return id_ekspedisi2;        
    }
    
    /**
     * Method get harga barang
     */
    private static double getHargaBarang(Connection connection, String id_barang) throws SQLException {
        double harga_barang = 0; // Inisialisasi hargaBarang dengan nilai default

        String query = "SELECT harga_barang FROM barang WHERE id_barang = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id_barang);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    harga_barang = resultSet.getDouble("harga_barang");
                } else {
                    System.out.println("Barang dengan ID " + id_barang + " tidak ditemukan.");
                }
            }
        }
        return harga_barang;
    }
    
    /**
     * Method get id promosi
     */
    private static String getIdPromosiBarang(Connection connection, String id_barang) throws SQLException {
        String id_promosi =""; // Inisialisasi hargaBarang dengan nilai default

        String query = "SELECT id_promosi FROM promosi WHERE id_barang = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id_barang);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Jika terdapat promosi, tampilkan nilai_promosi
                    id_promosi = resultSet.getString("id_promosi");
                }
            }
        }
        return id_promosi;
    }
    
    /**
     * Method get promosi barang
     */
    private static double getPromosiBarang(Connection connection, String id_barang) throws SQLException {
        double nilai_promosi = 0; // Inisialisasi hargaBarang dengan nilai default

        String query = "SELECT nilai_promosi FROM promosi WHERE id_barang = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id_barang);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Jika terdapat promosi, tampilkan nilai_promosi
                    nilai_promosi = resultSet.getDouble("nilai_promosi");
                }
            }
        }
        return nilai_promosi;
    }
    
    /**
     * Method untuk get harga ekspedisi
     */
    
    public static double getHargaEkspedisi(Connection connection, String id_ekspedisi) throws SQLException {
        double jumlah_pembayaran = 0; // Inisialisasi harga dengan nilai default

        String query = "SELECT jumlah_pembayaran FROM ekspedisi WHERE id_ekspedisi = ?";
        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id_ekspedisi);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Jika terdapat data produk, ambil nilai harga
                    jumlah_pembayaran = resultSet.getDouble("jumlah_pembayaran");
                }
            }
        }
        return jumlah_pembayaran;
    }
    
    /**
     * Method untuk metode pengiriman
     */
    
    // End method pengiriman 
    

    public static boolean checkLogin(Connection connection, String id_sales, String nama_sales) {
        String query = "SELECT COUNT(*) FROM sales WHERE id_sales = ? AND nama_sales = ?"; // Kolom dan tabel sudah disesuaikan

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id_sales);
            statement.setString(2, nama_sales);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }  
    }
    
    private static boolean getYesorNo(String message){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n"+message+" (y/n)? ");
        String pilihanUser = terminalInput.next();

        while(!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
            System.err.println("Pilihan anda bukan y atau n");
            System.out.print("\n"+message+" (y/n)? ");
            pilihanUser = terminalInput.next();
        }

        return pilihanUser.equalsIgnoreCase("y");

    }
}
