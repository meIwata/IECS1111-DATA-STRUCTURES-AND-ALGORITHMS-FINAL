CREATE DATABASE course_system DEFAULT CHARACTER SET utf8mb4;
USE course_system;

CREATE TABLE teacher (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL,
                         email VARCHAR(100)
);

CREATE TABLE course (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL,
                        credits INT,
                        teacher_id INT,
                        FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

CREATE TABLE student (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL,
                         email VARCHAR(100)
);

CREATE TABLE enrollment (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            student_id INT,
                            course_id INT,
                            enrollment_date DATE,
                            FOREIGN KEY (student_id) REFERENCES student(id),
                            FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE INDEX idx_student_id ON enrollment(student_id);
CREATE INDEX idx_course_id ON enrollment(course_id);