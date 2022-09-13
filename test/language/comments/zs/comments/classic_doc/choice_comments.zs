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
    case POSITIVE:
         /** Traffic allowed from start to end node. */
         uint8 positiveValue;

    case NEGATIVE:
        /** Traffic allowed from end to start node. */
        int8 negativeValue;

    case BOTH:
        /** Traffic allowed in both directions. */
        uint16 value;

    case NONE:
        /** No direction at all. */
        bool isNone;

    default:
        ;
};
