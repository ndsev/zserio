#ifndef ZSERIO_TYPES_H_INC
#define ZSERIO_TYPES_H_INC

// Skip standard type definition if another standard types header is provided to prevent compilation error.
#ifdef ZSERIO_STANDARD_TYPES_HEADER
    #include ZSERIO_STANDARD_TYPES_HEADER
#else
    #include <cstdint>
#endif // ifdef ZSERIO_STANDARD_TYPES_HEADER

#endif // ifndef ZSERIO_TYPES_H_INC
