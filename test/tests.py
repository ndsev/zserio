"""
Module used as a test runner for integration tests.
This module also executes pylint for test files and generated files.
"""

import unittest
import sys
import os
import argparse
import glob
import pylint.lint

def main():
    testRoot = os.path.dirname(os.path.realpath(__file__))

    testutilsPath = os.path.join(testRoot, "utils", "python")
    sys.path.append(testutilsPath)
    from testutils import TEST_ARGS

    argParser = argparse.ArgumentParser()
    argParser.add_argument("--build_dir")
    argParser.add_argument("--release_dir")
    argParser.add_argument("--java")
    argParser.add_argument("--verbosity", type=int)
    argParser.add_argument("--filter", help="comma separated list of filters")
    argParser.add_argument("--pylint_rcfile")
    argParser.add_argument("--pylint_rcfile_test")
    argParser.add_argument("--mypy_config_file")
    argParser.set_defaults(filter="**", verbosity=2)
    args = argParser.parse_args()
    if args.build_dir:
        TEST_ARGS["build_dir"] = args.build_dir
    if args.release_dir:
        TEST_ARGS["release_dir"] = args.release_dir
    if args.java:
        TEST_ARGS["java"] = args.java

    # path to zserio runtime release
    runtimePath = os.path.join(TEST_ARGS["release_dir"], "runtime_libs", "python")
    sys.path.append(runtimePath)

    sysPathBeforeTests = list(sys.path)

    # detect test directories
    testPattern = "*Test.py"
    testDirs = set()

    for testFilter in args.filter.split(","):
        if testFilter.endswith("**"):
            testFilesPattern = os.path.join(testRoot, testFilter, testPattern)
        else:
            testFilesPattern = os.path.join(testRoot, testFilter, "**", testPattern)

        testFiles = glob.glob(testFilesPattern, recursive=True)
        for globResult in testFiles:
            testDir = os.path.dirname(globResult)
            if testDir not in testDirs:
                testDirs.add(testDir)

    # sort test dirs
    testDirs = sorted(testDirs)

    # load tests
    loader = unittest.TestLoader()
    testSuite = unittest.TestSuite()
    for testDir in testDirs:
        loadedTests = loader.discover(testDir, pattern=testPattern, top_level_dir=testDir)
        testSuite.addTest(loadedTests)

    runner = unittest.TextTestRunner(verbosity=args.verbosity)
    testResult = runner.run(testSuite)
    if not testResult.wasSuccessful():
        return 1

    # restore sys.path to get rid of what test runner recently added
    sys.path = sysPathBeforeTests

    # run pylint
    pylintResult = _runPylintOnAllSources(args, testDirs)
    if pylintResult != 0:
        return pylintResult

    return _runMypyOnAllSources(args, testDirs, runtimePath, testutilsPath)

def _runPylintOnAllSources(args, testDirs):
    print("\nRunning pylint on python tests")

    if not "PYLINT_ENABLED" in os.environ or os.environ["PYLINT_ENABLED"] != '1':
        print("Pylint is disabled.\n")
        return 0

    from testutils import getApiDir, getTestSuiteName

    testDisableOption = ("missing-docstring, duplicate-code, too-many-public-methods, "
                         "too-few-public-methods, c-extension-no-member")
    pylintOptions = ["--persistent=n", "--score=n"]
    if args.pylint_rcfile_test:
        pylintOptions.append(f"--rcfile={args.pylint_rcfile_test}")

    genDisableOption = ("missing-docstring, no-self-use, duplicate-code, line-too-long, "
                        "singleton-comparison, too-many-instance-attributes, too-many-arguments, "
                        "too-many-public-methods, too-few-public-methods, too-many-locals, "
                        "too-many-branches, too-many-statements, unneeded-not, superfluous-parens, "
                        "import-self, invalid-unary-operand-type, c-extension-no-member")
    genPylintOptions = ["--persistent=n", "--score=n", "--ignore=api.py"]
    if args.pylint_rcfile:
        genPylintOptions.append(f"--rcfile={args.pylint_rcfile}")

    apiDisableOption = ("missing-docstring, unused-import, line-too-long")
    apiPylintOptions = ["--persistent=n", "--score=n", "--ignore-patterns=^.*\\.py(?<!^api\\.py)$"]
    if args.pylint_rcfile:
        apiPylintOptions.append(f"--rcfile={args.pylint_rcfile}")

    for testDir in testDirs:
        testSources = [os.path.join(testDir, child) for child in os.listdir(testDir) if child.endswith(".py")]

        testSuiteName = getTestSuiteName(testDir)
        print(testSuiteName)

        print("    test files...")
        pylintResult = _runPylint(testSources, pylintOptions, testDisableOption)
        if pylintResult != 0:
            return pylintResult

        apiDir = getApiDir(testDir)
        if os.path.isdir(apiDir):
            apiSources = [os.path.join(apiDir, child) for child in os.listdir(apiDir)
                          if child.endswith(".py") or os.path.isdir(os.path.join(apiDir, child))]

            sys.path.append(apiDir)

            print("    generated files...") # except api.py files
            pylintResult = _runPylint(apiSources, genPylintOptions, genDisableOption)
            if pylintResult != 0:
                return pylintResult

            print("    generated api.py files...")
            pylintResult = _runPylint(apiSources, apiPylintOptions, apiDisableOption)
            if pylintResult != 0:
                return pylintResult

            sys.path.remove(apiDir)

    print("Pylint done.\n")

    return 0

