DROP TABLE IF EXISTS todo;
CREATE TABLE todo (id INT IDENTITY PRIMARY KEY, description VARCHAR(255), details VARCHAR(4096), done BIT);

--INSERT INTO todo ([description],[details],[done])
--     VALUES
--           ('test1','testdescription',0),
--		   ('test2','test2description',1)