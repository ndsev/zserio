package with_range_check_code.choice_bit4_range_check;

choice ChoiceBit4RangeCheckCompound(bool selector) on selector
{
    case true:
        bit:4   value;

    case false:
        bool    notValid;
};
