#ifndef ZSERIO_RUNTIME_ARCH_H_INC
#define ZSERIO_RUNTIME_ARCH_H_INC

#include "zserio/Types.h"

#if UINTPTR_MAX == UINT64_MAX
    #define ZSERIO_RUNTIME_64BIT
#elif UINTPTR_MAX == UINT32_MAX
    #define ZSERIO_RUNTIME_32BIT
#else
    #error "Unexpected CPU architecture!"
#endif

#endif // ZSERIO_RUNTIME_ARCH_H_INC
