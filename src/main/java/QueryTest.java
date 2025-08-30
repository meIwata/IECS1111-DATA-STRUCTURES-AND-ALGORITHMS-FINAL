import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class QueryTest {
    public static void main(String[] args) throws Exception {
        Connection conn = DBUtil.getConnection();
        QueryService qs = new QueryService();

        // 測試功能一：查詢學生ID=1的所有課程
        System.out.println("學生ID=1所有課程：");
        List<Map<String, Object>> courses = qs.getCoursesByStudentId(conn, 1);
        for (Map<String, Object> row : courses) {
            System.out.println(row.get("name") + " 學分:" + row.get("credits") + " 日期:" + row.get("date"));
        }

        // 測試功能二：查詢課程ID=1的所有學生
        System.out.println("\n課程ID=1的所有學生：");
        List<Map<String, Object>> students = qs.getStudentsByCourseId(conn, 1);
        for (Map<String, Object> row : students) {
            System.out.println(row.get("name") + " Email:" + row.get("email") + " 日期:" + row.get("date"));
        }

        // 測試功能三：查詢課程ID=1有哪些學生修過
        System.out.println("\n課程ID=1的學生姓名：");
        List<String> names = qs.getStudentNamesByCourseId(conn, 1);
        for (String name : names) {
            System.out.println(name);
        }

        // 測試功能四：前10熱門課程
        System.out.println("\n前10熱門課程：");
        List<Map<String, Object>> hot = qs.getTop10Courses(conn);
        for (Map<String, Object> row : hot) {
            System.out.println("課程ID:" + row.get("id") + " 名稱:" + row.get("name") + " 修課人數:" + row.get("count"));
        }

        conn.close();
    }
}