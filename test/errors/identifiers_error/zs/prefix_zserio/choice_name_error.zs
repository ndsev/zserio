package prefix_zserio.choice_name_error;

choice ChoiceZserio(bool s) on s // not a prefix
{
    case true:
        uint32 field;
};

choice ZserioTest(bool s) on s // zserio prefix!
{
    case false:
        uint32 field;
};
