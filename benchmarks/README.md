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
- Used compilers: gcc 11.3.0, Java 1.8.0_362, Python 3.10.6
- Used Zserio version: 3.10

[addressbook.json]: https://github.com/ndsev/zserio-datasets/blob/master/addressbook/addressbook.json
[carsales.json]: https://github.com/ndsev/zserio-datasets/blob/master/carsales/carsales.json

[addressbook.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/addressbook/addressbook.zs
[addressbook_align.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/addressbook/addressbook_align.zs
[carsales.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/carsales/carsales.zs
[carsales_align.zs]: https://github.com/ndsev/zserio/blob/master/benchmarks/carsales/carsales_align.zs

| Benchmark              | Dataset                | Target                 |      Time | Blob Size | Zip Size |
| ---------------------- | ---------------------- | ---------------------- | --------- | --------- | -------- |
| [addressbook.zs]       | [addressbook.json]     | C++ (linux64-gcc)      |   1.444ms | 305.838kB |    222kB |
| [addressbook_align.zs] | [addressbook.json]     | C++ (linux64-gcc)      |   0.802ms | 311.424kB |    177kB |
| [carsales.zs]          | [carsales.json]        | C++ (linux64-gcc)      |   1.453ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | C++ (linux64-gcc)      |   0.823ms | 295.965kB |    205kB |
| [addressbook.zs]       | [addressbook.json]     | Java                   |   3.688ms | 305.838kB |    222kB |
| [addressbook_align.zs] | [addressbook.json]     | Java                   |   2.794ms | 311.424kB |    177kB |
| [carsales.zs]          | [carsales.json]        | Java                   |   3.851ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Java                   |   2.422ms | 295.965kB |    205kB |
| [addressbook.zs]       | [addressbook.json]     | Python                 | 253.982ms | 305.838kB |    222kB |
| [addressbook_align.zs] | [addressbook.json]     | Python                 | 239.968ms | 311.424kB |    177kB |
| [carsales.zs]          | [carsales.json]        | Python                 | 237.142ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Python                 | 221.180ms | 295.965kB |    205kB |
| [addressbook.zs]       | [addressbook.json]     | Python (C++)           |  64.074ms | 305.838kB |    222kB |
| [addressbook_align.zs] | [addressbook.json]     | Python (C++)           |  62.951ms | 311.424kB |    177kB |
| [carsales.zs]          | [carsales.json]        | Python (C++)           |  49.694ms | 280.340kB |    259kB |
| [carsales_align.zs]    | [carsales.json]        | Python (C++)           |  47.665ms | 295.965kB |    205kB |

## Schema align

Zserio schema `_align` does contain all fields in schema byte-aligned. This means that such schema will need
bigger Blob size but it should have reading time faster. As a consequence, binary data of such schema can be
better compressed, e.g. by `zip` algorithm.

## How to Add New Benchmark

- Add new dataset (e.g. `new_benchmark`) in JSON format
  into [datasets repository](https://github.com/ndsev/zserio-datasets)
- Add new schema (e.g. `new_benchmark`) in Zserio format into
  [benchmarks directory](https://github.com/ndsev/zserio/tree/master/benchmarks)
- Make sure that the first structure in the schema file is the top level structure
