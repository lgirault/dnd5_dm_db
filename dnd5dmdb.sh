
project_root=/home/lorilan/projects/dnd5_dm_db

source $project_root/target/CLASSPATH.bat

export dnd5_dm_db_resources_dir=$project_root/src/universal

export dnd5_dm_db_gen_all_out_dir=$project_root/out

export dnd5_dm_db_port="8080"

java -cp $CLASSPATH dnd5_dm_db.Server
