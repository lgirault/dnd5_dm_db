
project_root="/home/lorilan/projects/dnd5_dm_db"

source $project_root/target/CLASSPATH

export dnd5_dm_db_resources_dir="$project_root/src/main/resources"

export dnd5_dm_db_gen_all_out_dir="$project_root/src/main/resources"

export dnd5_dm_db_port="8080"

java -cp $CLASSPATH dnd5_dm_db.Server
