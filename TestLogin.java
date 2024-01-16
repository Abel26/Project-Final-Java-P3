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
                System.out.print("Id: ");
                String id_sales = terminalInput.nextLine();
    
                System.out.print("Nama: ");
                String nama_sales = terminalInput.nextLine();
    
                if (checkLogin(connection, id_sales, nama_sales)) {
                    System.out.println("Login berhasil");
                    
                    System.out.println("");
                    
                    System.out.println("Ingin berbelanja online / offline");
                    String pertanyaan = terminalInput.nextLine();
                    
                    System.out.println("Nama Pelanggan: ");
                    String nama_pelanggan = terminalInput.nextLine();
                    
                    System.out.println("No Hp Aktif: ");
                    String nomor_hp = terminalInput.nextLine();
                    
                    prep.setString(1,nama_pelanggan);
                    prep.setString(2,nomor_hp);
                    
                    terminalInput.nextLine();
                    connection.setAutoCommit(false);
                    prep.executeBatch();
                    connection.setAutoCommit(true);
                    
                    // Menutup ResultSet, Statement, dan Koneksi
                    // test.close();
                    connection.close();
                    
                    
                } else {
                    System.out.println("Id/Nama salah. Login gagal.");
                }  
                isLanjutkan = getYesorNo("Input ulang? (y/n): ");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
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
