package constraints.choice_constraints;

choice ChoiceConstraints(bool selector) on selector
{
    case true:
        uint8   value8  : value8 != 0;

    case false:
        uint16  value16 : value16 > 255;
};
