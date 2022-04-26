# Based on Sample README.txt

0. Open the Adminstrator DB2 Command Window (on Windows) and add your own Java compiler path e.g. set PATH=%PATH%;c:\Java173\bin;

For Mac, open a non-DB2 terminal.

1. Compile the java (JDBC) program sample.java

javac *.java

2. Replace the parameters in the db.properties file (The given file has all the default, if you follow the original instructions, then you probably don't need to change anything)

<url> = Replace you connection IP address and port # followed by your DB_NAME (e.g. SAMPLE or CS157A)

<username> = db username (should be your windows login id that was used during the DB2 Setup)

<password> = password for the above db username

3. Call on the compiled java program with the properties file as a parameter.  First download the db2jcc4.jar to your current directory, then run the command below (if you are on Window change the forward / to backward \ )

java -cp ":./db2jcc4.jar" P1 db.properties


For Windows, replace the : with a ;
java -cp ";./db2jcc4.jar" P1 db.properties

For bash terminal:

$ java -cp ";./db2jcc4.jar" P1 ./db.properties