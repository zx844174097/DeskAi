#各种查询语句
select_last_id : select last_insert_rowid();
is_table : SELECT name from sqlite_master where type='table' and `name` =?
#查询所有的表
table_names : SELECT name from sqlite_master where type='table' 
clear_table_data : TRUNCATE  table '<table_name>' ; 