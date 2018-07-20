package enumeration.et_duplicate;

/**
 * This is the first paragraph of a doc comment,
 * extending over two lines.
 *
 * A new paragraph is started after a blank line.
 *
 * @todo add some more documentation.
 */
enum uint16 Colour
{
    /** Red. */
    RED = 1,
    /** Green. */
    GREEN,
    /** Yellow. */
    YELLOW,
    BLUE = 7
};

enum uint8 TrafficLight
{
    RED = 10,
    YELLOW = 12,
    GREEN = 14
};

struct DuplicateEnum
{
    Colour colour : colour == Colour.RED;
    TrafficLight light : light == TrafficLight.RED;
};
