package comments.documentation_comments;

/**
 * Traffic flow on links.
 */
enum bit:2 Direction
{
    /** No traffic flow allowed. */
    NONE,

    /** Traffic allowed from start to end node. */
    POSITIVE,

    /** Traffic allowed from end to start node. */
    NEGATIVE,

    /** Traffic allowed in both directions. */
    BOTH
};

/**
 * This is a structure which uses Direction enumeration type.
 *
 * For more information, please have a look to the
 * @see "documentation" Direction page.
 *
 * @see Direction
 *
 * @param hasExtraValue True if the structure has extra value.
 *
 * @todo Update this comment.
 *
 * @deprecated
 */
struct DirectionStructure(bool hasExtraValue)
{
    /** Direction. */
    Direction   direction;

    /** Optional extra value. */
    uint32      extraValue if hasExtraValue == true;
};

struct FieldComments
{
    /** Field comment. */
    int32 field;

    /** Optional field comment. */
    optional int32 optionalField1;
    optional
        /** Optional field comment. */
        int32 optionalField2;

    uint32 offset1;
    /** Field with offset comment. */
    offset1:
        int32 fieldWitOffset1;
    uint32 offset2;
    offset2:
        /** Field with offset comment. */
        int32 fieldWitOffset2;

    uint32 offsets1[];
    /** Array with indexed offset comment. */
    offsets1[@index]:
        int32 arrWithIndexedOffset1[];

    uint32 offsets2[];
    offsets2[@index]:
        /** Array with indexed offset comment. */
        int32 arrWithIndexedOffset2[];

    /** Aligned field comment. */
    align(16):
        int32 alignedField1;

    align(16):
        /** Aligned field comment. */
        int32 alignedField2;

    uint32 mixedOffset1;
    /** Mixed comment. */
    align(32):
    mixedOffset1:
        optional int32 mixedField1;

    uint32 mixedOffset2;
    align(32):
    /** Mixed comment. */
    mixedOffset2:
        optional int32 mixedField2;

    uint32 mixedOffset3;
    align(32):
    mixedOffset3:
        /** Mixed comment. */
        optional int32 mixedField3;

    uint32 mixedOffset4;
    align(32):
    mixedOffset4:
        optional /** Mixed comment. */ int32 mixedField4;
};

/** Sql table comment. */
sql_table Table
{
    /** Id comment. */
    int32 id sql "PRIMARY KEY";
};

/** Virtual table comment. */
sql_table VirtualTable using fts4aux
{
    /** Virutal field comment. */
    sql_virtual string term;

    sql "VirtualTable";
};

/** DB comment. */
sql_database Db
{
    /** Table field comment. */
    VirtualTable virtualTable;
};
