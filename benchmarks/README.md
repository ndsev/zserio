# Zserio Benchmarks

In this folder you find a number of size and performance Zserio benchmarks with different data sets.
If you are looking for benchmarks to compare Zserio and Protobuf, you might be interested in
[Protobuf Benchmarks by Zserio](https://github.com/ndsev/zserio-protobuf-benchmarks).

## Running

Running can be done by provided `benchmark.sh` script which accepts as a parameter required platform
(e.g. `cpp-linux64-gcc`):

```
scripts/benchmark.sh <PLATFORM>
```

The script `benchmark.sh` automatically generates simple performance test for each benchmark.
The performance test uses generated Zserio' API to read appropriate dataset from JSON format,
serialize it into the binary format and then read it again. Both reading time and the BLOB
size are reported. BLOB size after zip compression is reported as well.

## Results

- Used platform: 64-bit Linux Mint 21.1, Intel(R) Core(TM) i7-9850H CPU @ 2.60GHz
- Used compilers: gcc 11.4.0, Java 1.8.0_382, Python 3.10.12
- Used Zserio version: 3.12

### Addressbook

[addressbook.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/addressbook/addressbook.zs
[addressbook_align.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/addressbook/addressbook_align.zs
[addressbook_packed.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/addressbook/addressbook_packed.zs
[addressbook.json]: https://github.com/ndsev/zserio-datasets/blob/master/addressbook/addressbook.json

| Benchmark                | Dataset            | Target                 |      Time | Blob Size  | Zip Size |
| ------------------------ | ------------------ | ---------------------- | --------- | ---------- | -------- |
| [addressbook.zs]         | [addressbook.json] | C++ (linux64-gcc)      |   1.478ms |  305.838kB |    222kB |
| [addressbook_align.zs]   | [addressbook.json] | C++ (linux64-gcc)      |   0.844ms |  311.424kB |    177kB |
| [addressbook_packed.zs]  | [addressbook.json] | C++ (linux64-gcc)      |   1.574ms |  297.619kB |    234kB |
| [addressbook.zs]         | [addressbook.json] | Java                   |   2.618ms |  305.838kB |    222kB |
| [addressbook_align.zs]   | [addressbook.json] | Java                   |   1.677ms |  311.424kB |    177kB |
| [addressbook_packed.zs]  | [addressbook.json] | Java                   |   3.174ms |  297.619kB |    234kB |
| [addressbook.zs]         | [addressbook.json] | Python                 | 212.694ms |  305.838kB |    222kB |
| [addressbook_align.zs]   | [addressbook.json] | Python                 |  82.406ms |  311.424kB |    177kB |
| [addressbook_packed.zs]  | [addressbook.json] | Python                 | 245.361ms |  297.619kB |    234kB |
| [addressbook.zs]         | [addressbook.json] | Python (C++)           |  49.357ms |  305.838kB |    222kB |
| [addressbook_align.zs]   | [addressbook.json] | Python (C++)           |  48.737ms |  311.424kB |    177kB |
| [addressbook_packed.zs]  | [addressbook.json] | Python (C++)           |  62.915ms |  297.619kB |    234kB |

#### Schema align

Zserio schema `_align` does contain enumeration field `PhoneType` in schema byte-aligned. This means that such
schema will need bigger Blob size but it should have reading time faster. As a consequence, binary data of such
schema can be better compressed, e.g. by `zip` algorithm.

#### Schema packed

Zserio schema `_packed` compresses arrays `people[]` and `phones[]` using delta compression. This means that
such schema will need smaller Blob size but it should have reading time slower. As a consequence, binary data of
such schema can be worse compressed, e.g. by `zip` algorithm.

### Apollo

[apollo.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/apollo/apollo.zs
[apollo.zs.json]: https://github.com/ndsev/zserio-datasets/blob/master/apollo/apollo.zs.json

| Benchmark              | Dataset                | Target                 |      Time | Blob Size  | Zip Size |
| ---------------------- | ---------------------- | ---------------------- | --------- | ---------- | -------- |
| [apollo.zs]            | [apollo.zs.json]       | C++ (linux64-gcc)      |   0.244ms |  226.507kB |    144kB |
| [apollo.zs]            | [apollo.zs.json]       | Java                   |   0.965ms |  226.507kB |    144kB |
| [apollo.zs]            | [apollo.zs.json]       | Python                 |  46.898ms |  226.507kB |    144kB |
| [apollo.zs]            | [apollo.zs.json]       | Python (C++)           |  19.126ms |  226.507kB |    144kB |

### CarSales

[carsales.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/carsales/carsales.zs
[carsales_align.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/carsales/carsales_align.zs
[carsales_packed.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/carsales/carsales_packed.zs
[carsales.json]: https://github.com/ndsev/zserio-datasets/blob/master/carsales/carsales.json
[carsales_sorted.json]: https://github.com/ndsev/zserio-datasets/blob/master/carsales/carsales_sorted.json

| Benchmark              | Dataset                | Target                 |      Time | Blob Size | Zip Size |
| ---------------------- | ---------------------- | ---------------------- | --------- | --------- | -------- |
| [carsales.zs]          | [carsales.json]        | C++ (linux64-gcc)      |   1.374ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | C++ (linux64-gcc)      |   0.925ms | 295.965kB |    205kB |
| [carsales_packed.zs]   | [carsales.json]        | C++ (linux64-gcc)      |   1.644ms | 273.909kB |    234kB |
| [carsales_packed.zs]   | [carsales_sorted.json] | C++ (linux64-gcc)      |   1.552ms | 262.546kB |    238kB |
| [carsales.zs]          | [carsales.json]        | Java                   |   2.942ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Java                   |   1.893ms | 295.965kB |    205kB |
| [carsales_packed.zs]   | [carsales.json]        | Java                   |   3.761ms | 273.909kB |    234kB |
| [carsales_packed.zs]   | [carsales_sorted.json] | Java                   |   3.558ms | 262.546kB |    238kB |
| [carsales.zs]          | [carsales.json]        | Python                 | 224.082ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Python                 | 105.024ms | 295.965kB |    205kB |
| [carsales_packed.zs]   | [carsales.json]        | Python                 | 260.414ms | 273.909kB |    234kB |
| [carsales_packed.zs]   | [carsales_sorted.json] | Python                 | 245.950ms | 262.546kB |    238kB |
| [carsales.zs]          | [carsales.json]        | Python (C++)           |  45.355ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Python (C++)           |  45.631ms | 295.965kB |    205kB |
| [carsales_packed.zs]   | [carsales.json]        | Python (C++)           |  76.779ms | 273.909kB |    234kB |
| [carsales_packed.zs]   | [carsales_sorted.json] | Python (C++)           |  78.773ms | 262.546kB |    238kB |

#### Schema align

Zserio schema `_align` does contain all fields in schema byte-aligned. This means that such
schema will need bigger Blob size but it should have reading time faster. As a consequence, binary data of such
schema can be better compressed, e.g. by `zip` algorithm.

#### Schema packed

Zserio schema `_packed` compresses array `carSales` using delta compression. This means that
such schema will need smaller Blob size but it should have reading time slower. As a consequence, binary data of
such schema can be worse compressed, e.g. by `zip` algorithm.

#### Json sorted

Json representation `_sorted` does contain sorted `CarSale` array elements according to the fields `year`
and `price`. This should improve used delta compression of the array `carSales`.

### SimpleTrace

[simpletrace.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/simpletrace/simpletrace.zs
[simpletrace_packed.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/simpletrace/simpletrace_packed.zs
[prague-groebenzell.json]: https://github.com/ndsev/zserio-datasets/blob/master/simpletrace/prague-groebenzell.json

| Benchmark              | Dataset                   | Target               |      Time | Blob Size | Zip Size |
| ---------------------- | ------------------------- | -------------------- | --------- | --------- | -------- |
| [simpletrace.zs]       | [prague-groebenzell.json] | C++ (linux64-gcc)    |   0.221ms |  87.042kB |     66kB |
| [simpletrace_packed.zs]| [prague-groebenzell.json] | C++ (linux64-gcc)    |   0.145ms |  40.266kB |     41kB |
| [simpletrace.zs]       | [prague-groebenzell.json] | Java                 |   0.425ms |  87.042kB |     66kB |
| [simpletrace_packed.zs]| [prague-groebenzell.json] | Java                 |   1.145ms |  40.266kB |     41kB |
| [simpletrace.zs]       | [prague-groebenzell.json] | Python               |  65.112ms |  87.042kB |     66kB |
| [simpletrace_packed.zs]| [prague-groebenzell.json] | Python               |  26.664ms |  40.266kB |     41kB |
| [simpletrace.zs]       | [prague-groebenzell.json] | Python (C++)         |   9.541ms |  87.042kB |     66kB |
| [simpletrace_packed.zs]| [prague-groebenzell.json] | Python (C++)         |  17.136ms |  40.266kB |     41kB |

#### Schema packed

Zserio schema `_packed` compresses array `trace` using delta compression. This means that
such schema will need smaller Blob size but it should have reading time slower. As a consequence, binary data of
such schema can be worse compressed, e.g. by `zip` algorithm.

## How to Add New Benchmark

- Add new dataset (e.g. `new_benchmark`) in JSON format
  into [datasets repository](https://github.com/ndsev/zserio-datasets)
- Add new schema (e.g. `new_benchmark`) in Zserio format into
  [benchmarks directory](https://github.com/ndsev/zserio/tree/master/benchmarks)
- Make sure that the first structure in the schema file is the top level structure
