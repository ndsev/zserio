package templates.choice_templated_field;

choice TemplatedChoice<T1, T2>(uint32 selector) on selector
{
    case 0:
        T1 templatedField1;
    case 1:
        T2 templatedField2;
    case 2:
        string stringField;
};

struct Compound<T>
{
    T value;
};

struct ChoiceTemplatedField
{
    uint32 selector;
    TemplatedChoice<uint32, uint16>(selector) choice1;
    TemplatedChoice<Compound<uint32>, uint16>(selector) choice2;
};
