import java.sql.*;
import java.util.Scanner;

public class InputPelanggan {
    public static void main(String[] args) {
        try {
            Scanner terminalInput = new Scanner(System.in);
            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection("jdbc:sqlite:toko.db");
            Statement stat = conn.createStatement();
            PreparedStatement prep = conn.prepareStatement("insert into pelanggan values (null, ?, ?);");

            ResultSet rs = stat.executeQuery("SELECT * FROM pelanggan");

            System.out.println("========== DATA PELANGGAN ==========");
            System.out.println("\n");

            System.out.println("No\tId Pelanggan\tNama Pelanggan\tNomor HP");
            System.out.println("=============================================");
            int no = 1;
            while (rs.next()) {
                String id_pelanggan = rs.getString("id_pelanggan");
                String nama_pelanggan = rs.getString("nama_pelanggan");
                String nomor_hp = rs.getString("nomor_hp");

                System.out.println(no++ + "\t" + id_pelanggan + "\t" + nama_pelanggan + "\t" + nomor_hp);
            }

            boolean isLanjutkan = true;

            while (isLanjutkan) {
                try {
                    System.out.print("Apakah Anda ingin menambahkan data pelanggan: ");
                    String pertanyaan = terminalInput.nextLine();

                    if (pertanyaan.equalsIgnoreCase("y")) {
                        System.out.print("Nama Pelanggan: ");
                        String nama_pelanggan = terminalInput.nextLine();
                        System.out.print("Nomor HP: ");
                        String nomor_hp = terminalInput.nextLine();

                        prep.setString(1, nama_pelanggan);
                        prep.setString(2, nomor_hp);
                        prep.addBatch();

                        System.out.println("Data berhasil ditambahkan");
                    } else {
                        System.out.println("Tidak ada data yang ditambahkan");
                    }
                    isLanjutkan = getYesOrNo("Apakah Anda ingin melanjutkan");
                    terminalInput.nextLine();
                    conn.setAutoCommit(false);
                    prep.executeBatch();
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    conn.rollback();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Mengecek id_pelanggan sudah ada di database atau belum
    private static boolean idPelangganExists(Connection conn, String id_pelanggan) throws SQLException {
        String query = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id_pelanggan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean getYesOrNo(String message) {
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n" + message + " (y/n)? ");
        String pilihanUser = terminalInput.next();

        while (!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
            System.err.println("Pilihan anda bukan y atau n");
            System.out.print("\n" + message + " (y/n)? ");
            pilihanUser = terminalInput.next();
        }

        return pilihanUser.equalsIgnoreCase("y");
    }
}
