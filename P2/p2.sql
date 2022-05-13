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
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg VARCHAR(100))
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
      INSERT INTO p2.customer (p2.customer.Name, p2.customer.Gender, p2.customer.Age, p2.customer.Pin) VALUES (p_name, p_gender, p_age, p2.encrypt(p_pin));
      -- DECLARE id_cust INTEGER;
      -- DECLARE c1 cursor for
      SELECT ID into id FROM P2.CUSTOMER WHERE p2.customer.Name = p_name AND p2.decrypt(p2.customer.Pin) = p_pin;
      -- OPEN c1;
      -- FETCH c1 into err_msg;
      -- CLOSE c1;
      -- SET err_msg = id_cust;
      SET sql_code = 0;
    END IF;
END@
--

CREATE PROCEDURE P2.CUST_LOGIN
(IN p_id INTEGER, IN p_pin INTEGER, OUT valid INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE cust_count INTEGER;
    IF p_pin < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid pin';
    ELSEIF p_id < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid id';
    ELSE
      SELECT COUNT(*) INTO cust_count FROM p2.customer WHERE p2.decrypt(p2.customer.Pin) = p_pin AND p2.customer.ID = p_id;
      IF cust_count = 1 THEN
        SET valid = 1;
        SET sql_code = 0;
      ELSE
        SET sql_code = -100;
        SET valid = 0;
        SET err_msg = 'Incorrect id or pin';
      END IF;
    END IF;
END@

--
--

--Section B:  User Defined Functions – Two UDFs are created in create.clp for you.
-- P2.encrypt (pin integer): Must be used in the CUST_CRT (…) as we only store encrypted PW.
-- P2.decrypt (pin integer): Must be used in CUST_LOGIN (…) to decrypt/verify the encrypted PW.

-- Section C:  SQL/PL Stored Procedures:

-- 1.  CUST_CRT (Name, Gender, Age, Pin, ID, sqlcode, err_msg)
-- 2.  CUST_LOGIN (ID, Pin, Valid, sqlcode, err_msg)  (Valid = 1 if match, 0 for failure)
-- 3.  ACCT_OPN (ID, Balance, Type, Number, sqlcode, err_msg)
-- 4.  ACCT_CLS (Number, sqlcode, err_msg)
-- 5.  ACCT_DEP (Number, Amt, sqlcode, err_msg)
-- 6.  ACCT_WTH (Number, Amt, sqlcode, err_msg)
-- 7.  ACCT_TRX (Src_Acct, Dest_Acct, Amt, sqlcode, err_msg)
-- 8.  ADD_INTEREST (Savings_Rate, Checking_Rate, sqlcode, err_msg)

CREATE PROCEDURE P2.ACCT_OPN
(IN p_id INTEGER, IN p_balance INTEGER, IN p_type CHAR(1), OUT p_number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE cust_id INTEGER;
    IF p_id < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid id';
    ELSEIF p_balance < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid balance';
    ELSEIF p_type != 'S' AND p_type != 'C' THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid type';
    ELSE
      SELECT ID INTO cust_id FROM p2.customer WHERE Id = p_id;
      IF cust_id IS NULL THEN
        SET sql_code = -100;
        SET err_msg = 'Invalid id';
      ELSE
        INSERT INTO p2.account (p2.account.ID, p2.account.Balance, p2.account.Type, p2.account.Status) VALUES (cust_id, p_balance, p_type, 'A');
        SELECT Number INTO p_number FROM p2.ACCOUNT WHERE ID = p_id AND Type = p_type;
        SET sql_code = 0;
      END IF;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_CLS
(IN p_number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE temp_num INTEGER;
    IF p_number < 1000 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSE
      SELECT Number INTO temp_num FROM p2.ACCOUNT WHERE Number = p_number AND Status = 'A';
      IF temp_num IS NULL THEN
        SET sql_code = -100;
        SET err_msg = 'Invalid account number';
      ELSE
        UPDATE p2.account SET Status = 'I', Balance = 0 WHERE Number = p_number AND Status = 'A';
        SET sql_code = 0;
      END IF;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_DEP
(IN p_number INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE temp_num INTEGER;
    IF p_number < 1000 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSEIF p_amt < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid amount';
    ELSE
      SELECT Number INTO temp_num FROM p2.account WHERE Number = p_number AND Status = 'A';
      IF temp_num IS NULL THEN
        SET sql_code = -100;
        SET err_msg = 'Invalid account number';
      ELSE
        UPDATE p2.account SET Balance = Balance + p_amt WHERE Number = p_number AND Status = 'A';
        SET sql_code = 0;
      END IF;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_WTH
(IN p_number INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE temp_num INTEGER;
    DECLARE temp_amt INTEGER;
    IF p_number < 1000 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSEIF p_amt < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid amount';
    ELSE
      SELECT Number INTO temp_num FROM P2.ACCOUNT Where Number = p_number AND Status = 'A';
      IF temp_num IS NULL THEN
        SET sql_code = -100;
        SET err_msg = 'Invalid account number';
      ELSE
        SELECT Balance INTO temp_amt FROM p2.account WHERE Number = p_number AND Status = 'A';
        IF temp_amt < p_amt THEN
          SET sql_code = -100;
          SET err_msg = 'Insufficient funds';
        ELSE
          UPDATE p2.account SET Balance = Balance - p_amt WHERE Number = p_number AND Status = 'A';
          SET sql_code = 0;
        END IF;
      END IF;
    END IF;
END@

-- FOR P2.ACCT_TRX, Use Stored procedures P2.ACCT_WTH then P2.ACCT_DEP to transfer balance.
CREATE PROCEDURE P2.ACCT_TRX
(IN p_src_acct INTEGER, IN p_dest_acct INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE s_code INTEGER;
    DECLARE e_msg CHAR(100);
    DECLARE acct_src INTEGER;
    DECLARE acct_dest INTEGER;
    DECLARE p_src_bal INTEGER;
    DECLARE p_dest_bal INTEGER;
    IF p_src_acct < 1000 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid source account number';
    ELSEIF p_dest_acct < 1000 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid destination account number';
    ELSEIF p_src_acct = p_dest_acct THEN
      SET sql_code = -100;
      SET err_msg = 'Source and destination accounts cannot be the same';
    ELSEIF p_amt < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid amount';
    ELSE
      SELECT Number INTO acct_src FROM p2.account WHERE Number = p_src_acct AND Status = 'A' AND p_src_acct <> p_dest_acct;
      IF acct_src IS NULL THEN
        SET sql_code = -100;
        SET err_msg = 'Invalid source account number';
      ELSE
        SELECT Balance INTO p_src_bal FROM p2.account WHERE Number = p_src_acct AND Status = 'A' AND p_src_acct <> p_dest_acct;
        IF p_src_bal < p_amt THEN
          SET sql_code = -100;
          SET err_msg = 'Insufficient funds';
        ELSE
          SELECT Number INTO acct_dest FROM p2.account WHERE Number = p_dest_acct AND Status = 'A' AND p_src_acct <> p_dest_acct;
          IF acct_dest IS NULL THEN
            SET sql_code = -100;
            SET err_msg = 'Invalid destination account number';
          ELSE
            SELECT Balance INTO p_dest_bal FROM p2.account WHERE Number = p_dest_acct AND Status = 'A' AND p_src_acct <> p_dest_acct;   
            IF p_src_bal < p_amt THEN
              SET sql_code = -100;
              SET err_msg = 'Insufficient funds';
            ELSE
              CALL P2.ACCT_WTH(p_src_acct, p_amt, s_code, e_msg);
              IF s_code <> 0 THEN
                SET sql_code = s_code;
                SET err_msg = e_msg;
              ELSE
                CALL P2.ACCT_DEP(p_dest_acct, p_amt, s_code, e_msg);
                IF s_code <> 0 THEN
                  SET sql_code = s_code;
                  SET err_msg = e_msg;
                END IF;
              END IF;
            END IF;
          END IF;
        END IF;
        SET sql_code = 0;
      END IF;
    END IF;
END@

CREATE PROCEDURE P2.ADD_INTEREST
(IN p_savings_rate FLOAT, IN p_checking_rate FLOAT, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    UPDATE p2.account SET Balance = Balance + (Balance * p_savings_rate) WHERE Type = 'S' AND Status = 'A';
    UPDATE p2.account SET Balance = Balance + (Balance * p_checking_rate) WHERE Type = 'C' AND Status = 'A';
    SET sql_code = 0;
END@

TERMINATE@