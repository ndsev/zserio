package comments.markdown_doc.bitmask_comments;

/*!

**Permission**

Permissions on file.

!*/
bitmask bit:2 Permission
{
    /*! Reading from file allowed. !*/
    READ,

    /*! Writing to file allowed. !*/
    WRITE,

    /*! Both reading and writing allowed. !*/
    READ_WRITE = 3
};
