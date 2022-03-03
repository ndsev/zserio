# Zserio Invisibles

We have recently introduced some convenience features to zserio that generate some invisible elements in the
byte stream. They are called invisibles since zserio writers will add them while they stay kind of invisible
in the schema definition.

This section describes these features in detail and shows the equivalent classic zserio modeling without any
invisibles being generated.

## optional keyword

The `optional` keyword adds a `bool` (1 bit) field in front of the structure which indicates whether
the structure is available or not and a constraint on the structure itself.

**invisible zserio**

```
struct Company
{
    optional string website;
};
```

**classic zserio**

```
struct Company
{
    bool    hasWebsite;
    string  website if hasWebsite;
}
```

Both examples from above result in the exact same byte stream.

> Note: In classic zserio the hasWebsite field can be set anywhere before the definition of the string website,
it does not necessarily have to be placed right in front.

## Auto Arrays

Auto Arrays do not expose the size of the list in the schema. Where in classic zserio the size of an array must
be explicitly stated in the schema, the Auto Arrays set an invisible `varsize` length descriptor right in front
of the array.

**invisible zserio**

```
struct Company
{
    string employees[];
};
```

**classic zserio**

```
struct Company
{
    varsize   numEntries;
    string    employees[numEntries];
};
```

Both examples from above result in the exact same byte stream.

> Note: In classic zserio the numEntries field can be set anywhere before the definition of the array, it does
not necessarily have to be placed right in front.

## Union Types

Union Types adds a hidden `varsize` value to the schema. This value denotes which branch in the union has been
chosen. Union Types are an automatic Choice Types in classic zserio.

**invisible zserio**

```
union ColorValue
{
    uint8   value8;
    uint16  value16;
};
```

**classic zserio**

```
struct ColorHolder
{
    ChoiceTag   choiceTag;
    ColorValue  colorValue(choiceTag);
};

enum varsize ChoiceTag
{
    TAG_VALUE8  = 0,
    TAG_VALUE16 = 1
};

choice ColorValue(ChoiceTag choiceTag) on choiceTag
{
    case TAG_VALUE8:
        uint8   value8;

    case TAG_VALUE16:
        uint16  value16;
};
```

Both examples from above result in the exact same byte stream.

## Packed Arrays

### Packed Arrays of Integers

All packed arrays of integers adds at least one hidden `bool` (1 bit) field at the beginning of the array to
indicate whether the array is actually packed or not.

The following shows an invisible zserio for array of `uint32` packed using delta compression:

**invisible zserio**

```
struct PackedArray
{
    packed uint32 list[5];
};
```

**classic zserio**

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct DeltaPackedArray(bit:6 maxBitNumber)
{
    uint32 element0;
    int<maxBitNumber + 1> deltas[4];
};

struct PackedArray
{
    PackingDescriptor packingDescriptor;
    DeltaPackedArray(packingDescriptor.maxBitNumber) packedList if packingDescriptor.isPacked;
    uint32 unpackedList[5] if !packingDescriptor.isPacked;
};
```

Both examples from above result in the exact same byte stream.

### Packed Arrays of Compounds

All packed arrays of compounds adds at least one hidden `bool` (1 bit) field before each packable field
of compound to indicate whether the field is actually packed or not.

The following shows an invisible zserio for array of structures packed using delta compression:

**invisible zserio**

```
struct PackableStructure
{
    uint32 value;
    string text;
};

struct PackedArray
{
    packed PackableStructure list[5];
};
```

**classic zserio**

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct PackableStructureElement0
{
    PackingDescriptor valuePackingDescriptor;
    uint32 value;
    string text;
};

struct PackableStructureElementX(PackingDescriptor valuePackingDescriptor)
{
    int<valuePackingDescriptor.maxBitNumber + 1> valueDelta if valuePackingDescriptor.isPacked;
    uint32 value if !valuePackingDescriptor.isPacked;
    string text;
};

struct PackedArray
{
    PackableStructureElement0 element0;
    PackableStructureElementX(element0.valuePackingDescriptor) elements[4];
};
```

Both examples from above result in the exact same byte stream.

Packed arrays of choices and unions are encoding in the same way, meaning that all fields corresponded to the
same case are considered as an array. If such array is empty, nothing is encoded in the bit stream.

### Packed Arrays of Nested Compounds

All packed arrays of nested compounds adds at least one hidden `bool` (1 bit) field before each packable field
of compound to indicate whether the field is actually packed or not.

The following shows an invisible zserio for array of nested structures packed using delta compression:

**invisible zserio**

```
struct InnerStructure
{
    uint64 value64;
    uint16 value16;
};

struct PackableStructure
{
    uint32 value32;
    string text;
    InnerStructure innerStructure;
};

struct PackedArray
{
    packed PackableStructure list[5];
};
```

**classic zserio**

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct InnerStructureElement0
{
    PackingDescriptor value64PackingDescriptor;
    uint64 value64;
    PackingDescriptor value16PackingDescriptor;
    uint16 value16;
};

struct PackableStructureElement0
{
    PackingDescriptor value32PackingDescriptor;
    uint32 value32;
    string text;
    InnerStructureElement0 innerStructureElement0;
};

struct InnerStructureElementX(PackingDescriptor value64PackingDescriptor,
        PackingDescriptor value16PackingDescriptor)
{
    int<value64PackingDescriptor.maxBitNumber + 1> value64Delta if value64PackingDescriptor.isPacked;
    uint64 value64 if !value64PackingDescriptor.isPacked;
    int<value16PackingDescriptor.maxBitNumber + 1> value16Delta if value16PackingDescriptor.isPacked;
    uint16 value16 if !value16PackingDescriptor.isPacked;
};

struct PackableStructureElementX(PackingDescriptor value32PackingDescriptor,
        PackingDescriptor value64PackingDescriptor, PackingDescriptor value16PackingDescriptor)
{
    int<value32PackingDescriptor.maxBitNumber + 1> value32Delta if value32PackingDescriptor.isPacked;
    uint32 value32 if !value32PackingDescriptor.isPacked;
    string text;
    InnerStructureElementX(value64PackingDescriptor, value16PackingDescriptor) innerStructureElementX;
};

struct PackedArray
{
    PackableStructureElement0 element0;
    PackableStructureElementX(element0.value32PackingDescriptor,
            element0.innerStructureElement0.value64PackingDescriptor,
            element0.innerStructureElement0.value16PackingDescriptor) elements[4];
};
```

Both examples from above result in the exact same byte stream.