def _runPylint(sources, options, disableOption=None):
    if not sources:
        return 0

    pylintOptions = list(sources)
    pylintOptions += options
    if disableOption:
        pylintOptions.append(f"--disable={disableOption}")

    if "PYLINT_EXTRA_ARGS" in os.environ:
        pylintOptions += os.environ["PYLINT_EXTRA_ARGS"].split()

    pylintRunner = pylint.lint.Run(pylintOptions, do_exit=False)
    if pylintRunner.linter.msg_status:
        return pylintRunner.linter.msg_status

    return 0

def _runMypyOnAllSources(args, testDirs, runtimePath, testutilsPath):
    print("\nRunning mypy on python tests")

    if not "MYPY_ENABLED" in os.environ or os.environ["MYPY_ENABLED"] != '1':
        print("Mypy is disabled.\n")
        return 0

    from testutils import TEST_ARGS, getApiDir, getTestSuiteName
    from mypy import api

    os.environ["MYPYPATH"] = runtimePath + os.pathsep + testutilsPath

    mypyCacheDir = os.path.join(TEST_ARGS["build_dir"], ".mypy_cache")
    mypyArgs = []
    mypyArgs.append(f"--cache-dir={mypyCacheDir}")
    if args.mypy_config_file:
        mypyArgs.append(f"--config-file={args.mypy_config_file}")
    mypyArgs.append("--no-strict-optional") # Item "None" of "Optional[Blob]" has no attribute "..."

    for testDir in testDirs:
        apiDir = getApiDir(testDir)
        testSuiteName = getTestSuiteName(testDir)
        print(testSuiteName + " ... ", end='', flush=True)

        mypyArgsForTest = list(mypyArgs)
        _loadMypyExtraArgsFile(testDir, mypyArgsForTest)
        if os.path.exists(apiDir):
            mypyArgsForTest.append(apiDir)
        mypyArgsForTest.append(testDir)

        mypyResult = api.run(mypyArgsForTest)

        if mypyResult[2] != 0:
            print("FAILED!")
            if mypyResult[0]:
                print("Type checking report:")
                print(mypyResult[0])
            if mypyResult[1]:
                print("Error report:")
                print(mypyResult[1])

            return mypyResult[2]

        else:
            print(mypyResult[0], end='')

    print("Mypy done.\n")

    return 0

def _loadMypyExtraArgsFile(testDir, mypyArgsForTest):
    argsFilename = os.path.join(testDir, "mypy_extra_args.txt")
    if os.path.isfile(argsFilename):
        with open(argsFilename, 'r', encoding='utf-8') as argsFile:
            for argLine in argsFile:
                arg = argLine.rstrip('\n')
                if arg and not arg.startswith('#'):
                    mypyArgsForTest.append(arg)

if __name__ == "__main__":
    sys.exit(main())
