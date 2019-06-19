#ifndef ZSERIO_TYPES_H_INC
#define ZSERIO_TYPES_H_INC

// Skip standard type definition if another standard types header is provided to prevent compilation error.
#ifdef ZSERIO_STANDARD_TYPES_HEADER
    #include ZSERIO_STANDARD_TYPES_HEADER
#else
    #ifndef __STDC_CONSTANT_MACROS
        // request (U)INTn_C() literal macros
        // this is required at least in mingw
        #define __STDC_CONSTANT_MACROS
    #endif
    // the following is required for INT32_C() to work on mingw (it uses the limit macros internally...)
    #ifndef __STDC_LIMIT_MACROS
        #define __STDC_LIMIT_MACROS
    #endif
    #include <stdint.h>
#endif // ifdef ZSERIO_STANDARD_TYPES_HEADER

#endif // ifndef ZSERIO_TYPES_H_INC
