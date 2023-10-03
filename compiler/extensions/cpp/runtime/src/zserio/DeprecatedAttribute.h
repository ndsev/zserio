#ifndef ZSERIO_DEPRECATED_ATTRIBUTE_H_INC
#define ZSERIO_DEPRECATED_ATTRIBUTE_H_INC

#if defined(__clang__)
    #if __cplusplus >= 201703L
        #define ZSERIO_DEPRECATED [[deprecated]]
    #elif __clang_major__ >= 4
        #define ZSERIO_DEPRECATED __attribute__((deprecated))
    #endif
#elif defined(__GNUC__)
    #if __cplusplus >= 201703L
        #define ZSERIO_DEPRECATED [[deprecated]]
    #elif __GNUC__ >= 6
        #define ZSERIO_DEPRECATED __attribute__((deprecated))
    #endif
#elif defined(_MSC_VER)
    #if _MSC_VER >= 1900
        #define ZSERIO_DEPRECATED [[deprecated]]
    #endif
#endif

#ifndef ZSERIO_DEPRECATED
    // compiler not supported, define as an empty macro
    #define ZSERIO_DEPRECATED
#endif

#endif // ZSERIO_DEPRECATED_ATTRIBUTE_H_INC
