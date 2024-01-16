import java.sql.*;
import java.util.Scanner;

public class InputBarangKeDb{
    public static void main(String[] args) throws Exception{
        Scanner terminalInput = new Scanner(System.in);
        // Untuk mendaftarkan driver JDBC untuk SQLite. Ini perlu dilakukan sebelum membuat koneksi
        Class.forName("org.sqlite.JDBC");
        // Membuat koneksi ke database SQLite
        Connection conn = DriverManager.getConnection("jdbc:sqlite:toko.db");
        // Membuat objek
        Statement stat = conn.createStatement();
        PreparedStatement prep = conn.prepareStatement("insert into barang values (?, ?, ?, ? );");
        
        // Mengeksekusi query untuk mengambil data
        ResultSet rs = stat.executeQuery("SELECT * FROM barang");
        
        System.out.println("========== DATA BARANG ==========");
        // Menampilkan hasil query
        System.out.println("\n");
        
        System.out.println("No\tId Barang\tStock Barang\tHarga Barang");
        System.out.println("=============================================");
        int no = 1;
        while(rs.next()){
            String id_barang = rs.getString("id_barang");
            String nama_barang = rs.getString("nama_barang");
            // Mengkonversi stock_barang ke tipe data int
            int stock_barang = Integer.parseInt(rs.getString("stock_barang"));
            // Mengkonversi harga_barang ke tipe data double
            double harga_barang = Double.parseDouble(rs.getString("harga_barang"));
            
            // Menyesuaikan lebar kolom dengan menggunakan tabulasi
            System.out.println(no++ + "\t" + id_barang + "\t" + nama_barang + "\t" + stock_barang + "\t" + harga_barang);
        }
        
        boolean isLanjutkan = true;
        
        while(isLanjutkan){
            System.out.print("Apakah Anda ingin menambahkan data barang: ");
            String pertanyaan = terminalInput.nextLine();
            
            if (pertanyaan.equalsIgnoreCase("y")) {
                String id_barang;
                while (true) {
                    System.out.print("Id barang : ");
                    id_barang = terminalInput.nextLine();
    
                    if (npmExists(conn, id_barang)) {
                        System.out.println("Id barng sudah ada di database. Silakan input id barang yang berbeda.");
                    } else {
                        break; // Keluar dari loop jika id_barang belum ada di database
                    }
                }
    
                System.out.print("Nama barang: ");
                String nama_barang = terminalInput.nextLine();
                System.out.print("Stock Barang: ");
                int stock_barang = terminalInput.nextInt();
                System.out.print("Harga Barang: ");
                double harga_barang = terminalInput.nextDouble();
    
                prep.setString(1, id_barang);
                prep.setString(2, nama_barang);
                prep.setInt(3, stock_barang);
                prep.setDouble(4, harga_barang);
                prep.addBatch();
    
                System.out.println("Data berhasil ditambahkan");
            }else{
                System.out.println("Tidak ada data yang ditambahkan");
            } 
            isLanjutkan = getYesorNo("Apakah Anda ingin melanjutkan");
            // Membersihkan newline pada buffer
            terminalInput.nextLine();
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);
            
            // Menutup ResultSet, Statement, dan Koneksi
            // test.close();
            conn.close();
        }
    }
    
    // Mengecek id_barang sudah ada di database atau belum
    private static boolean npmExists(Connection conn, String id_barang) throws SQLException {
        String query = "SELECT * FROM barang WHERE id_barang = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id_barang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
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