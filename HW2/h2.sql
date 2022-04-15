--h2.sql hw2 Darwish Quiambao
Connect to sample;
--Q1
SELECT id, name, years FROM staff WHERE years>=10 ORDER BY years desc;
--Q2
SELECT id, name, comm + salary AS total_compensation FROM staff WHERE comm NOTNULL ORDER BY total_compensation desc;
--Q3
SELECT id, name, salary FROM staff WHERE comm IS NULL ORDER BY salary asc LIMIT 5;
--Q4
SELECT deptname, location, name FROM staff JOIN org ON staff.dept = org.deptnumb WHERE deptname IN ('Mountain', 'Plains', 'New England') ORDER BY deptname, name;
--Q5
SELECT job, count(*) AS employee_count FROM staff GROUP BY job ORDER BY employee_count desc;
--Q6
SELECT division, count(*) AS employee_count FROM staff JOIN org on staff.dept = org.deptnumb GROUP BY division ORDER BY employee_count ASC;
terminate;