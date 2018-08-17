# Zserio Invisibles

We have recently introduced some convenience features to zserio that generate some invisible elements in the
byte stream. They are called invisibles since zserio writers will add them while they stay kind of invisible
in the schema definition.

This section describes these features in detail and shows the equivalent classic zserio modeling without any
invisibles being generated.

## optional keyword

The `optional` keyword adds a bool (1 bit) field in front of the structure which indicates whether
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
be explicitly stated in the schema, the Auto Arrays set an invisible varuint64 length descriptor right in front
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
    varuint64 numEntries;
    string    employees[numEntries];
};
```

Both examples from above result in the exact same byte stream.

> Note: In classic zserio the numEntries field can be set anywhere before the definition of the array, it does
not necessarily have to be placed right in front.

## Union Types

Union Types adds a hidden varuint64 value to the schema. This value denotes which branch in the union has been
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

enum varuint64 ChoiceTag
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
