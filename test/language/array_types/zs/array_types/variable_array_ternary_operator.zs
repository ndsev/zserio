package array_types.variable_array_ternary_operator;

struct VariableArrayElement(bit:6 bitSize)
{
    bit:6   maxBitSize = 32;
    bit<(bitSize > maxBitSize) ? maxBitSize : bitSize> element;
};

struct VariableArray
{
    bool    isFirstSizeUsed;
    bit:6   firstSize = 10;
    bit:6   secondSize = 20;
    VariableArrayElement(isFirstSizeUsed ? firstSize : secondSize)
            array[isFirstSizeUsed ? firstSize * firstSize : secondSize * secondSize];
};
