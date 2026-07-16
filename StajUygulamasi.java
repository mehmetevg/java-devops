import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StajUygulamasi {
    
    // Docker Compose içindeki PostgreSQL servis adını (db) host olarak kullanıyoruz
    static final String DB_URL = "jdbc:postgresql://db:5432/stajdb";
    static final String USER = "stajuser";
    static final String PASS = "stajpass";

    public static void main(String[] args) throws Exception {
        System.out.println("Veritabaninin uyanmasi bekleniyor (5sn)...");
        Thread.sleep(5000);
        
        // Tabloyu oluştur
        tabloyuOlustur();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Sistem 8000 portunda aktif.");
    }

    static void tabloyuOlustur() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
             
             String sql = "CREATE TABLE IF NOT EXISTS maclar (" +
                          "id SERIAL PRIMARY KEY, " +
                          "tarih VARCHAR(50), " +
                          "ev_sahibi VARCHAR(50), " +
                          "skor VARCHAR(20), " +
                          "deplasman VARCHAR(50), " +
                          "sari_kart VARCHAR(20), " +
                          "kirmizi_kart VARCHAR(20))";
             stmt.executeUpdate(sql);
             System.out.println("PostgreSQL tablosu kontrol edildi/olusturuldu.");
        } catch (SQLException e) {
             System.out.println("Tablo olusturma hatasi: " + e.getMessage());
        }
    }

    // Cron'un indirdiği CSV verilerini okuyup PostgreSQL veritabanına senkronize eden metod
    static void csvVerileriniVeritabaninaYaz() {
        String csvPath = "/app/data/matches.csv";
        File file = new File(csvPath);
        if (!file.exists()) {
            System.out.println("Senkronizasyon basarisiz: " + csvPath + " adresinde CSV dosyasi henüz yok.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Çakışma olmaması için her senkronizasyonda tabloyu temizleyip güncel CSV'yi yazıyoruz
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE TABLE maclar");
            }

            String sql = "INSERT INTO maclar (tarih, ev_sahibi, skor, deplasman, sari_kart, kirmizi_kart) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                List<String> lines = Files.readAllLines(Paths.get(csvPath));
                
                // İlk satır başlıklar (Tarih, Ev Sahibi...) olduğu için i = 1'den başlıyoruz
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isEmpty()) continue;
                    
                    String[] cols = line.split(",");
                    if (cols.length >= 6) {
                        pstmt.setString(1, cols[0].trim());
                        pstmt.setString(2, cols[1].trim());
                        pstmt.setString(3, cols[2].trim());
                        pstmt.setString(4, cols[3].trim());
                        pstmt.setString(5, cols[4].trim());
                        pstmt.setString(6, cols[5].trim());
                        pstmt.addBatch();
                    }
                }
                pstmt.executeBatch();
                System.out.println("CSV verileri PostgreSQL veritabanina basariyla yazildi!");
            }
        } catch (Exception e) {
            System.out.println("Senkronizasyon hatasi: " + e.getMessage());
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Sayfa her yüklendiğinde önce CSV dosyasını oku ve veritabanını güncelle
            csvVerileriniVeritabaninaYaz();

            StringBuilder html = new StringBuilder();
            
            html.append("<html><head><meta charset='UTF-8'><title>Hakemlik Panosu</title>");
            html.append("<style>");
            html.append("body {font-family: 'Segoe UI', sans-serif; background-color: #1e1e2f; color: #fff; text-align: center; padding: 40px;}");
            html.append("h1 {color: #00d2ff; text-transform: uppercase; letter-spacing: 2px;}");
            html.append("table {margin: 30px auto; border-collapse: collapse; width: 90%; background: #2a2a40; border-radius: 10px; overflow: hidden; box-shadow: 0 8px 16px rgba(0,0,0,0.3);}");
            html.append("th, td {padding: 15px 20px; border-bottom: 1px solid #3f3f5a;}");
            html.append("th {background-color: #00d2ff; color: #000; font-weight: bold;}");
            html.append("tr:hover {background-color: #3f3f5a;}");
            html.append(".sarı {color: #ffd700; font-weight: bold; text-align: center;}"); 
            html.append(".kırmızı {color: #ff4c4c; font-weight: bold; text-align: center;}"); 
            html.append("</style></head><body>");
            
            html.append("<h1>Resmi Maç Yönetim Fikstürü</h1>");
            html.append("<table><tr><th>Tarih</th><th>Ev Sahibi</th><th>Skor</th><th>Deplasman</th><th>Sarı Kart</th><th>Kırmızı Kart</th></tr>");

            // ARTIK VERİLERİ SADECE VERİTABANINDAN ÇEKİYORUZ
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM maclar ORDER BY id DESC")) {
                 
                 boolean veriVarMi = false;
                 while(rs.next()){
                     veriVarMi = true;
                     html.append("<tr>")
                         .append("<td>").append(rs.getString("tarih")).append("</td>")
                         .append("<td>").append(rs.getString("ev_sahibi")).append("</td>")
                         .append("<td><b>").append(rs.getString("skor")).append("</b></td>")
                         .append("<td>").append(rs.getString("deplasman")).append("</td>")
                         .append("<td class='sari'>").append(rs.getString("sari_kart")).append("</td>")
                         .append("<td class='kirmizi'>").append(rs.getString("kirmizi_kart")).append("</td>")
                         .append("</tr>");
                 }
                 if (!veriVarMi) {
                     html.append("<tr><td colspan='6'>Henüz mac verisi eklenmedi. CSV dosyasini kontrol edin.</td></tr>");
                 }
            } catch (SQLException e) {
                html.append("<tr><td colspan='6'>Veritabani baglantisi sirasinda hata olustu: ").append(e.getMessage()).append("</td></tr>");
            }

            html.append("</table></body></html>");

            byte[] response = html.toString().getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
