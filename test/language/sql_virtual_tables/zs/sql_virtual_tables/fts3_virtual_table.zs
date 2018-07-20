package sql_virtual_tables.fts3_virtual_table;

sql_table Fts3VirtualTable using fts3
{
    string      title;
    string      body;
};

sql_database Fts3TestDb
{
    Fts3VirtualTable    fts3VirtualTable;
};
