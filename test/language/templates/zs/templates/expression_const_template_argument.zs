package templates.expression_const_template_argument;

const uint8 LENGTH = 10;

struct ConstTemplateArgument<C>
{
    uint32      offsetsField[C + C];
offsetsField[@index + C]:                               // template in indexed offset expression
    uint8       arrayField[C];                          // template in array length expression
    uint8       initializerField = C;                   // template in initializer expression
    int32       optionalField if C == 10;               // template in optional clause expression
    int32       constraintField : constraintField == C; // template in constraint expression
    bit<C - 8>  bitField;                               // template in bitfield length expression

    function bool check()
    {
        return C == 10;                                 // template in function body expression
    }
};

struct ConstTemplateArgumentHolder
{
    ConstTemplateArgument<LENGTH> constTemplateArgument;
};
