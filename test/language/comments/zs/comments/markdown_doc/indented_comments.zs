package comments.markdown_doc.indented_comments;

    /*!

    **Direction**

    This is indented markdown comment. Markdown renderer is sensitive and this should not be indented.

    Enum Item  | Description
    -----------| ----------------------------------------------------------------------------
    NONE       | No traffic flow allowed.
    POSITIVE   | Traffic allowed from start to end node.
    NEGATIVE   | Traffic allowed from end to start node.
    BOTH       | Traffic allowed in both directions.

    !*/
enum bit:2 Direction
{
    /*! No traffic flow allowed. */
    NONE,

    /*! Traffic allowed from start to end node. */
    POSITIVE,

    /*! Traffic allowed from end to start node. */
    NEGATIVE,

    /*! Traffic allowed in both directions. */
    BOTH
};
