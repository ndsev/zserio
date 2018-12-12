package expressions.used_before_type;

// This example demonstrates expression which uses Zserio types defined after expression definition.
// This checks evaluation of expression 'Color.RED' which must invoke evaluation of enumeration 'Color' and
// const types 'COLOR_RED_VALUE' and 'NUM_VALUE_BITS'.
struct UsedBeforeTypeExpression
{
    Color   color;
    bool    isRedColorLight if color == Color.RED;
};

enum bit<NUM_VALUE_BITS> Color
{
    RED     = COLOR_RED_VALUE,
    BLUE
};

const bit<NUM_VALUE_BITS> COLOR_RED_VALUE = 5;

const uint8 NUM_VALUE_BITS = 7;
