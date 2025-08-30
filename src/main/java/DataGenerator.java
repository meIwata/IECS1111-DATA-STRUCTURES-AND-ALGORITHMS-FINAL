import com.github.javafaker.Faker;
import java.sql.*;
import java.util.*;

public class DataGenerator {
    public static void main(String[] args) throws Exception {
        Faker faker = new Faker();
        Random rand = new Random();
        Connection conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        // 1. 老師
        PreparedStatement psTeacher = conn.prepareStatement(
                "INSERT INTO teacher (name, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < 100; i++) {
            psTeacher.setString(1, faker.name().fullName());
            psTeacher.setString(2, faker.internet().emailAddress());
            psTeacher.addBatch();
        }
        psTeacher.executeBatch();
        conn.commit();

        // 2. 課程
        List<Integer> teacherIds = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery("SELECT id FROM teacher");
        while (rs.next()) teacherIds.add(rs.getInt(1));

        PreparedStatement psCourse = conn.prepareStatement(
                "INSERT INTO course (name, credits, teacher_id) VALUES (?, ?, ?)");
        for (int i = 0; i < 1000; i++) {
            psCourse.setString(1, faker.book().title());
            psCourse.setInt(2, rand.nextInt(5) + 1);
            psCourse.setInt(3, teacherIds.get(rand.nextInt(teacherIds.size())));
            psCourse.addBatch();
        }
        psCourse.executeBatch();
        conn.commit();

        // 3. 學生
        PreparedStatement psStudent = conn.prepareStatement(
                "INSERT INTO student (name, email) VALUES (?, ?)");
        for (int i = 0; i < 10000; i++) {
            psStudent.setString(1, faker.name().fullName());
            psStudent.setString(2, faker.internet().emailAddress());
            psStudent.addBatch();
            if (i % 1000 == 0) { psStudent.executeBatch(); conn.commit(); }
        }
        psStudent.executeBatch();
        conn.commit();

        // 4. 修課紀錄
        List<Integer> courseIds = new ArrayList<>();
        List<Integer> studentIds = new ArrayList<>();
        rs = conn.createStatement().executeQuery("SELECT id FROM course");
        while (rs.next()) courseIds.add(rs.getInt(1));
        rs = conn.createStatement().executeQuery("SELECT id FROM student");
        while (rs.next()) studentIds.add(rs.getInt(1));

        PreparedStatement psEnroll = conn.prepareStatement(
                "INSERT INTO enrollment (student_id, course_id, enrollment_date) VALUES (?, ?, ?)");
        for (int i = 0; i < 1_000_000; i++) {
            psEnroll.setInt(1, studentIds.get(rand.nextInt(studentIds.size())));
            psEnroll.setInt(2, courseIds.get(rand.nextInt(courseIds.size())));
            long minDay = java.sql.Date.valueOf("2023-01-01").getTime();
            long maxDay = java.sql.Date.valueOf("2024-08-30").getTime();
            long randomDay = minDay + (long) (rand.nextDouble() * (maxDay - minDay));
            psEnroll.setDate(3, new java.sql.Date(randomDay));
            psEnroll.addBatch();
            if (i % 5000 == 0) { psEnroll.executeBatch(); conn.commit(); }
        }
        psEnroll.executeBatch();
        conn.commit();

        conn.close();
        System.out.println("資料產生完成！");
    }
}