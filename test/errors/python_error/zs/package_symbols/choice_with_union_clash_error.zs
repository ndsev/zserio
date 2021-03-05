package package_symbols.choice_with_union_clash_error;

choice SomeName(bool selector) on selector
{
    case true:
        uint32 value;
    default:
        ; // empty
};

union Some_Name
{
    uint32 u32Field;
    float32 f32Field;
};
