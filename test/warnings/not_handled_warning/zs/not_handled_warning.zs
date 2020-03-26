package not_handled_warning;

enum uint8 Selector
{
    BLACK,
    WHITE,
    GREY,
    RED
};

subtype int8  Black;
subtype int16 Grey;

// This should fire not handled warnings for WHITE and RED enumeration items.
choice EnumParamChoice(Selector selector) on selector
{
    case Selector.BLACK:
        Black black;

    case GREY:
        Grey grey;
};

// The following is just to kill not_used warning.
sql_table NotUsedTable
{
    int32                       id  sql "PRIMARY KEY NOT NULL";
    Selector                    selector;
    EnumParamChoice(selector)   enumParamChoice;
};

sql_database NotUsedDatabase
{
    NotUsedTable    notUsedTable;
};
