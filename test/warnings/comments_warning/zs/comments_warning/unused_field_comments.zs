package comments_warning.unused_field_comments;

struct BasicComment
{
    /** Used comment. */
    int32 field;
};

struct UnusedCommentById
{
    int32 /** Unused comment. */ field;
};

struct MaskedByOptional
{
    /** Used comment. */
    optional
    /** Unused comment. */ int32 field;
};

struct MaskedByOffset
{
    uint32 offset;
    /** Used comment. */
    offset:
    /** Unused comment. */ int32 field;
};

// check that indexed offsets comments work
struct MaskedByIndexedOffset
{
    uint32 offsetArray[];
    /** Used comment. */
    offsetArray[@index]:
        /** Unused comment. */
        int32 array[];
};

struct MaskedByAlignment
{
    /** Used comment. */
    align(5):
    /** Unused comment. */ int32 field;
};

// comments masked by higher priority positions
struct MultipleMaskedComments
{
    uint32 offset1;
    /** Used comment. */
    align(5):
        /** Unused comment. */
    offset1:
        /** Unused comment. */
        optional /** Unused comment. */ int32 field1;

    uint32 offset2;
    /** Used comment. */
    offset2:
        /** Unused comment. */
        optional /** Unused comment. */ int32 field2;

    /** Used comment. */
    optional /** Unused comment. */ int32 field3;
};

// check that comments are not masked by missing comments on higher priority positions
struct MultipleUnmaskedComments
{
    uint32 offset1;
    align(5):
        /** Used comment. */
    offset1:
        /** Unused comment. */
        optional /** Unused comment. */ int32 field1;

    uint32 offset2;
    align(5):
    offset2:
        /** Used comment. */
        optional /** Unused comment. */ int32 field2;

    uint32 offset3;
    align(5):
    offset3:
        optional /** Used comment. */ int32 field3;
};

struct MultipleFieldComments
{
    /** Unused comment. */
    /** Used comment. */
    int32 field;
};

sql_table Table
{
    int32 id sql "PRIMARY KEY";
    BasicComment basicComment;
    UnusedCommentById commentById;
    MaskedByOptional maskedByOptional;
    MaskedByOffset maskedByOffset;
    MaskedByIndexedOffset maskedByIndexedOffset;
    MaskedByAlignment maskedByAlignment;
    MultipleMaskedComments multipleMaskedComments;
    MultipleUnmaskedComments multipleUnmaskedComments;
    MultipleFieldComments multipleFieldsComments;
};

sql_database Database
{
    Table table;
};
