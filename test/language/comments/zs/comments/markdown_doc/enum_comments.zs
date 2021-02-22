package comments.markdown_doc.enum_comments;

/*!

**Direction**

Traffic flow on links.

!*/
enum bit:2 Direction
{
    /*! No traffic flow allowed. !*/
    NONE,

    /*! Traffic allowed from start to end node. !*/
    POSITIVE,

    /*! Traffic allowed from end to start node. !*/
    NEGATIVE,

    /*! Traffic allowed in both directions. !*/
    BOTH
};
