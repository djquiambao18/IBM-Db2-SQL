/*HW3.STUDENT
Student_Id:	char(6)		e.g. primary key
First:		varchar(15)	not null
Last:		varchar(15)	not null
Gender:		char(1)  		e.g. ( must be either M, F, or O)

HW3.CLASS
Class_Id:	char(6)		e.g. primary key
Name:		varchar(20)	not null
Desc:		varchar(20)	not null

HW3.CLASS_PREREQ
Class_Id:	char(6)		e.g. must exist in HW3.CLASS (e.g. foreign key)
Prereq_Id:	char(6)		e.g. must exist in HW3.CLASS, must not be the same as Class_ID (can’t pre-req itself) (e.g. foreign key)
Req_Grade:  char(1)		not null e.g. must be this grade or higher	

HW3.SCHEDULE
Student_Id:	char(6)		e.g. must exist in HW3.STUDENT (e.g. foreign key)
Class_Id:	char(6) 		e.g. must exist in HW3.CLASS (e.g. foreign key)
Semester:	int			e.g. (must be either 1 for Spring, 2 for Summer, or 3 for Fall)
Year:		int			e.g. (1950, …, 2013, 2014, …, 2022)
Grade:		char(1)		e.g. (must be either A, B, C, D, F, I, or W); nullable
*/

/*Assumptions:
-  Can only insert one row at a time into schedule.
-  If a class has 1 or more pre-req (e.g. CS160 has 3 pre-req), they must have a passing grade in all their pre-reqs in order to add their classes.
-  If a student is currently taking a class but has no grade yet, then you will reject the insert (e.g. you are in 157A and you try to register for 157B next semester, fail the insert based on the rules of this assignment).
- It is possible for a student to repeat a class.  In this case, they just need at least 1 passing grade in the list.
*/

-- disallow insertion of duplicate student_id in the Student table and duplicate class_id in the Class table (DDL Statement)
connect to cs157a^
CREATE TABLE hw3.student (
        student_id char (6) NOT NULL,
        first varchar (15) NOT NULL,
        last varchar (15) NOT NULL,
        gender char (1),
        CHECK(gender in ('M', 'F', 'O'))
        PRIMARY KEY(student_id)
)^

CREATE TABLE hw3.class (
    class_id char (6) NOT NULL,
    name varchar (20) NOT NULL,
    desc varchar (20) NOT NULL 
    PRIMARY KEY(class_id)
 )^


-- If a class is drop from the class table, cascade the delete to the child rows that are dependent on it.  (DDL statement)
CREATE TABLE hw3.class_prereq (
    class_id char (6) NOT NULL,
    prereq_id char (6) NOT NULL,
    req_grade char (1) NOT NULL,
    CHECK(req_grade in ('A', 'B', 'C', 'D', 'F', 'I', 'W')),
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    FOREIGN KEY(prereq_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    CHECK(prereq_id <> class_id)
    PRIMARY KEY(class_id, prereq_id)
)^


-- Disallow insertion of invalid student id or class id in the Schedule table. (DDL statement)
CREATE TABLE hw3.schedule (
    student_id char (6) NOT NULL,
    class_id char (6) NOT NULL,
    semester int NOT NULL,
    year int NOT NULL,
    grade char (1),
    CHECK(semester in (1, 2, 3)),
    CHECK(year in (1950, 1951, …, 2022)),
    CHECK(grade in ('A', 'B', 'C', 'D', 'F', 'I', 'W')),
    FOREIGN KEY(student_id) REFERENCES hw3.student(student_id) ON DELETE CASCADE,
    FOREIGN KEY(class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
    PRIMARY KEY(student_id, class_id)
)^


-- Disallow students to take a class without completing their pre-requisite(s) (Must use a trigger). The trigger must return an error message “Missing Pre-req” when trying to insert a class without its proper pre-req.
CREATE TRIGGER hw3.classcheck
NO CASCADE BEFORE INSERT ON hw3.schedule
REFERENCING NEW as newrow
FOR EACH ROW MODE DB2SQL
BEGIN ATOMIC
    DECLARE class_id_check char(6);
    DECLARE prereq_id_check char(6);
    DECLARE req_grade_check char(1);
    DECLARE grade_check char(1);
    DECLARE grade_check_result int;
    SELECT req_grade 
    -- IF EXISTS (SELECT * FROM hw3.class_prereq WHERE class_id = newrow.class_id AND NOT EXISTS (SELECT * FROM hw3.schedule WHERE student_id = newrow.student_id AND grade < )) THEN
    --     RAISE_APPLICATION_ERROR(-20001, 'Missing Pre-req');
    -- END IF;
END^

-- CREATE TRIGGER hw3.classcheck
-- BEFORE INSERT ON HW3.SCHEDULE
-- FOR EACH ROW
-- BEGIN
--     SELECT
--         COUNT(*)
--     FROM
--         HW3.CLASS_PREREQ
--     WHERE
--         HW3.CLASS_PREREQ.Class_Id = NEW.Class_Id
--         AND HW3.CLASS_PREREQ.Prereq_Id NOT IN (
--             SELECT
--                 HW3.SCHEDULE.Class_Id
--             FROM
--                 HW3.SCHEDULE
--             WHERE
--                 HW3.SCHEDULE.Student_Id = NEW.Student_Id
--                 AND HW3.SCHEDULE.Grade = 'A'
--         );
--     IF (COUNT(*) > 0) THEN
--         RAISE_APPLICATION_ERROR(-20001, 'Missing Pre-req');
--     END IF;
-- END;


