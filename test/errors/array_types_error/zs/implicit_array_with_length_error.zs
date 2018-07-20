package implicit_array_with_length_error;

struct ImplicitArrayWithLengthError
{
    uint16          arraySize;
    implicit int32  array[arraySize];   // implicit arrays cannot have length
};
