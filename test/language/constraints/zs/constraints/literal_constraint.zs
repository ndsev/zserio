package constraints.literal_constraint;

struct LiteralConstraint
{
    int32  value : (value >= -268435455 && value < 0) || (value > 0 && value <= 268435455);
};
