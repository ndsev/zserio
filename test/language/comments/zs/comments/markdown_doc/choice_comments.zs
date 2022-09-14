package comments.markdown_doc.choice_comments;

import comments.markdown_doc.enum_comments.Direction;

/*!

**TestChoice**

This is a a choice which uses enumeration.

See [Direction](enum_comments.zs#Direction) page.

**direction** Direction to be used for choice cases.

[] Update this comment.

**Deprecated!**

!*/
choice TestChoice(Direction direction) on direction
{
    case POSITIVE:
        /*! Traffic allowed from start to end node. !*/
        uint8 positiveValue;

    case NEGATIVE:
        /*! Traffic allowed from end to start node. !*/
        int32 negativeValue;

    case BOTH:
        /*! Traffic allowed in both directions. !*/
        uint64 value;

    case NONE:
        /*! No direction at all. !*/
        bool isNone;

    default:
        ;
};
