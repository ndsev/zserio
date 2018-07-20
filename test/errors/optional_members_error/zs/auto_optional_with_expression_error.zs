package auto_optional_with_expression_error;

struct AutoOptionalWithExpressionError
{
    bool            hasOptional;
    optional uint8  autoOptionalValue if hasOptional;
};
