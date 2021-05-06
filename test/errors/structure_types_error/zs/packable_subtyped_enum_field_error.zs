package packable_subtyped_enum_field_error;

enum uint8 TestEnum
{
    ONE,
    TWO
};

subtype TestEnum SubtypedEnum;

struct PackableSubtypedEnumFieldError
{
    packable SubtypedEnum subtypedEnum;
};
