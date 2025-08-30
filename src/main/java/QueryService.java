import java.sql.*;
import java.util.*;

public class QueryService {

    // 1. 查詢學生的所有修課
    public List<Map<String, Object>> getCoursesByStudentId(Connection conn, int studentId) throws SQLException {
        String sql = "SELECT c.name, c.credits, e.enrollment_date FROM enrollment e JOIN course c ON e.course_id = c.id WHERE e.student_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", rs.getString(1));
            row.put("credits", rs.getInt(2));
            row.put("date", rs.getDate(3));
            result.add(row);
        }
        return result;
    }

    // 2. 查詢課程的所有學生
    public List<Map<String, Object>> getStudentsByCourseId(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT s.name, s.email, e.enrollment_date FROM enrollment e JOIN student s ON e.student_id = s.id WHERE e.course_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", rs.getString(1));
            row.put("email", rs.getString(2));
            row.put("date", rs.getDate(3));
            result.add(row);
        }
        return result;
    }

    // 3. 查詢某課有哪些學生修過
    public List<String> getStudentNamesByCourseId(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT DISTINCT s.name FROM enrollment e JOIN student s ON e.student_id = s.id WHERE e.course_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<String> result = new ArrayList<>();
        while (rs.next()) result.add(rs.getString(1));
        return result;
    }

    // 4. 修課人數最多的前10門課
    public List<Map<String, Object>> getTop10Courses(Connection conn) throws SQLException {
        String sql = "SELECT c.id, c.name, COUNT(*) AS cnt FROM enrollment e JOIN course c ON e.course_id = c.id GROUP BY c.id, c.name ORDER BY cnt DESC LIMIT 10";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", rs.getInt(1));
            row.put("name", rs.getString(2));
            row.put("count", rs.getInt(3));
            result.add(row);
        }
        return result;
    }
}