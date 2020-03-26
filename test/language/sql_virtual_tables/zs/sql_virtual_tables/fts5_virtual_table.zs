package sql_virtual_tables.fts5_virtual_table;

sql_table Fts5VirtualTable using fts5
{
    string      title;
    string      body;

    sql "tokenize=\"unicode61 remove_diacritics 0 tokenchars '\u001a\u0019'\"";
 };

sql_database Fts5TestDb
{
    Fts5VirtualTable    fts5VirtualTable;
};
