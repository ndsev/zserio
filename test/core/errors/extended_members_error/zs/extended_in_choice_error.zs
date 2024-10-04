package extended_in_union_error;

choice Extended(bool param) on param
{
    case true:
        uint32 field1;
    default:
        extend string field2;
};
