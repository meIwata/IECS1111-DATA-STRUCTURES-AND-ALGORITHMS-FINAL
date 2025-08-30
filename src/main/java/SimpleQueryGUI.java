import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class SimpleQueryGUI extends Application {
    private QueryService service = new QueryService();

    @Override
    public void start(Stage primaryStage) {
        // UI元件
        Label studentLabel = new Label("查學生課程（輸入學生ID）:");
        TextField studentField = new TextField();
        Button studentBtn = new Button("查詢學生課程");
        ListView<String> courseListView = new ListView<>();

        Label courseLabel = new Label("查課程學生（輸入課程ID）:");
        TextField courseField = new TextField();
        Button courseBtn = new Button("查詢課程學生");
        ListView<String> studentListView = new ListView<>();

        // 學生查課程事件
        studentBtn.setOnAction(e -> {
            courseListView.getItems().clear();
            try (Connection conn = DBUtil.getConnection()) {
                int sid = Integer.parseInt(studentField.getText());
                List<Map<String, Object>> list = service.getCoursesByStudentId(conn, sid);
                for (Map<String, Object> row : list) {
                    courseListView.getItems().add(
                            String.format("課程: %s　學分: %s　修課日期: %s",
                                    row.get("name"), row.get("credits"), row.get("date"))
                    );
                }
                if(list.isEmpty()) courseListView.getItems().add("查無資料");
            } catch (Exception ex) {
                courseListView.getItems().add("查詢錯誤：" + ex.getMessage());
            }
        });

        // 課程查學生事件
        courseBtn.setOnAction(e -> {
            studentListView.getItems().clear();
            try (Connection conn = DBUtil.getConnection()) {
                int cid = Integer.parseInt(courseField.getText());
                List<Map<String, Object>> list = service.getStudentsByCourseId(conn, cid);
                for (Map<String, Object> row : list) {
                    studentListView.getItems().add(
                            String.format("姓名: %s　Email: %s　修課日期: %s",
                                    row.get("name"), row.get("email"), row.get("date"))
                    );
                }
                if(list.isEmpty()) studentListView.getItems().add("查無資料");
            } catch (Exception ex) {
                studentListView.getItems().add("查詢錯誤：" + ex.getMessage());
            }
        });

        // 版面配置
        VBox studentBox = new VBox(5, studentLabel, studentField, studentBtn, courseListView);
        VBox courseBox = new VBox(5, courseLabel, courseField, courseBtn, studentListView);
        HBox root = new HBox(15, studentBox, courseBox);

        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setTitle("簡易課程修課查詢");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}