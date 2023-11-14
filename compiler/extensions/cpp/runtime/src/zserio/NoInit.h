#ifndef ZSERIO_NO_INIT_H_INC
#define ZSERIO_NO_INIT_H_INC

namespace zserio
{

/**
 * Helper type to specify that type should not call initialize method after an operation.
 */
struct NoInitT
{
    constexpr explicit NoInitT() = default;
};

/**
 * Constant used to convenient specification that initialize method should not be called.
 */
constexpr NoInitT NoInit;

} // namespace zserio

#endif // ZSERIO_NO_INIT_H_INC
