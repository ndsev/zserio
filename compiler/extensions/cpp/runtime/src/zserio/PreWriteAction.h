#ifndef ZSERIO_PRE_WRITE_ACTION_H_INC
#define ZSERIO_PRE_WRITE_ACTION_H_INC

namespace zserio
{

/** Enum defining bit-mask values pre-write action configuration. */
enum PreWriteAction
{
    NO_PRE_WRITE_ACTION             = 0x00, /**< Perfrom no pre-write action. */
    PRE_WRITE_INITIALIZE_CHILDREN   = 0x01, /**< Initialize children during the pre-write action. */
    PRE_WRITE_INITIALIZE_OFFSETS    = 0x02, /**< Initialize offsets during the pre-write action. */
    ALL_PRE_WRITE_ACTIONS           = 0x03  /**< Perform all pre-write actions. */
};

} // namespace zserio

#endif // ifndef ZSERIO_PRE_WRITE_ACTION_H_INC
