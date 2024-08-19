package selector_expression_uses_indexed_offset_field_error;

struct Container
{
    uint32 offsets[];
};

choice TestChoice(Container container) on container.offsets[0]
{
    case 0:
        uint32 fieldU32;
    default:
        string fieldStr;
};

struct TestStruct
{
    Container container;
container.offsets[@index]:
    TestChoice(container) testChoice[];
};
