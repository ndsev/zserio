# Zserio JSON Guide 1.0

Zserio supports encoding in JSON to have readable format allowing users convenient way of data inspection, e.g.
for debugging purposes. This JSON encoding is described by this document.

> Note that JSON debug string is available only when `-withTypeInfoCode` option is used.
> Moreover in C++ it's available only when `-withReflectionCode` option is used.

[Simple Example](#simple-example)

[JSON Mapping](#json-mapping)

## Simple Example

Consider the following Zserio schema:

```
package tutorial;

struct Employee
{
    uint8      age;
    string     name;
    uint16     salary;
    optional uint16 bonus;
    Role       role;
    Experience skills[] if role == Role.DEVELOPER;
};

struct Experience
{
    bit:6      yearsOfExperience;
    Language   programmingLanguage;
};

enum bit:2 Language
{
    CPP    = 0,
    JAVA   = 1,
    PYTHON = 2,
    JS     = 3
};

enum uint8 Role
{
    DEVELOPER = 0,
    TEAM_LEAD = 1,
    CTO       = 2,
};
```

If we use the schema above and encode one employee to JSON with

- age = 32
- name = Joe Smith
- salary = 5000
- no optional bonus
- role = DEVELOPER
- skills[0] = {8, CPP}
- skills[1] = {4, PYTHON}

the resulting JSON looks like the following:

```
{
    "age": 32,
    "name": "Joe Smith",
    "salary": 5000,
    "bonus": null,
    "role": "DEVELOPER",
    "skills": [
        {
            "yearsOfExperience": 8,
            "programmingLanguage": "CPP"
        },
        {
            "yearsOfExperience": 4,
            "programmingLanguage": "PYTHON"
        }
    ]
}
```

## JSON Mapping

When parsing JSON data into a Zserio object, if any field is missing, it will not be interpreted as a parsing
error. This behavior allows users to update Zserio object partially which might be very useful for debugging
purposes. However, users should be aware that partial update of Zserio object might leave object in inconsistent
state.

> Note that it's also not required to have a fully initialized object to be able to write it to JSON data.
> Thus it's possible to use JSON format for debugging in both directions, reading and writing.

The JSON encoding is described for all Zserio types in the table below:

Zserio Type            | JSON type      | Example                   | Notes
---------------------- | -------------- | ------------------------- | --------------------------------------------
`uint8/16/32/64`     | Number         | 0, 1, 100                 | JSON value can be only a decimal number.
`int8/16/32/64`      | Number         | 0, -1, -100               | JSON value can be only a decimal number.
`bit:N`, `bit<expr>` | Number         | 0, 1                      | JSON value can be only a decimal number.
`int:N`, `int<expr>` | Number         | 0, -1                     | JSON value can be only a decimal number.
`float16/32/64`      | Number, String | 3.14, "Nan", "Infinity", -"Infinity" | JSON value can be a number or one of the special string values "NaN", "Infinity" or "-Infinity". Exponent notation is also accepted.
`varuint/16/32/64`   | Number         | 0, 1, 100                 | JSON value can be only a decimal number.
`varint/16/32/64`    | Number         | 0, -1, -100               | JSON value can be only a decimal number.
`varsize`             | Number         | 0, 1, 100                 | JSON value can be only a decimal number.
`bool`                | Boolean        | true, false               | JSON value can be only false or true.
`string`              | String         | "text"                    | JSON value can be only a string.
`extern`              | Object         | {</br>"buffer": [203, 240],</br>"bitSize": 12</br>} | Generates JSON objects. JSON object key "buffer" contains bit sequence in bytes. JSON object key "bitSize" contains number of bits in bit sequence "buffer". If not all bits of the last byte of "buffer" are used, then only most significant bits of the corresponded size are used.
`bytes`               | Object         | {</br>"buffer": [202, 254]</br>} | Generates JSON objects. JSON object key "buffer" contains byte sequence.
`enum`                | Number, String | 0, "DEVELOPER"           | By default, enumeration items are encoded as JSON strings. Valid enumeration item is encoded as enumeration item string. Invalid enumeration item is encoded as string with comment, e.g. "10 /* no match */" where "10" is enumeration item converted to string. Default encoding can be changed to JSON numbers by `setEnumerableFormat()` method in `JsonWriter`. During parsing, both numbers and strings are accepted.
`bitmask`            | Number, String | 0, "READ \| WRITE"      | By default, bitmask values are encoded as JSON strings. When an exact match with or-ed bitmask values is found, then it's used, e.g. "READ \| WRITE". When no exact match is found, but some or-ed values match, the integral value is converted to string and the or-ed values are included in a comment, e.g. "127 /\* READ \| CREATE \*/". When no match is found at all, the integral value is converted to string and an appropriate comment is included, e.g. "13 /\* no match \*/". Default encoding can be changed to JSON numbers by `setEnumerableFormat()` method in `JsonWriter`. During parsing, both numbers and strings are accepted.
`struct`             | Object          | {</br>"age": 32,</br>"name": "Joe Smith"</br>} | Generates JSON objects. Structure field names become JSON object keys. Structure field values become JSON object values. JSON type null is an accepted value for all field types and it is handled as default value of the corresponding non-optional field type or it means that the corresponding optional field is not present.
`choice`             | Object          | {</br>"a": 234</br>} | Generates JSON objects with one name-value pair. The field name of selected case becomes JSON object key. The field value of selected case becomes JSON object value. JSON type null is an accepted value and it is handled as default value of the corresponding field. Generated JSON object can be empty if selected case is empty.
`union`              | Object          | {</br>"a": 234</br>} | Generates JSON objects with one name-value pair. The name of selected union field becomes JSON object key. The value of selected union field becomes JSON object value. JSON type null is an accepted value and it is handled as default value of the corresponding field. Generated JSON object can be empty if no field is selected.
arrays                | Array           | [</br>1, 2, 3</br>]  | Generates JSON arrays. Can be empty in case of empty Zserio array. JSON type null is an accepted value and it is handled as empty array.

[top](#zserio-json-guide)
