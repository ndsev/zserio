package choice_choice_name_conflict_error;

choice Test(bool variant) on variant
{
    case true:
        int32 field32;
    default:
        int16 field16;
};

choice Test(uint8 numBits) on numBits // Test is already defined!
{
    case 8:
        uint8 field8;
    case 16:
        uint16 field16;
    default:
        bit<numBits> field;
};
