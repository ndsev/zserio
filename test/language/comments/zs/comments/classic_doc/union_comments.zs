package comments.classic_doc.union_comments;

import comments.classic_doc.enum_comments.Direction;

/**
 * This is a enumeration which uses constraint in one case.
 *
 * For more information, please have a look to the @see "documentation" Direction page.
 *
 * @see Direction
 *
 * @param case1Allowed True if case1Field is allowed.
 *
 * @todo Update this comment.
 *
 * @deprecated
 */
union TestUnion(bool case1Allowed)
{
    /** This is a comment for case1Field. */
    int32     case1Field : case1Allowed;

    /** This is a comment for case2Field. */
    Direction case2Field;

    /** This is a comment for case3Field. */
    int8      case3Field;
};
