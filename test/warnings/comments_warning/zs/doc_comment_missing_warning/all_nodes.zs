package doc_comment_missing_warning.all_nodes;

bitmask uint8 BitmaskType
{
    BITMASK_VALUE
};

enum uint8 EnumType
{
    ENUM_ITEM
};

const string CONSTANT = "constant";

struct StructureType<T>
{
    T field;
};

instantiate StructureType<uint32> StructureTypeU32;
instantiate StructureType<string> StructureTypeSTR;

subtype uint32 Subtype;

choice ChoiceType(int32 selector) on selector
{
    case 0:
        uint32 field;
    default:
        ;

    function uint32 getField()
    {
        return selector == 0 ? field : 0;
    }
};

union UnionType
{
    uint32 fieldU32;
    string fieldSTR;
};

sql_table SqlTableType
{
    uint32 id sql "PRIMARY KEY NOT NULL";
};

sql_database SqlDatabaseType
{
    SqlTableType sqlTable;
};

rule_group Rules
{
    rule "test-rule";
};

service ServiceType
{
    StructureTypeU32 serviceMethod(StructureTypeSTR);
};

pubsub PubsubType
{
    topic("pubsub/message") StructureTypeU32 pubsubMessage;
};
