import java.sql.*;
import java.util.*;

public class PerformanceTest {
    public static void main(String[] args) throws Exception {
        int[] dataSizes = {10_000, 100_000, 1_000_000};
        for (int size : dataSizes) {
            System.out.println("\n==== 資料量: " + size + " ====");
            testSQLPerformance(size);
            testHashPerformance(size);
        }
    }

    public static void testSQLPerformance(int size) throws Exception {
        // 準備資料庫只保留 size 筆 enrollment 資料，或只用前 size 筆測試
        Connection conn = DBUtil.getConnection();
        QueryService qs = new QueryService();

        // 隨機產生 1000 個學生ID
        List<Integer> studentIds = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT DISTINCT student_id FROM enrollment LIMIT 1000");
        while (rs.next()) studentIds.add(rs.getInt(1));

        long total = 0;
        for (int id : studentIds) {
            long start = System.nanoTime();
            qs.getCoursesByStudentId(conn, id);
            total += (System.nanoTime() - start);
        }
        System.out.println("[SQL] 查詢1000次總耗時(ms): " + total / 1_000_000.0);
        System.out.println("[SQL] 單次平均(ms): " + total / 1000_000.0);
        conn.close();
    }

    public static void testHashPerformance(int size) throws Exception {
        Connection conn = DBUtil.getConnection();

        // 1. 讀取所有enrollment資料，建HashMap
        Map<Integer, List<Integer>> stuCourseMap = new HashMap<>();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT student_id, course_id FROM enrollment LIMIT " + size);
        while (rs.next()) {
            int sid = rs.getInt(1), cid = rs.getInt(2);
            stuCourseMap.computeIfAbsent(sid, k -> new ArrayList<>()).add(cid);
        }

        // 2. 測試查詢速度
        List<Integer> allStuIds = new ArrayList<>(stuCourseMap.keySet());
        Random rand = new Random();
        long total = 0;
        for (int i = 0; i < 1000; i++) {
            int sid = allStuIds.get(rand.nextInt(allStuIds.size()));
            long start = System.nanoTime();
            stuCourseMap.get(sid);
            total += (System.nanoTime() - start);
        }
        System.out.println("[Hash] 查詢1000次總耗時(ms): " + total / 1_000_000.0);
        System.out.println("[Hash] 單次平均(ms): " + total / 1000_000.0);
        System.out.println("[Hash] 佔用記憶體(bytes): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        conn.close();
    }
}