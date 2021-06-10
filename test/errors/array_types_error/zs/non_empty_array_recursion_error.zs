package non_empty_array_recursion_error;

struct NonEmptyArrayRecursionError
{
    int32 signedLength;
    uint32 unsignedLength;
    NonEmptyArrayRecursionError array1[]; // auto array can be empty
    NonEmptyArrayRecursionError array2[signedLength + 1]; // still can be empty, lower bound is lower than 0
    NonEmptyArrayRecursionError array3[unsignedLength + 1]; // cannot be empty, lower bound is 1
};
