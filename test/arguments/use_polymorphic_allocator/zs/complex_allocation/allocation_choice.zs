package complex_allocation.allocation_choice;

struct ChoiceCompound
{
    uint16 value16;
    bool   isValid;
};

choice AllocationChoice(bool hasArray) on hasArray
{
    case false:
        ChoiceCompound compound : compound.value16 > 1;

    default:
        ChoiceCompound array[];
};
