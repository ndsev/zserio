#ifndef ZSERIO_PRE_WRITE_ACTION_H_INC
#define ZSERIO_PRE_WRITE_ACTION_H_INC

namespace zserio
{

enum PreWriteAction
{
    NO_PRE_WRITE_ACTION             = 0x00,
    PRE_WRITE_CHECK_RANGES          = 0x01,
    PRE_WRITE_INITIALIZE_CHILDREN   = 0x02,
    PRE_WRITE_INITIALIZE_OFFSETS    = 0x04,
    ALL_PRE_WRITE_ACTIONS           = 0x07
};

} // namespace zserio

#endif // ifndef ZSERIO_PRE_WRITE_ACTION_H_INC
