--
-- db2 -td"@" -f p2.sql
--
CONNECT TO CS157A@
--
--
DROP PROCEDURE P2.CUST_CRT@
DROP PROCEDURE P2.CUST_LOGIN@
DROP PROCEDURE P2.ACCT_OPN@
DROP PROCEDURE P2.ACCT_CLS@
DROP PROCEDURE P2.ACCT_DEP@
DROP PROCEDURE P2.ACCT_WTH@
DROP PROCEDURE P2.ACCT_TRX@
DROP PROCEDURE P2.ADD_INTEREST@
--
--
CREATE PROCEDURE P2.CUST_CRT
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    IF p_gender != 'M' AND p_gender != 'F' THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid gender';
    ELSEIF p_age <= 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid age';
    ELSEIF p_pin < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid pin';
    ELSE
      INSERT INTO p2.customer (Name, Gender, Age, Pin) VALUES (p_name, p_gender, p_age, p2.encrypt(p_pin));
      SET err_msg = id;
      SET sql_code = 0;
    END IF;
END@
--

CREATE PROCEDURE P2.CUST_LOGIN
(IN p_pin INTEGER, IN p_id INTEGER, OUT valid BINARY, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    IF p_pin < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid pin';
    ELSEIF p_id < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid id';
    ELSE
      SELECT COUNT(*) INTO valid FROM p2.customer WHERE Pin = p2.decrypt(p_pin) AND Id = p_id;
      SET sql_code = 0;
      SET err_msg = valid;
    END IF;
END@
TERMINATE@
--
--
