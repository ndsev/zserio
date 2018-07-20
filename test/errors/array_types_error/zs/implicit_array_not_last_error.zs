package implicit_array_not_last_error;

struct ImplicitArrayNotLastError
{
    implicit int32  array[];   // implicit arrays must be at the end
    uint16          wrong;
};
