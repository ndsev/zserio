package comments_warning.markdown_comment_with_wrong_terminator;

/*! Floating single line comment with wrong terminator. */

/*! Floating single line comment correctly terminated. !*/

/*!
Floating multiline comment
with wrong terminator.
*/

/*!
Floating multiline comment
correctly terminated.
!*/

/**
 * Classic comment.
 */

/*!
Sticky multiline comment with wrong terminator
*/
struct TestStruct
{
    /*! Field single line comment with wrong terminator */
    uint32 field;

    /*! Correctly temrinated field comment !*/
    string strField;
};

/*! Correctly terminated sticky comment. !*/
struct OtherTestStruct
{
    /** Classic field comment */
    uint32 iField;
    /*!
    Markdown multiline comment
    with wrong terminator.
    */
    float32 fField;
    /**
    Markdown multiline comment
    correctly terminated.
    */
    string sField;
};

sql_table TestTable
{
    int32 id sql "PRIMARY KEY NOT NULL";
    TestStruct testStruct;
    OtherTestStruct otherTestStruct;
};

sql_database TestDatabase
{
    TestTable testTable;
};

