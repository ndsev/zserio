#include "zserio/ZserioTreeCreator.h"

namespace zserio
{

CppRuntimeException& operator<<(CppRuntimeException& exception, detail::CreatorState state)
{
    switch (state)
    {
    case detail::CreatorState::BEFORE_ROOT:
        return exception << "BEFORE_ROOT";
    case detail::CreatorState::IN_COMPOUND:
        return exception << "IN_COMPOUND";
    default: // detail::CreatorState::IN_ARRAY
        return exception << "IN_ARRAY";
    }
}

} // namespace zserio
