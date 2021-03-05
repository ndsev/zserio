package package_symbols.const_with_structure_clash_error;

const uint32 SOME_NAME = 13;

struct SomeName
{
    uint32 field : field < SOME_NAME;
};
