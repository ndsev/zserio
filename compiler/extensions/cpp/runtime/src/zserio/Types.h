#ifndef ZSERIO_TYPES_H_INC
#define ZSERIO_TYPES_H_INC

// Skip standard type definition if another standard types header is provided to prevent compilation error.
#ifdef ZSERIO_STANDARD_TYPES_HEADER
    #include ZSERIO_STANDARD_TYPES_HEADER
#else
    // MSVC doesn't know the (u)int*_t types, add them manually (1600 == MSVC 2010 which already supports it).
    #if defined _MSC_VER && _MSC_VER < 1600
        typedef __int8           int8_t;
        typedef __int16          int16_t;
        typedef __int32          int32_t;
        typedef __int64          int64_t;
        typedef unsigned __int8  uint8_t;
        typedef unsigned __int16 uint16_t;
        typedef unsigned __int32 uint32_t;
        typedef unsigned __int64 uint64_t;

        #define INT8_C(VALUE)   static_cast<int8_t>(VALUE ## LL)
        #define INT16_C(VALUE)  static_cast<int16_t>(VALUE ## LL)
        #define INT32_C(VALUE)  static_cast<int32_t>(VALUE ## LL)
        #define INT64_C(VALUE)  static_cast<int64_t>(VALUE ## LL)

        #define UINT8_C(VALUE)  static_cast<uint8_t>(VALUE ## ULL)
        #define UINT16_C(VALUE) static_cast<uint16_t>(VALUE ## ULL)
        #define UINT32_C(VALUE) static_cast<uint32_t>(VALUE ## ULL)
        #define UINT64_C(VALUE) static_cast<uint64_t>(VALUE ## ULL)

        #define INT8_MIN         (-127i8 - 1)
        #define INT16_MIN        (-32767i16 - 1)
        #define INT32_MIN        (-2147483647i32 - 1)
        #define INT64_MIN        (-9223372036854775807i64 - 1)
        #define INT8_MAX         127i8
        #define INT16_MAX        32767i16
        #define INT32_MAX        2147483647i32
        #define INT64_MAX        9223372036854775807i64
        #define UINT8_MAX        0xffui8
        #define UINT16_MAX       0xffffui16
        #define UINT32_MAX       0xffffffffui32
        #define UINT64_MAX       0xffffffffffffffffui64
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
    #endif
#endif // ifdef ZSERIO_STANDARD_TYPES_HEADER

#endif // ifndef ZSERIO_TYPES_H_INC
