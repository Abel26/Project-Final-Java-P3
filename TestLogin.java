import java.sql.*;
import java.util.Scanner;

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
                    
                    System.out.println("\nSELAMAT BERTUGAS " + nama_sales2);
                    
                    // Input data pemesanan
                    inputDataPemesanan();

                    // terminalInput.nextLine();
                    
                    
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
     * Method untuk input data pesanan
     */
    public static void inputDataPemesanan()throws Exception{
        boolean isLanjutkan = true;
        Scanner terminalInput = new Scanner(System.in);
        Connection connection = DriverManager.getConnection(JDBC_URL);
        while(isLanjutkan){
                    System.out.println("\nINPUT DATA PEMESANAN");
                    
                    System.out.print("Input id barang: ");
                    String id_barang = terminalInput.nextLine();
                    
                    System.out.print("Input QTY barang: ");
                    int qty = terminalInput.nextInt();
                    
                    // Melakukan koneksi ke database
                    try {
                        // Mengecek promosi berdasarkan ID barang
                        String query = "SELECT nilai_promosi FROM promosi WHERE id_barang = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, id_barang);
            
                            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                    // Jika terdapat promosi, tampilkan nilai_promosi
                                    double nilai_promosi = resultSet.getDouble("nilai_promosi");
                                    // System.out.println("Promosi ditemukan! Nilai promosi: " + nilai_promosi);
                                    System.out.println("Terdapat potongan harga sebesar: " + nilai_promosi);
                                } else {
                                    // Jika tidak terdapat promosi
                                    System.out.println("Tidak ada promosi untuk ID barang " + id_barang);
                                }
                            }
                        }
            
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        }
                    isLanjutkan = getYesorNo("Tambah Barang? (y/n): ");
                    pengiriman();
                    // Menghitung total belanja
                    }
    }
    
    
    /**
     * Method untuk metode pengiriman
     */
    public static void pengiriman(){
        Scanner terminalInput = new Scanner(System.in);
        
        System.out.println("Pilih metode pengiriman: ");
        System.out.println("1. Kurir Toko");
        System.out.println("2. Ambil Sendiri");
        System.out.println("3. Ojek Online");
        String metode = terminalInput.nextLine();
        
        switch(metode){
            case "1" :
                System.out.println("Metode Pengiriman dengan kurir toko");
                break;
            case "2" :
                System.out.println("Ambil barang belian Anda di toko");
                break;
            case "3" :
                System.out.println("Metode Pengiriman dengan ojek online");
        }
    }
    

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
