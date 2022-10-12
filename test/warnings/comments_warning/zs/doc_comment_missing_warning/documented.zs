/** Same as root package just to check that documented compatibility version doesn't fire warning. */
zserio_compatibility_version("2.7.0");

/** Package which checks that documented nodes don't fire warnings. */
package doc_comment_missing_warning.documented;

/** Documented bitmask type. */
bitmask uint8 DocumentedBitmaskType
{
    /** Documented bitmask value. */
    BITMASK_VALUE
};

/** Documented enum type. */
enum uint8 DocumentedEnumType
{
    /*! Documentd enum item. !*/
    ENUM_ITEM
};

/** Documented constant. */
const string DOCUMENTED_CONSTANT = "constant";

/** Documented structure type. */
struct DocumentedStructureType<T>
{
    /** Documented structure field. */
    T field;
};

/** Documented instantiate type. */
instantiate DocumentedStructureType<uint32> DocumentedStructureTypeU32;
/*! Documented instantiate type. !*/
instantiate DocumentedStructureType<string> DocumentedStructureTypeSTR;

/** Documented subtype. */
subtype uint32 DocumentedSubtype;

/** Documented choice type. */
choice DocumentedChoiceType(int32 selector) on selector
{
    /** Documentd case expression. */
    case 0:
        /** Documented choice field. */
        uint32 field;
    /** Documented choice default. */
    default:
        ;

    /** Documented function. */
    function uint32 getField()
    {
        return selector == 0 ? field : 0;
    }
};

/*! Documented union type. !*/
union DocumentedUnionType
{
    /** Documented union field. */
    uint32 fieldU32;
    /*! Documented union field. !*/
    string fieldSTR;
};

/*!
Documented SQL table type.
!*/
sql_table DocumentedSqlTableType
{
    /** Documented column. */
    uint32 id sql "PRIMARY KEY NOT NULL";
};

/**
 * Documented SQL database type.
 */
sql_database DocumentedSqlDatabaseType
{
    /** Documented table. */
    DocumentedSqlTableType sqlTable;
};

/**
 * Documented rule group.
 */
rule_group DocumentedRules
{
    /*!
    Documented single rule.
    !*/
    rule "documented-test-rule";
};

/** Documented service type. */
service DocumentdServiceType
{
    /** Documented method. */
    DocumentedStructureTypeU32 serviceMethod(DocumentedStructureTypeSTR);
};

/** Documented pubsub type. */
pubsub DocumentedPubsubType
{
    /*! Documented message. !*/
    topic("pubsub/message") DocumentedStructureTypeU32 pubsubMessage;
};
