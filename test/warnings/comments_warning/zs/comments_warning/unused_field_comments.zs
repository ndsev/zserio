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

struct ByOptionalAndType
{
    /** Used comment. */
    optional
    /** Used comment. */ int32 field;
};

struct ByOffsetAndType
{
    uint32 offset;
    /** Used comment. */
    offset:
    /** Used comment. */ int32 field;
};

// check that indexed offsets comments work
struct ByIndexedOffsetAndType
{
    uint32 offsetArray[];
    /** Used comment. */
    offsetArray[@index]:
        /** Used comment. */
        int32 array[];
};

struct ByAlignmentAndType
{
    /** Used comment. */
    align(5):
    /** Used comment. */ int32 field;
};

// comments masked by higher priority positions
struct MultipleComments
{
    uint32 offset1;
    /** Used comment. */
    align(5):
        /** Used comment. */
    offset1:
        /** Used comment. */
        optional /** Used comment. */ int32 /** Unused */ field1;

    uint32 offset2;
    /** Used comment. */
    offset2:
        /** Used comment. */
        optional /** Used comment. */ int32 /*! # Unused markdown comment !*/ field2;

    /** Used comment. */
    optional /** Used comment. */ int32 field3;
};

// check that comments are not masked by missing comments on higher priority positions
struct MultipleUnmaskedComments
{
    uint32 offset1;
    align(5):
        /** Used comment. */
    offset1:
        /** Used comment. */
        optional /** Used comment. */ int32 field1;

    uint32 offset2;
    align(5):
    offset2:
        /** Used comment. */
        optional /** Used comment. */ int32 field2;

    uint32 offset3;
    align(5):
    offset3:
        optional /** Used comment. */ int32 field3;
};

struct MultipleFieldComments
{
    /*! Used markdown comment. !*/
    /** Used comment. */
    /** Used comment. */
    int32 field;
};

sql_table Table
{
    int32 id sql "PRIMARY KEY NOT NULL";
    BasicComment basicComment;
    UnusedCommentById commentById;
    ByOptionalAndType maskedByOptional;
    ByOffsetAndType maskedByOffset;
    ByIndexedOffsetAndType maskedByIndexedOffset;
    ByAlignmentAndType maskedByAlignment;
    MultipleComments multipleMaskedComments;
    MultipleUnmaskedComments multipleUnmaskedComments;
    MultipleFieldComments multipleFieldsComments;
};

sql_database Database
{
    Table table;
};
