package comments.classic_doc.choice_comments;

import comments.classic_doc.enum_comments.Direction;

/**
 * This is a choice which uses enumeration.
 *
 * For more information, please have a look
 * to the @see "documentation" comments.classic_doc.enum_comments.Direction page.
 *
 * @see "Direction" comments.classic_doc.enum_comments.Direction
 *
 * @param direction Direction to be used for choice cases.
 *
 * @todo Update this comment.
 *
 * @deprecated
 */
choice TestChoice(Direction direction) on direction
{
    /** Traffic allowed from start to end node. */
    case POSITIVE:
        /** Value which contains 8-bit unsigned integer. */
        uint8 positiveValue;

    /** Traffic allowed from end to start node. */
    case NEGATIVE:
        /** Value which contains 32-bit signed integer. */
        int32 negativeValue;

    /** Traffic allowed in both directions. */
    case BOTH:
    /** No direction at all. */
    case NONE:
        /** Value which contains 64-bit unsigned integer. */
        uint64 value;

    /** Default. */
    default:
        ;
};
