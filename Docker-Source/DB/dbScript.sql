/*DROP DATABASE IF EXISTS `user_db`;*/

/*CREATE DATABASE IF NOT EXISTS `user_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;*/
USE `user_db`;

CREATE TABLE usr_User (
  id bigint(200) AUTO_INCREMENT NOT NULL,
  fname varchar(255) NOT NULL,
  lname varchar(255) NOT NULL,
  initials varchar(255) DEFAULT NULL,
  dob date DEFAULT NULL,
  phoneNo varchar(10) DEFAULT NULL,
  gender varchar(1) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  isActive BOOLEAN NOT NULL,
  isDelete BOOLEAN NOT NULL,
  CONSTRAINT usr_User_pk PRIMARY KEY(id)
);

CREATE TABLE usr_Login (
  userId bigint(200) DEFAULT NULL,
  email varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  roleId bigint(200) DEFAULT NULL,
  isConfirmed int(1) NOT NULL,
  confirmationCode varchar(8) NOT NULL,
  loginCount bigint(200) NOT NULL,
  CONSTRAINT usr_login_pk PRIMARY KEY(email),
  CONSTRAINT usr_login_fk FOREIGN KEY(userId) REFERENCES usr_User(id)
);

CREATE TABLE usr_Role (
  id bigint(200) AUTO_INCREMENT NOT NULL,
  roleName varchar(255) NOT NULL,
  roleDesc varchar(255) DEFAULT NULL,
  isActive BOOLEAN NOT NULL,
  CONSTRAINT usr_role_pk PRIMARY KEY(id)
);

INSERT INTO usr_Role (id, roleName, roleDesc, isActive) VALUES
(0, 'Admin', 'Admin', 1),
(0, 'Manager', 'Manager', 1),
(0, 'Customer', 'Customer', 1);

/*Start Of Procedures*/
    /*Start Of Login Management*/

        /*Login count increment*/
                delimiter //

                    CREATE PROCEDURE login_increaseLoginCount(
                        IN id bigint(200),
                        OUT status int
                        )
                        BEGIN
                            /*Start Error Handling*/
                                DECLARE EXIT HANDLER FOR SQLEXCEPTION
                                BEGIN
                                    ROLLBACK;
                                    SET status = 0;
                                    SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                                END;
                            /*End Error Handling*/

                            START TRANSACTION;

                                UPDATE usr_Login
                                SET usr_Login.loginCount = loginCount + 1
                                WHERE usr_Login.userId = id;
                                SET status = 1;   

                            COMMIT;
                        END//
                delimiter ;

    /*End Of Login Management*/

    /*Start Of User Management*/

        /* User Registration */

            delimiter //

            CREATE PROCEDURE user_Registration(
                /*usr_User*/
                IN fname varchar(255),
                IN lname varchar(255),
                IN initials varchar(255),
                IN dob date,
                IN phoneNo varchar(10),
                IN gender varchar(1),
                IN address varchar(255),
                /*usr_Login*/
                IN email varchar(255),
                IN password varchar(255),
                IN roleId bigint(200),
                IN confirmationCode varchar(8),
                OUT status int
                )
                BEGIN
                    DECLARE userID bigint(200);
                    /*Start Error Handling*/
                        DECLARE EXIT HANDLER FOR 1062
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT CONCAT('Duplicate key occurred') AS message;
                        END;

                        DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                        END;
                    /*End Error Handling*/

                    
                    START TRANSACTION;

                        INSERT INTO usr_User VALUES(0, fname, lname, initials, dob, phoneNo, gender, address, 1, 0);
                        SELECT MAX(id) INTO userID FROM usr_User;
                        INSERT INTO usr_Login VALUES(userID, email, password, roleId, 1, confirmationCode, 0);
                        SET status = 1;

                    COMMIT;
                
                END//
            delimiter ;

        /*Update User Details*/
            delimiter //

                CREATE PROCEDURE user_updateUserDetails(
                    IN fname varchar(255),
                    IN lname varchar(255),
                    IN initials varchar(255),
                    IN dob date,
                    IN phoneNo varchar(10),
                    IN gender varchar(1),
                    IN address varchar(255),
                    IN id bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            UPDATE usr_User
                            SET usr_User.fname = fname,
                            usr_User.lname = lname,
                            usr_User.initials = initials,
                            usr_User.dob = dob,
                            usr_User.phoneNo = phoneNo,
                            usr_User.gender = gender,
                            usr_User.address = address
                            WHERE usr_User.id = id;
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;

        /*Delete User*/
            delimiter //

                CREATE PROCEDURE user_deleteUser(
                    IN id bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            UPDATE usr_User
                            set usr_User.isDelete = 1, usr_User.isActive = 0
                            WHERE usr_User.id = id;
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;

        /*Change Password*/
            delimiter //

            CREATE PROCEDURE user_changePassword(
                IN password varchar(255),
                IN email varchar(255),
                OUT status int
                )
                BEGIN
                    /*Start Error Handling*/
                        DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                        END;
                    /*End Error Handling*/

                    
                    START TRANSACTION;

                        UPDATE usr_Login
                        SET usr_Login.password = password
                        WHERE usr_Login.email = email;
                        SET status = 1;

                    COMMIT;
                
                END//
                delimiter ;

        /*Forgot Password*/
            delimiter //

                CREATE PROCEDURE user_forgotPassword(
                    IN password varchar(255),
                    IN userId bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            set usr_Login.password = password
                            WHERE usr_Login.userId = userId;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

        /*Email Confirmation*/
            delimiter //

                CREATE PROCEDURE user_confirmEmailConfirmation(
                    IN email varchar(255),
                    IN confirmationCode varchar(255),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            SET usr_Login.isConfirmed = 1
                            WHERE usr_Login.email = email and usr_Login.confirmationCode = confirmationCode;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

        /*Request Email Confirmation Code*/
            delimiter //

                CREATE PROCEDURE user_requestEmailConfirmationCode(
                    IN confirmationCode varchar(255),
                    IN email varchar(255),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            SET usr_Login.confirmationCode = confirmationCode
                            WHERE usr_Login.email = email;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

    /*End Of User Management*/

  /*End Of Procedures*/
