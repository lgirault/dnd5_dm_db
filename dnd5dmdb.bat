set project_root=C:\dnd5_dm_db-master\

call %project_root%\target\CLASSPATH

SET dnd5_dm_db_resources_dir=%project_root%\src\universal

SET dnd5_dm_db_gen_all_out_dir=%project_root%\out

SET dnd5_dm_db_port=8080

java -cp %CLASSPATH% dnd5_dm_db.Server
