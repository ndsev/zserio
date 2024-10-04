package implicit_array_complex_with_field_behind_error;

struct StructWithImplicit
{
    bool hasOptional;
    implicit uint32 array[];
    uint32 optionalField if hasOptional;
};

union UnionWithOnlyImplicitBranch
{
    StructWithImplicit choice1;
    implicit uint32 choice2[];
};

choice ChoiceWithOnlyImplicitBranch(bool selector) on selector
{
    case true:
        UnionWithOnlyImplicitBranch choice1;
    default:
        StructWithImplicit choice2;
};

struct StructWithEmptyBranch(uint32 len)
{
    uint32 array[len];
    string field if len != 0;
};

choice ChoiceWithEmptyBranch(int32 selector) on selector
{
    case 0:
        StructWithEmptyBranch(0) choice1;
    default:
        StructWithImplicit choice2;
};

struct ImplicitArrayWithFieldBehindError
{
    StructWithImplicit structArray[]; // can be empty, thus can be without implicit
    ChoiceWithOnlyImplicitBranch(true) choiceWithOnlyImplicitBranch;
    ChoiceWithEmptyBranch(0) choiceWithEmptyBranch;
    uint32 optionalField if choiceWithEmptyBranch.choice1.len != 0;
    uint32 field; // unconditional field behind implicit!
};
