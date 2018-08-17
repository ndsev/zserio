# Zserio Benchmarks

In this folder you find a number of size and performance benchmarks with different data sets.

## Overview

- **Size benchmark zserio vs. protobuf**

![size comparison](ZserioProtobufSize.png)

## Zserio vs. Protocol Buffers

Google's Protocol Buffers are very popular and in wide-spread use. One of the many questions we always have to
answer is: "Why don't you use protobuf? It is already there."

Fact is that it wasn't open sourced when we would have needed it. Maybe we would have used it back then. But
even today we think we came along with something more tailored to our needs. This is also the reason why we
open sourced zserio after such a long time.

So let's see how zserio performs in comparison to protobuf. For being fair we have chosen to use the example
that is used on Google's documentation page of protobuf. Before we take a look at the data schema, we would
like to say that the example that Google is using does not really help to promote a binary - thus smaller -
representation of data. It mostly uses strings. In contrast to just simply storing them in a CSV file, a binary
format has to keep string length of each and every field to be able to parse the byte stream. One might argue
that a CSV may also be type-safe to some extent, but we will leave this discussion to be done somewhere else.

Spoiler: The test data that we used for this benchmark is smallest when using a CSV file.

### The test and data

We will encode 1000 address book entries which we mocked at Mockaroo (https://mockaroo.com/). The test data
contains an ID, a name, optionally an email address and some phone numbers with different types (home, work,
mobile). The mocked data does not include all fields for each entry.

The CSV looks like this:

id  | name            | email                        | phoneHome    | phoneMobile  | phoneWork
--- | --------------- | ---------------------------- | ------------ | ------------ | ------------
1   | Florri Marousek |                              | 223-353-1881 | 112-972-3954 |
... | ...             | ...                          | ...          | ...          | ...
12  | Angus Brandoni  | abrandonib@elegantthemes.com | 861-540-6138 | 838-999-6605 | 436-583-4303

We encode the data in Protobuf and three different zserio schemas. After encoding the data, we also compress it
using default values of zlib to demonstrate the importance of how your data is layed out when you want to
squeeze the size.

### The schema

#### Protobuf version 3

```
syntax = "proto3";

package tutorial;

message Person {
  string name = 1;
  int32 id = 2;
  string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    string number = 1;
    PhoneType type = 2;
  }

  repeated PhoneNumber phones = 4;
}

message AddressBook {
  repeated Person people = 1;
}

```
#### Zserio schema: "plain"

In a first step we transcode this schema without any further thought to zserio. This results in the following
zserio schema (which actually looks very similar to protobuf):

```
package addressbook;

struct Person {
  string name;
  int32 id;
  string email;
  PhoneNumber phones[];
};

struct AddressBook {
  Person people[];
};

struct PhoneNumber {
  string number;
  PhoneType type;
};

enum bit:2 PhoneType {
  MOBILE = 0,
  HOME = 1,
  WORK = 2,
};
```

This schema will be called *zserio plain*. Please note the following in comparison to Protobuf v3:

- `int32` is of fixed size in zserio whereas Protobuf uses variable encoding
- The `PhoneType` enum is quite optimized in the schema above to demonstrate the abilities in zserio. We will
  see later that it might not always be the best idea to use the smallest available bit size when you later
  compress the encoded stream.

#### Zserio schema: "optimized"

Taking into account that Protobuf internally always uses variable integer encoding in v3 and that also each
and every element is optional, we adapt the zserio schema to be more like the Protobuf schema under its hood.

```
package addressbook;

struct Person {
  varint32 id; // <-- change to varint32 instead of int32
  string name;
  optional string email; // <-- make email optional
  PhoneNumber phones[];
};

struct AddressBook {
  Person people[];
};

struct PhoneNumber {
  string number;
  PhoneType type;
};

enum bit:2 PhoneType {
  MOBILE = 0,
  HOME = 1,
  WORK = 2,
};
```

We refer to this schema as *zserio optimized*. Please note that introducing the `optional` keyword in zserio
adds 1 bit in the byte stream before the actual value. So instead of an empty string which would consume 1 byte
for the size=0 information, we only store 1 bit. One has to keep in mind that with this the byte stream gets
"unaligned", meaning that e.g. the strings get shifted by one bit. This is important to keep in mind when
dealing with compression of the byte stream after encoding it. Compressors usually perform better with aligned
data.

#### Zserio schema: "aligned"

The aligned schema takes into account the fact that we later want to compress the encoded stream. Therefore we
change the type of the enumeration to `uint8` and rather store empty strings than using the `optional` keyword
to keep everything aligned. We still keep the varint32 from the previous optimization.

```
package addressbook;

struct Person {
  varint32 id; // <-- change to varint32 instead of int32
  string name;
  string email; // <-- for alignment it is better to keep 1 byte if empty
  PhoneNumber phones[];
};

struct AddressBook {
  Person people[];
};

struct PhoneNumber {
  string number;
  PhoneType type;
};

enum uint8 PhoneType { // <-- use a full byte instead of 2 bit to keep alignment
  MOBILE = 0,
  HOME = 1,
  WORK = 2,
};
```

### Summary size comparison

Encoding 1000 mocked phone book entries (from https://mockaroo.com/) results in the following data sizes:

Data size [byte] | raw csv | protobuf   | zserio plain | zserio optimized | zserio aligned
---------------- | ------- | ---------- | ------------ | ---------------- | --------------
uncompressed     | 63,271  | **73,567** | 63,958       | **62,020**       | 63,469
zlib compressed  | 33,860  | **38,964** | 45,370       | 50,316           | **36,029**

Zserio beats Protobuf in all uncompressed cases. When adding compression on top, we have to take care about
the alignments of e.g. strings. Unaligned strings just don't compress well. But once we did take this into
account, zserio compresses as good as protobuf does.

### Note

We also could have used the `align` keyword in zserio to align to byte boundaries without even changing the
data types. We could have simply used

```
struct PhoneNumber {
  string number;
align(8):
  PhoneType type;
};
```

and keep the enum at 2 bit:

```
enum bit:2 PhoneType {
  MOBILE = 0,
  HOME = 1,
  WORK = 2,
};
```

That would result in the same data size.
