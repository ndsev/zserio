package api_clashing.choice_with_api_clash_error;

choice Api(bool large) on large
{
    case false:
        uint32 field32;
    default:
        uint64 field64;
};
