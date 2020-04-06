package field_alignment_error;

struct FieldAlignmentError
{
    bit:3 alignment;
align(alignment):
    int32 field;
};
