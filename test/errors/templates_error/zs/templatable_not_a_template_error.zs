package templatable_not_a_template_error;

struct Templatable
{
    uint32 value;
};

struct TemplatableNotATemplate
{
    Templatable<uint32> value;
};
