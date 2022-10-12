package comments.mixed_doc.struct_comments;

/*!

**Direction**

Traffic flow on links.

!*/

/**
 * This is additional classic documentation comment.
 */
enum bit:2 Direction
{
    /*! No traffic flow allowed. !*/
    /** This is additional classic documentation comment. */
    NONE,

    /*! Traffic allowed from start to end node. !*/
    /** This is additional classic documentation comment. */
    POSITIVE,

    /*! Traffic allowed from end to start node. !*/
    /** This is additional classic documentation comment. */
    NEGATIVE,

    /*! Traffic allowed in both directions. !*/
    /** This is additional classic documentation comment. */
    BOTH
};

/*!

**DirectionStructure**

 This is a structure which uses Direction enumeration type.

!*/

/**
 * This is additional classic documentation comment.
 */
/** This is an another sticky comment which is oneliner! */
struct DirectionStructure(bool hasExtraValue)
{
    /*! Direction. !*/
    /** This is additional classic documentation comment. */
    Direction       direction;

    /*! Optional extra value. !*/
    /** This is additional classic documentation comment. */
    uint32          extraValue if hasExtraValue == true;

    /*! Optional field comment. !*/
    /** This is additional classic documentation comment. */
    optional int32  optionalField1;

    optional
    /*! Optional field comment. !*/
    /** This is additional classic documentation comment. */
    int32           optionalField2;

    /*! The first offset. !*/
    /** This is additional classic documentation comment. */
    uint32          offset1;

    /*! Field with offset comment. !*/
    /** This is additional classic documentation comment. */
    offset1:
    int32           fieldWitOffset1;

    /*! The second offset. !*/
    /** This is additional classic documentation comment. */
    uint32          offset2;

    offset2:
    /*! Field with offset comment. !*/
    /** This is additional classic documentation comment. */
    int32           fieldWitOffset2;

    /*! The first offset array. !*/
    /** This is additional classic documentation comment. */
    uint32          offsets1[];

    /*! Array with indexed offset comment. !*/
    /** This is additional classic documentation comment. */
    offsets1[@index]:
    int32           arrWithIndexedOffset1[];

    /*! The second offset array. !*/
    /** This is additional classic documentation comment. */
    uint32          offsets2[];

    offsets2[@index]:
    /*! Array with indexed offset comment. !*/
    /** This is additional classic documentation comment. */
    int32           arrWithIndexedOffset2[];

    /*! Aligned field comment. !*/
    /** This is additional classic documentation comment. */
    align(16):
    int32           alignedField1;

    align(16):
    /*! Aligned field comment. !*/
    /** This is additional classic documentation comment. */
    int32           alignedField2;

    /*! The first offset. !*/
    /** This is additional classic documentation comment. */
    uint32          mixedOffset1;

    /*! Mixed comment. !*/
    /** This is additional classic documentation comment. */
    align(32):
    mixedOffset1:
    optional int32  mixedField1;

    /*! The second offset. !*/
    /** This is additional classic documentation comment. */
    uint32          mixedOffset2;

    align(32):
    /*! Mixed comment. !*/
    /** This is additional classic documentation comment. */
    mixedOffset2:
    optional int32 mixedField2;

    /*! The third offset. !*/
    /** This is additional classic documentation comment. */
    uint32          mixedOffset3;

    align(32):
    mixedOffset3:
    /*! Mixed comment. !*/
    /** This is additional classic documentation comment. */
    optional int32  mixedField3;

    /*! The fourth offset. !*/
    /** This is additional classic documentation comment. */
    uint32 mixedOffset4;

    align(32):
    mixedOffset4:
    optional /*! Mixed comment. !*/ /** Classic documentation comment. */ int32 mixedField4;
};
