package implicit_non_array_field_error;

struct Item
{
    // Implicit can be arrays only.
    implicit int32  param1;
};
