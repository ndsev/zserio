package comments.markdown_doc.union_comments;

import comments.markdown_doc.enum_comments.Direction;

/*!

**TestUnion**

This is an union which uses constraint in one case.

See [Direction](../markdown_doc/enum_comments.zs#Direction) page.

**case1Allowed** True if case1Field is allowed.

[] Update this comment.

**Deprecated!**

!*/
union TestUnion(bool case1Allowed)
{
    /*! This is a comment for **case1Field**. !*/
    int32     case1Field : case1Allowed;

    /*! This is a comment for **case2Field**. !*/
    Direction case2Field;

    /*! This is a comment for **case3Field**. !*/
    int8      case3Field;
};
