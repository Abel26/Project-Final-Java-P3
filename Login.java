import java.sql.*;
import java.util.Scanner;

public class Login {

    private static final String JDBC_URL = "jdbc:sqlite:toko.db";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            Scanner terminalInput = new Scanner(System.in);
            System.out.print("Id: ");
            String id_sales = terminalInput.nextLine();

            System.out.print("Nama: ");
            String nama_sales = terminalInput.nextLine();

            if (checkLogin(connection, id_sales, nama_sales)) {
                System.out.println("Login berhasil");
            } else {
                System.out.println("Id/Nama salah. Login gagal.");
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
}
