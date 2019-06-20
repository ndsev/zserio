choice Choice(uint8 selector) on selector
{
    case 0:
        bool    valueBool;
    case 1:
    case 2:
        uint16  value16;
    default:
        uint64  value64;
};
