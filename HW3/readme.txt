If not yet done, use command to create database inside container: db2 "create db cs157a"

Then, connect using: db2 connect to cs157a

Run the drop.sql by: db2 -td"^" -f drop.sql
Afterwards, run create.sql: db2 -td"^" -f create.sql

Then, run the test sql to populate the tables: db2 -tvf test.sql