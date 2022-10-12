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
    /*! Traffic allowed from start to end node. !*/
    case POSITIVE:
        /*! Value which contains 8-bit unsigned integer. !*/
        uint8 positiveValue;

    /*! Traffic allowed from end to start node. !*/
    case NEGATIVE:
        /*! Value which contains 32-bit signed integer. !*/
        int32 negativeValue;

    /*! Traffic allowed in both directions. !*/
    case BOTH:
    /*! No direction at all. !*/
    case NONE:
        /*! Value which contains 64-bit unsigned integer. !*/
        uint64 value;

    /*! Default. !*/
    default:
        ;
};
