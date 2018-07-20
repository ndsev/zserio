package compound.ct_choice;

enum bit:3 Color
{
    NONE = 000b,
    RED = 010b,
    BLUE,
    BLACK = 111b
};

choice MyChoice(int16 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;

    function uint16 multiply()
    {
        return type * 10;
    }
};

/**
 *  This is a very important note
 */
choice Content(uint16 tag) on tag
{
    case 1:
        /**
         * choice member comment
         * if it's variant A
         */
        VariantA  a;

    case 2:
    case 3:
    case 4:
        /**
         * variant B is only possible with cases 2-4
         */
        VariantB  b;

    case 5:
    case 6:
        // empty
          ;

    default:
        /**
         *  else take the default variant
         */
        VariantC  c;

    function uint32 sensless()
    {
        return 1;
    }
};

subtype int8  VariantA;
subtype int16 VariantB;
subtype int32 VariantC;

// enum
choice ChoiceOnEnum(Color color) on color
{
    case RED:
        int16   a;
    case Color.BLUE:
        int32   b;
    default:
        int32   c;
};

choice ChoiceOnEnumNoDefault(Color color) on color
{
    case Color.RED:
        int16   a;
    case BLUE:
        int32   b;
};

// signed integers
choice ChoiceOnI8(int8 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnI16(int16 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnI32(int32 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnI64(int64 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

// unsigned integers
choice ChoiceOnU8(uint8 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnU16(uint16 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnU32(uint32 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

// unsigned bitfields
choice ChoiceOnB1(bit:1 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnB25(bit:25 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnB45(bit:45 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnB63(bit:63 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

// signed bitfields
choice ChoiceOnSB1(int:1 type) on type
{
    case -1:
        int16   a;
    case 0:
        int32   b;
};

choice ChoiceOnSB25(int:25 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnSB45(int:45 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnSB63(int:63 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnV16(varint16 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};

choice ChoiceOnVU16(varuint16 type) on type
{
    case 0:
        int16   a;
    case 1:
        int32   b;
};
