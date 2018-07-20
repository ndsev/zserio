package comments.documentation_comments;

/**
 * Traffic flow on links.
 */
enum bit:2 Direction
{
    /** No traffic flow allowed. */
    NONE,

    /** Traffic allowed from start to end node. */
    POSITIVE,

    /** Traffic allowed from end to start node. */
    NEGATIVE,

    /** Traffic allowed in both directions. */
    BOTH
};

/**
 * This is a structure which uses Direction enumeration type.
 *
 * For more information, please have a look to the
 * @see "documentation" Direction page.
 *
 * @see Direction
 *
 * @param hasExtraValue True if the structure has extra value.
 *
 * @todo Update this comment.
 *
 * @deprecated
 */
struct DirectionStructure(bool hasExtraValue)
{
    /** Direction. */
    Direction   direction;

    /** Optional extra value. */
    uint32      extraValue if hasExtraValue == true;
};
