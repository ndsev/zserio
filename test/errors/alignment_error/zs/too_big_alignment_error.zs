package too_big_alignment_error;

struct NoIntegerAlignmentError
{
align(0xDEADBEEF1):
    int32 field;
};
