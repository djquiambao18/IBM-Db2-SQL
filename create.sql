-- disallow insertion of duplicate student_id in the Student table and duplicate class_id in the Class table (DDL Statement)
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
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    FOREIGN KEY(prereq_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    CHECK(prereq_id <> class_id),
    PRIMARY KEY(class_id)
)^

CREATE TABLE hw3.schedule (
    student_id char (6) NOT NULL,
    class_id char (6) NOT NULL,
    semester int NOT NULL,
    year int NOT NULL,
    grade char (1),
    CHECK(semester in (1, 2, 3)),
    --Check year is between 1950 and 2022
    CHECK(year >= 1950 AND year <= 2022),
    CHECK(grade in ('A', 'B', 'C', 'D', 'F', 'I', 'W')),
    FOREIGN KEY(student_id) REFERENCES hw3.student(student_id) ON DELETE CASCADE,
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    PRIMARY KEY(student_id)
)^

-- Disallow students to take a class without completing their pre-requisite(s) (Must use a trigger). The trigger must return an error message “Missing Pre-req” when trying to insert a class without its proper pre-req. Create trigger
-- CREATE TRIGGER classcheck 
-- NO CASCADE BEFORE INSERT ON hw3.schedule 
-- REFERENCING NEW AS newRow 
-- FOR EACH ROW MODE DB2SQL
--     WHEN ( (SELECT )) 
-- BEGIN ATOMIC 
--     SET newRow.semester=null;
--     IF (newRow.semester is null) 
--     THEN 
--         SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
--     END IF;
-- END^
-- CREATE TRIGGER class_prereq_check 
-- NO CASCADE BEFORE INSERT ON hw3.schedule
-- REFERENCING NEW AS newrow
-- FOR EACH ROW MODE DB2SQL
-- WHEN ( (SELECT COUNT(*) FROM hw3.class_prereq WHERE class_id = newrow.class_id) > 0 )
-- BEGIN ATOMIC
--     IF ( (SELECT COUNT(*) FROM hw3.class_prereq WHERE class_id = newrow.class_id AND req_grade <= (SELECT grade FROM hw3.schedule WHERE student_id = newrow.student_id AND class_id = newrow.class_id)) > 0 ) THEN
--         SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
--     END IF;
-- END^


CREATE TRIGGER hw3.classcheck
NO CASCADE BEFORE INSERT ON HW3.SCHEDULE
FOR EACH ROW
BEGIN
    SELECT COUNT(*) FROM (hw3.class_prereq
    WHERE hw3.class_prereq.class_id = NEW.class_id
        AND hw3.class_prereq.prereq_id NOT IN (
            SELECT
                hw3.schedule.class_id
            FROM
                hw3.schedule
            WHERE
                HW3.SCHEDULE.Student_Id = NEW.Student_Id
                AND (hw3.schedule.grade <= hw3.class_prereq.req_grade)
        );
    IF (COUNT(*) > 0) THEN
        SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
    END IF;
END^


