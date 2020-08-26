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
    argParser.add_argument("--filter")
    argParser.add_argument("--pylint_rcfile")
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

    # load tests
    loader = unittest.TestLoader()
    testSuite = unittest.TestSuite()

    testPattern = "*Test.py"
    if "**" in args.filter:
        testFilesPattern = os.path.join(testRoot, args.filter, testPattern)
    else:
        testFilesPattern = os.path.join(testRoot, args.filter, "**", testPattern)

    testFiles = glob.glob(testFilesPattern, recursive=True)
    testDirs = set()
    for globResult in testFiles:
        testDir = os.path.dirname(globResult)
        if testDir not in testDirs:
            testDirs.add(testDir)
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

    return _runMypyOnAllSources(testDirs, runtimePath, testutilsPath)

def _runPylintOnAllSources(args, testDirs):
    print("\nRunning pylint on python tests")

    if not "PYLINT_ENABLED" in os.environ or os.environ["PYLINT_ENABLED"] != '1':
        print("Pylint is disabled.\n")
        return 0

    from testutils import getApiDir, getTestSuiteName

    pylintOptions = ["--persistent=n", "--score=n"]
    if args.pylint_rcfile:
        pylintOptions.append("--rcfile=%s" % (args.pylint_rcfile))

    testDisableOption = ("missing-docstring, invalid-name, duplicate-code, too-many-public-methods, "
                         "too-few-public-methods, c-extension-no-member")
    genDisableOption = ("missing-docstring, invalid-name, no-self-use, duplicate-code, line-too-long, "
                        "singleton-comparison, too-many-instance-attributes, too-many-arguments, "
                        "too-many-public-methods, too-few-public-methods, too-many-locals, too-many-branches, "
                        "too-many-statements, unneeded-not, superfluous-parens, len-as-condition, "
                        "import-self, misplaced-comparison-constant, invalid-unary-operand-type, "
                        "c-extension-no-member")
    genPylintOptions = list(pylintOptions)
    genPylintOptions.append("--ignore=api.py")
    apiDisableOption = ("missing-docstring, unused-import, line-too-long")
    apiPylintOptions = list(pylintOptions)
    apiPylintOptions.append("--ignore-patterns=^.*\\.py(?<!^api\\.py)$")

    for testDir in testDirs:
        testSources = [os.path.join(testDir, child) for child in os.listdir(testDir) if child.endswith(".py")]

        apiDir = getApiDir(testDir)
        if os.path.isdir(apiDir):
            apiSources = [os.path.join(apiDir, child) for child in os.listdir(apiDir)
                          if child.endswith(".py") or os.path.isdir(os.path.join(apiDir, child))]

            testSuiteName = getTestSuiteName(testDir)
            print(testSuiteName)

            print("    test files...")
            pylintResult = _runPylint(testSources, pylintOptions, testDisableOption)
            if pylintResult != 0:
                return pylintResult

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

def _runMypyOnAllSources(testDirs, runtimePath, testutilsPath):
    print("\nRunning mypy on python tests")

    if not "MYPY_ENABLED" in os.environ or os.environ["MYPY_ENABLED"] != '1':
        print("Mypy is disabled.\n")
        return 0

    from testutils import TEST_ARGS, getApiDir
    from mypy import api

    # get directories containing all generated code for active tests
    apiDirs = list()
    for testDir in testDirs:
        apiDir = getApiDir(testDir)
        apiDirs.append(apiDir)

    mypyCacheDir = os.path.join(TEST_ARGS["build_dir"], ".mypy_cache")

    mypyArgs = list()
    mypyArgs.append("--cache-dir=" + mypyCacheDir)
    mypyArgs.append("--show-error-context")
    mypyArgs.append("--show-error-codes")
    mypyArgs.append("--no-strict-optional") # Item "None" of "Optional[Blob]" has no attribute "..."
    # TODO[Mi-L@]: Needed for apsw, but shadows problems with missing imports in language/packages
    mypyArgs.append("--ignore-missing-imports")
    mypyArgs.append(runtimePath)
    mypyArgs.append(testutilsPath)
    mypyArgs.extend(testDirs)
    mypyArgs.extend(apiDirs)

    mypyResult = api.run(mypyArgs)

    if mypyResult[0]:
        print("Type checking report:")
        print(mypyResult[0])

    if mypyResult[1]:
        print("Error report:")
        print(mypyResult[1])

    if mypyResult[2] != 0:
        return mypyResult[2]

    print("Mypy done.\n")

    return 0

def _runPylint(sources, options, disableOption=None):
    if not sources:
        return 0

    pylintOptions = list(sources)
    pylintOptions += options
    if disableOption:
        pylintOptions.append("--disable=%s" % disableOption)

    pylintRunner = pylint.lint.Run(pylintOptions, do_exit=False)
    if pylintRunner.linter.msg_status:
        return pylintRunner.linter.msg_status

    return 0

if __name__ == "__main__":
    sys.exit(main())
