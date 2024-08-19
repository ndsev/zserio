package selector_expression_uses_offset_field_error;

struct Container
{
    uint32 offset;
};

choice TestChoice(Container container) on container.offset
{
    case 0:
        uint32 fieldU32;
    default:
        string fieldStr;
};

struct TestStruct
{
    Container container;
container.offset:
    TestChoice(container) testChoice;
};
