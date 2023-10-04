package optional_members.optional_expression_with_removed_enum_item;

enum uint8 Numbers
{
    ONE = 1,
    @removed TEN = 10,
    ZERO = 0
};

struct Compound
{
    varsize lenBase;
    uint32 array[lenBase - valueof(Numbers.TEN)] if lenBase >= valueof(Numbers.TEN);
};
