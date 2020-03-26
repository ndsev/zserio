package sql_constraints.table_constraints;

sql_table TableConstraintsTable
{
    int32   primaryKey1 sql "NOT NULL";
    int32   primaryKey2 sql "NOT NULL";
    int32   uniqueValue1;
    int32   uniqueValue2;
    
    // spaces are intended
    sql "PRIMARY KEY (  primaryKey1 , primaryKey2 ) UNIQUE ( uniqueValue1 ,  uniqueValue2  )";
};
