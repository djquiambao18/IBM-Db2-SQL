
connect to cs157a^
CREATE TABLE hw3.student (
        student_id char (6) NOT NULL,
        first varchar (15) NOT NULL,
        last varchar (15) NOT NULL,
        gender char (1) NOT NULL,
        CHECK(gender in ('M', 'F', 'O')),
        PRIMARY KEY(student_id)
)^

CREATE TABLE hw3.class (
    class_id char (6) NOT NULL,
    name varchar (20) NOT NULL,
    desc varchar (20) NOT NULL,
    PRIMARY KEY(class_id)
 )^

CREATE TABLE hw3.class_prereq (
    class_id char (6) NOT NULL,
    prereq_id char (6) NOT NULL,
    req_grade char (1) NOT NULL,
    CHECK(req_grade in ('A', 'B', 'C', 'D', 'F', 'I', 'W')),
    CHECK(prereq_id <> class_id),
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    FOREIGN KEY(prereq_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE
)^

CREATE TABLE hw3.schedule (
    student_id char (6) NOT NULL,
    class_id char (6) NOT NULL,
    semester int NOT NULL,
    year int NOT NULL,
    grade char (1),
    CHECK(semester in (1, 2, 3)),
    CHECK(year >= 1950 AND year <= 2022),
    CHECK(grade in ('A', 'B', 'C', 'D', 'F', 'I', 'W')),
    FOREIGN KEY(student_id) REFERENCES hw3.student(student_id) ON DELETE CASCADE,
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    PRIMARY KEY(student_id)
)^


CREATE TRIGGER hw3.classcheck
NO CASCADE BEFORE INSERT ON HW3.SCHEDULE
REFERENCING NEW AS newrow
FOR EACH ROW MODE DB2SQL
WHEN ( (SELECT COUNT(*) FROM hw3.class_prereq WHERE hw3.class_prereq.class_id = newrow.class_id) > 0 )
BEGIN ATOMIC
        DECLARE num_prereq int;
        DECLARE prereq_pass int;

        SET num_prereq = (SELECT COUNT(*) FROM hw3.class_prereq WHERE hw3.class_prereq.class_id = newrow.class_id) + (SELECT hw3.class_prereq.req_grade FROM hw3.class_prereq WHERE newrow.grade <= hw3.class_prereq.req_grade AND hw3.class_prereq.class_id = newrow.class_id);

        IF ( num_prereq > 0 )
        THEN      SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
        END IF;
END^
