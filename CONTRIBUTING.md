# Contributing to Zserio

The following summarizes the process for contributing changes to the Zserio project.

## Where to start

* [Zserio Build Instructions](doc/ZserioBuildInstructions.md).

## General rules for any code contribution

* Consider to open a [discussion](https://github.com/ndsev/zserio/discussions/new/choose)
  or create an [issue](https://github.com/ndsev/zserio/issues/new/choose).

* Open a new GitHub pull request.

* Follow our coding style.

  * For all C++ and Java code we use [**clang-format**](https://clang.llvm.org/docs/ClangFormat.html) to check
    that the code follows the defined [coding style](.clang-format).

    > Note that you have to run `clang-format` on your own, our pipeline only checks that the code follows
      the rules.
    ```bash
    clang-format --style=file -i source.cpp
    ```

  * For Python code we use [**black**](https://black.readthedocs.io/en/stable/) to check that the code follows
    the defined coding style, which is basically [PEP-8](https://www.python.org/dev/peps/pep-0008/) with line
    length limited to 112 characters.

    > Note that you have to run `black` on your own, our pipeline only checks that the code follows
      the rules.
    ```bash
    black source.py --line-length 112
    ```

* Ensure that all GitHub [workflows](https://github.com/ndsev/zserio/actions) are passing.

* Wait for the review by [@Mi-La](https://www.github.com/Mi-La) or [@mikir](https://www.github.com/mikir).

> Please try to address only a single problem in you pull request.

## Did you find a bug?

* Ensure that the bug was not already reported in some [issue](https://github.com/ndsev/zserio/issues).

* If there is no open issue addressing the problem,
  [open a new one](https://github.com/ndsev/zserio/issues/new?assignees=&labels=bug&projects=&template=bug_report.md&title=).

* Follow [General rules for any code contribution](#general-rules-for-any-code-contribution).

## Do you want to propose a performance optimization?

* Consider to open a [discussion](https://github.com/ndsev/zserio/discussions/new/choose)
  or create an [issue](https://github.com/ndsev/zserio/issues/new/choose).

* Run our [benchmarks](https://github.com/ndsev/zserio/tree/master/benchmarks) and provide comparison with the latest zserio release
  in your Pull Request.

* Feel free to provide any other measurements.

* Follow [General rules for any code contribution](#general-rules-for-any-code-contribution).

## Do you want to propose a new feature?

* Consider to open a [discussion](https://github.com/ndsev/zserio/discussions/new?category=ideas) or create an
  [issue](https://github.com/ndsev/zserio/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=).

* The proposed changes must not break
  [backward compatibility](doc/ZserioCompatibilityGuide.md#schema-language-compatibility).

## Do you intend to write a new generator?

* Consider to open a [discussion](https://github.com/ndsev/zserio/discussions/new?category=ideas) or create an
  [issue](https://github.com/ndsev/zserio/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=).

* In case you want to implement a new generator (i.e. zserio extension), you can start with
  [Zserio Extension Sample](https://github.com/ndsev/zserio-extension-sample)
  and keep it as an external extension. We will be happy to add a link to your extension in our [README.md](README.md)!

## License

We do not require any formal copyright assignment or contributor license agreement (CLA).
Any contributions intentionally sent upstream are presumed to be offered under terms of BSD 3-Clause License.
See [LICENSE](https://github.com/ndsev/zserio/LICENSE) for details.
