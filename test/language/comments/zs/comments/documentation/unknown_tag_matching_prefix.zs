package comments.documentation.unknown_tag_matching_prefix;

const int32 VALUE = 10;

// '@sea' caused an error with older Zserio versions, currently it shall be treated as a text

/**
 * Test structure.
 *
 * @sea VALUE.
 */
struct Test
{
    int32 test;
};
