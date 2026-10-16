# Zserio Developer Guide

This guide is for contributors and maintainers of zserio itself: the people who change the compiler, the
extensions, the runtime libraries, the tests or the CI of this repository, and the people who release it. It is
not for developers who use zserio in their own projects; they find their documentation in the
[README](../README.md#documentation).

* [Repository Layout](#repository-layout)
* [Building](#building)
* [Testing](#testing)
* [Continuous Integration](#continuous-integration)
* [C++ Static Analysis](#c-static-analysis)
* [Versioning](#versioning)
* [Releasing](#releasing)
* [zserio.org](#zserioorg)

How to propose a change, the coding style and the review are described in the
[Contribution Guide](../CONTRIBUTING.md).

## Repository Layout

| Path | Content |
|------|---------|
| `compiler/core` | zserio core: the ANTLR grammar, the parser, the AST, the checks and the extension loading |
| `compiler/extensions/<language>` | the extensions `cpp`, `java`, `python`, `doc` and `xml`: Java sources, FreeMarker templates and tests of the generator |
| `compiler/extensions/<language>/runtime` | the runtime libraries of C++, Java and Python with their unit tests and test objects |
| `test/core` | tests of zserio core: schemas with expected errors and warnings |
| `test/extensions` | tests of the generated code, one directory per test, each with its schemas and its `cpp`, `java` and `python` tests |
| `test/data` | submodule [zserio-test-data](https://github.com/ndsev/zserio-test-data) with the schemas and data the tests share |
| `scripts` | Bash scripts to build, pack, test and release zserio |
| `cmake` | CMake helpers: toolchains, compiler warnings, clang-tidy, clang-format and coverage |
| `ant_task`, `build.xml` | the zserio Ant task and the top level Ant build |
| `benchmarks` | the benchmarks and the submodule [zserio-benchmarks-datasets](https://github.com/ndsev/zserio-benchmarks-datasets) |
| `3rdparty` | third party sources: Java libraries, googletest (submodule) and SQLite |
| `doc` | the documentation of zserio, which is also published on [zserio.org](#zserioorg) |
| `.github/workflows` | the CI, release and Pages workflows |

Clone with the submodules, the tests need `test/data`:

```bash
git clone --recursive https://github.com/ndsev/zserio.git
```

## Building

[Zserio Compiler Build Instructions](ZserioBuildInstructions.md) list the prerequisites and the packages of
`scripts/build.sh`, which builds into the directory `distr`. `scripts/build.sh -h` and `scripts/build.sh -e` show
the current packages and the environment variables, e.g. `GCC_VERSION_SUFFIX` or `CLANG_TIDY_BIN`.

The tests do not run on `distr` but on a packed release in `release-<version>`, made by `scripts/release.sh`.
The usual sequence for a linux64 gcc build is:

```bash
scripts/build.sh all-linux64-gcc
scripts/release.sh
scripts/test.sh all-linux64-gcc
```

Building a runtime library package, e.g. `cpp_rt-linux64-gcc`, also runs the unit tests of that runtime library.

## Testing

[`scripts/test.sh`](../scripts/test.sh) runs the tests of `test/core` and `test/extensions` with the release in
`release-<version>`. `scripts/test.sh -h` lists the packages, `scripts/test.sh -e` the environment variables.

```bash
scripts/test.sh core                                        # zserio core tests
scripts/test.sh java python                                 # all Java and Python tests
scripts/test.sh cpp-linux64-gcc -i language/choice_types    # one test directory
scripts/test.sh python -i "language/*" -x "*sql_tables*"    # patterns to include and exclude
scripts/test.sh -p cpp-linux64-gcc                          # purge the test build directory first
```

`ZSERIO_EXTRA_ARGS` passes extra zserio arguments to every test, the CI runs the C++ tests also with
`-withTypeInfoCode -withReflectionCode ...`, see [Continuous Integration](#continuous-integration).

Further test scripts in [`scripts`](../scripts), each with `-h`:

| Script | Tests |
|--------|-------|
| `test_zs.sh` | given zserio sources outside of `test` |
| `test_gen.sh` | that repeated generation of the same schema gives the same sources |
| `test_compat_api.sh` | the tests of an older zserio version against the generated API of this version |
| `test_perf.sh`, `benchmark.sh` | the performance on given zserio sources and the [benchmarks](../benchmarks/README.md) |

A new language feature or fix comes with tests in `test/extensions` for each language, a new check or error of
zserio core with a test in `test/core`.

## Continuous Integration

The workflows *Linux* ([`build_linux.yml`](../.github/workflows/build_linux.yml)) and *Windows*
([`build_windows.yml`](../.github/workflows/build_windows.yml)) build and test zserio in legs: `zserio` (core and
the bundle, needed by every other leg), `cpp`, `java`, `python`, `doc` and `xml`. Two kinds of run exist:

| Run | When | What runs |
|-----|------|-----------|
| smoke run | pull requests into `master`, `release/**` or `*-ci`, and pushes to a branch named `*-ci` | only the Linux legs whose changed paths can affect them, one leg per language, without Windows and without the extra and slow checks |
| full run | pushes to `master` and `release/**`, manual runs, the release workflow (Linux only) | all legs on Linux and Windows with all checks, e.g. all compilers, extra arguments, clang-tidy, the API compatibility check |

[`select_legs.yml`](../.github/workflows/select_legs.yml) decides between the two. In a smoke run,
[`.github/scripts/select_legs.sh`](../.github/scripts/select_legs.sh) maps the changed paths to the legs. The
script is the authority, the list shows its rules in outline:

* `compiler/core`, `scripts`, `cmake` and `.github` run all legs,
* `doc/`, `*.md`, `LICENSE` and `CNAME` run no leg,
* `compiler/extensions/<language>` runs the leg of that language; `compiler/extensions/cpp` and `3rdparty/cpp`
  also the Python leg, which builds the C++ runtime for its C++ binding,
* `test/*/<language>/*` runs the leg of that language, any other test path (e.g. a schema shared by all
  languages) runs all legs,
* any unknown path runs all legs.

A change of the mapping comes with a case in [`.github/scripts/test_select_legs.sh`](../.github/scripts/test_select_legs.sh),
which every run executes first.

A smoke run misses what only a full run checks: the Windows legs, the other compilers and the clang-tidy run over
the tests. That shows up on the next push to `release/**`. To get a full run for a branch before it is merged,
start the workflows by hand: `gh workflow run build_linux.yml -R ndsev/zserio --ref <branch>`, likewise
`build_windows.yml`.

*SonarCloud* ([`sonar_cloud_trigger.yml`](../.github/workflows/sonar_cloud_trigger.yml)) analyses only pushes to
`master` and `release/**`.

## C++ Static Analysis

clang-tidy checks the C++ runtime library and the C++ tests including their generated code, clang-format checks
the runtime library and the hand written test sources against [`.clang-format`](../.clang-format). Both run when
their binary is set: `CLANG_TIDY_BIN=clang-tidy-14` and `CLANG_FORMAT_BIN=clang-format-14` for
`scripts/build.sh` and `scripts/test.sh`. `CLANG_TIDY_ONLY=1` makes `scripts/test.sh` only run clang-tidy over
the C++ tests instead of building and running them, which is what the CI job `cpp-clang-tidy` does in a full run.

The checks are configured per target:

* the runtime library: [`ClangTidyConfig.txt`](../compiler/extensions/cpp/runtime/ClangTidyConfig.txt) and
  [`ClangTidySuppressions.txt`](../compiler/extensions/cpp/runtime/ClangTidySuppressions.txt) in
  `compiler/extensions/cpp/runtime`,
* the tests: a `ClangTidySuppressions.txt` next to the `CMakeLists.txt` of a test directory in `test/extensions`.

A warning which is neither fixed nor suppressed fails the build. The suppressions are matched by
[`cmake/clang_tidy_check.cmake`](../cmake/clang_tidy_check.cmake) against the clang-tidy log, one per line,
`#` starts a comment:

```
rule-definition                    # suppress the rule everywhere
rule-definition:path-regex         # suppress the rule in matching paths
rule-definition:path-regex:10      # suppress the rule on line 10 of matching paths
rule-definition:path-regex:10:1    # suppress the rule at line 10, column 1 of matching paths
```

For example `bugprone-signed-char-misuse:src/zserio/DeltaContext.h:95`. Each suppression gets a comment above it
why the warning is accepted.

The runtime library is built with `WERROR_UNUSED_SUPPRESSIONS`: a suppression which no longer matches a warning
fails the build as well. A line based suppression therefore has to move with its code: a change which shifts
lines of a suppressed file must shift the line numbers of its suppressions in the same pull request. The build
prints the unused suppressions and the unsuppressed warnings, which give the new line numbers.

## Versioning

Zserio has independent versions, each changed by `scripts/update_version.sh <type> <version>` (`-m` commits
the change, `-h` lists the types):

| Type | Version of | Where it lives |
|------|------------|----------------|
| `core` | zserio core `X.Y.Z`, the version of a release | `ZserioVersion.java` and `EXPECTED_ZSERIO_VERSION_STRING` of every `*ExtensionVersion.java` |
| `cpp`, `java`, `python`, `doc`, `xml` | each extension `A.B.C` | its `*ExtensionVersion.java`, for C++ also `CppRuntimeVersion.h` of the C++ runtime |
| `bin`, `json` | the binary and the JSON encoding | [Zserio Encoding Guide](ZserioEncodingGuide.md), [Zserio JSON Guide](ZserioJsonGuide.md) and the extension version files |

What a major, minor or patch version promises to zserio users is described in the
[Zserio Compatibility Guide](ZserioCompatibilityGuide.md); choose the size of each bump by it. The rules as
practised in the releases so far:

* Every release bumps the core version.
* Every core release bumps **every** extension at least by a patch version, also an extension without changes:
  the core bump rewrites `EXPECTED_ZSERIO_VERSION_STRING` in every extension, which decides whether zserio core
  loads the extension, see [Extension API Compatibility](ZserioCompatibilityGuide.md#extension-api-compatibility).
* `bin` and `json` change only when the encoding itself changes, which has not happened so far.
* After the version bumps, `scripts/update_test_objects.sh -m all` regenerates the test objects of the C++, Java
  and Python runtime libraries (`compiler/extensions/*/runtime/test*/test_object`). The header of each generated
  source names the generator and core version which generated it, so the test objects of a release are
  generated by that release.

The commit order on `release/X.Y` before a release is therefore, one commit each:

```bash
scripts/update_version.sh -m core X.Y.Z
scripts/update_version.sh -m cpp A.B.C
scripts/update_version.sh -m java A.B.C
scripts/update_version.sh -m python A.B.C
scripts/update_version.sh -m doc A.B.C
scripts/update_version.sh -m xml A.B.C
scripts/update_test_objects.sh -m all
```

## Releasing

This section walks a zserio release `X.Y.Z` from the branch `release/X.Y` to the published artifacts and
ends with the matching `zserio-cpp17` release. Each step names its command or button, what to check before
the next step and what to do when it fails.

A release publishes:

* the GitHub release `vX.Y.Z` with `zserio-X.Y.Z-bin.zip` and `zserio-X.Y.Z-runtime-libs.zip`,
* the wheel `zserio` on [PyPI](https://pypi.org/project/zserio/), built by
  [zserio-pypi](https://github.com/ndsev/zserio-pypi),
* the jars `io.github.ndsev:zserio` and `io.github.ndsev:zserio-runtime` on Maven Central,
* the site [zserio.org](https://zserio.org) with the runtime docs of the release,
* updates of the dependent repositories (Conan Center Index fork, extension sample, tutorials, Streamlit app),
* the matching release of the [C++17 extension](https://github.com/ndsev/zserio-cpp17).

Everything from the GitHub release to zserio.org is done by the workflow
[`.github/workflows/release.yml`](../.github/workflows/release.yml) on a pushed tag `vX.Y.Z`. A pushed tag cannot
be taken back once PyPI has the wheel, so everything before the tag is about making that run boring.

Organisations that use zserio may run their own checks against `release/X.Y`; they are outside this guide.

### Overview

| # | Step | Command or button | Publishes |
|---|------|-------------------|-----------|
| 0 | [Prerequisites](#0-prerequisites) | GitHub settings, `gh api` | nothing |
| 1 | [Branch `release/X.Y`](#1-branch-releasexy) | `git push origin release/X.Y` | nothing |
| 2 | [Green CI](#2-green-ci) | push to `release/X.Y` | nothing |
| 3 | [Dry run of `release.yml`](#3-dry-run-of-releaseyml) | `gh workflow run release.yml` | nothing |
| 4 | [Draft release notes](#4-draft-release-notes) | `gh release create --draft` | nothing |
| 5 | [Tag](#5-tag) | `git push origin vX.Y.Z` | GitHub release |
| 6 | [PyPI](#6-pypi) | *Review deployments* → *Approve and deploy* | wheel on PyPI |
| 7 | [Central Portal publish](#7-central-portal-publish) | *Publish* on central.sonatype.com | jars on Maven Central |
| 8 | [zserio.org](#8-zserioorg) | none, part of the tag run | zserio.org |
| 9 | [`post_release.sh`](#9-post_releasesh) | `scripts/post_release.sh -v X.Y.Z` | commits in dependent repos |
| 10 | [Matching `zserio-cpp17` release](#10-matching-zserio-cpp17-release) | `gh release create` in `ndsev/zserio-cpp17` | C++17 extension |
| 11 | [Merge into `master`](#11-merge-into-master) | pull request into `master`, *Create a merge commit* | nothing |

### 0. Prerequisites

You need write access to `ndsev/zserio` and `ndsev/zserio-cpp17`, be a required reviewer of the GitHub
environment `pypi`, and have access to the `io.github.ndsev` namespace on
[central.sonatype.com](https://central.sonatype.com).

The release workflow relies on these repository settings, none of them is in the repository files:

| Setting | Expected | Check |
|---------|----------|-------|
| Environment `pypi` | required reviewers; deployment refs: tags `v*` and the branch the dry run runs on | `gh api repos/ndsev/zserio/environments/pypi/deployment-branch-policies -q '.branch_policies[].name'` |
| Environment `maven-central` | secrets `CENTRAL_TOKEN_USERNAME`, `CENTRAL_TOKEN_PASSWORD`, `MAVEN_GPG_KEY`, `MAVEN_GPG_PASSPHRASE`; deployment refs: tags `v*` and the branch the dry run runs on | `gh api repos/ndsev/zserio/environments/maven-central/deployment-branch-policies -q '.branch_policies[].name'` |
| PyPI project `zserio` | trusted publisher: repository `ndsev/zserio`, workflow `release.yml`, environment `pypi` | *Your projects* → `zserio` → *Publishing* on pypi.org |
| Pages | source *GitHub Actions*, custom domain `zserio.org` | `gh api repos/ndsev/zserio/pages -q '.build_type, .cname'` prints `workflow` and `zserio.org` |
| Ruleset `master merge commits only` | target branch `master`: pull requests may be merged only by *Create a merge commit* (step 11), no deletion, no force push | `gh api repos/ndsev/zserio/rules/branches/master -q '[.[].type]'` lists `pull_request`, `deletion` and `non_fast_forward` |
| Ruleset `release tags immutable` | target tags `refs/tags/v*`: no deletion, no update, no force push | `gh api repos/ndsev/zserio/rulesets -q '.[].name'` lists it |

**Check before step 1:** every row matches. The dry run in step 3 runs both environments from one ref, so that
ref must be allowed in `pypi` and in `maven-central`.

**If it fails:** a repository admin fixes the setting under *Settings* → *Environments* or *Settings* →
*Pages* or *Settings* → *Rules*. Do not start the release with a mismatch: a rejected environment fails the tag run half way.

### 1. Branch `release/X.Y`

All work for the release goes to `release/X.Y`, which starts from `master`:

```bash
git fetch origin
git switch -c release/X.Y origin/master
git push -u origin release/X.Y
```

Changes for the release reach the branch by pull requests with base `release/X.Y`. Before the release, bump the
core and every extension and regenerate the runtime test objects by the commits listed in
[Versioning](#versioning), then push them: `git push origin release/X.Y`.

**Check before step 2:** on `origin/release/X.Y`,
`grep VERSION_STRING compiler/core/src/zserio/tools/ZserioVersion.java` shows `"X.Y.Z"`,
`grep -r "EXPECTED_ZSERIO_VERSION_STRING =" compiler/extensions` shows `"X.Y.Z"` for every extension, and each
extension's own version is higher than in the previous release. The release workflow takes the version only from
the built `zserio-X.Y.Z-bin.zip` and refuses a tag which does not match it.

**If it fails:** fix the version by another `update_version.sh` commit on `release/X.Y`, followed by
`scripts/update_test_objects.sh -m all`.

### 2. Green CI

A push to `release/X.Y` runs the Linux and Windows workflows with all legs (pull requests run only the legs
their paths affect, see [Continuous Integration](#continuous-integration)).

```bash
gh run list -R ndsev/zserio --branch release/X.Y --limit 10
```

**Check before step 3:** the newest *Linux* and *Windows* runs on the head commit of `release/X.Y` are
successful. `gh run view <run-id> -R ndsev/zserio` shows the job list.

**If it fails:** fix the failure by a pull request into `release/X.Y` and wait for the next push run. A flaky
job is re-run by `gh run rerun <run-id> --failed -R ndsev/zserio`; record in the pull request that it was
flaky.

### 3. Dry run of `release.yml`

The dry run runs every job of the release without publishing anything:

* the GitHub release is created as draft `vX.Y.Z-dry-run-<run id>` and deleted by the same job,
* the wheel is built and tested from an already published release (`wheel_tag`), as the new release does not
  exist yet,
* the OIDC token is exchanged for a PyPI token, which is only checked to be scoped to one project,
* the jars are uploaded to the Central Portal as version `X.Y.Z-dry-run.<run id>`, validated and dropped,
* zserio.org is built and uploaded as the Pages artifact, it is not deployed.

Start it from `release/X.Y`, with the latest published release as `wheel_tag`:

```bash
gh workflow run release.yml -R ndsev/zserio --ref release/X.Y -f wheel_tag=v<latest published version>
gh run list -R ndsev/zserio --workflow release.yml --limit 1
```

A manual run needs `release.yml` on the default branch. Before that, push the branch
`release-dry-run/<wheel_tag>` instead; the run takes `wheel_tag` from the branch name:

```bash
git push origin release/X.Y:refs/heads/release-dry-run/v<latest published version>
```

The run title reads *Release dry run from &lt;ref&gt; - no upload*. The job *Dry run: PyPI token check, no
upload* waits for the `pypi` environment approval: open the run, *Review deployments*, tick `pypi`,
*Approve and deploy*.

**Check before step 4:** the run is successful, with these jobs green: `github_release`, `wheel`,
*Dry run: PyPI token check, no upload*, *Dry run: Central Portal validation, then dropped* and
*Dry run: build zserio.org, no deployment / build*. *Release: upload to PyPI* and *Deploy to zserio.org* are
skipped. [Deployments](https://central.sonatype.com/publishing/deployments) shows no deployment left over.
Delete the branch `release-dry-run/...` if you pushed one.

**If it fails:**

| Failing job or message | What to do |
|------------------------|------------|
| `build` | the same as a red CI in step 2 |
| `Branch "<ref>" is not allowed to deploy to <environment>` | the environment does not allow the ref, see step 0 |
| `github_release`: `Expected one zserio binary zip` | the `build / release` job did not produce the zips; check its log |
| `wheel` | `zserio-pypi` cannot build or test the wheel of `wheel_tag`; fix it in `ndsev/zserio-pypi` and run again with `-f zserio_pypi_ref=<branch>` |
| *PyPI token check*: `PyPI did not return a token` | the trusted publisher on pypi.org does not match repository, workflow or environment, see step 0 |
| *Central Portal validation* | the log names the failed validation rule (signature, POM, javadoc); a missing or wrong secret in `maven-central` fails before the upload. If the job was cancelled before its cleanup, drop the deployment by hand on [Deployments](https://central.sonatype.com/publishing/deployments) |
| *build zserio.org* | `scripts/build_web_pages.sh` or Jekyll failed; a version which is not newer than the published ones is only a notice in a dry run |
| *build zserio.org*: `exceeds the GitHub Pages limit of 1 GB` | the next release would not fit on zserio.org; reduce the published runtime docs before the release |

Run the dry run again after each fix, until it is green on the head commit of `release/X.Y`.

### 4. Draft release notes

The tag run publishes a prepared draft release of the tag with its notes; without a draft it creates the
release with notes generated by GitHub. Write the notes in the format of the
[previous releases](https://github.com/ndsev/zserio/releases): one section per changed component and version
(*Zserio Core X.Y.Z*, *C++ Extension A.B.C*, ...), then *Fixes* and *Improvements* with the issue numbers.

```bash
gh release create vX.Y.Z -R ndsev/zserio --draft --target release/X.Y --title "Zserio vX.Y.Z" \
        --notes-file release-notes.md
```

**Check before step 5:** `gh release view vX.Y.Z -R ndsev/zserio --json isDraft,name` prints
`"isDraft": true` and `"name": "Zserio vX.Y.Z"`. The draft has no assets; the tag run uploads them.

**If it fails:** fix the draft by `gh release edit vX.Y.Z -R ndsev/zserio --notes-file release-notes.md`.

### 5. Tag

A release is tagged on `release/X.Y`, not on `master`: the head commit of `release/X.Y` has already passed the
full CI run (step 2) and the dry run (step 3), so tagging it needs no further build round. Merging into
`master` first would produce a new commit that has to pass CI again before it could be tagged. `master` takes
the tagged commit afterwards by a merge commit, see [step 11](#11-merge-into-master).

Tag the head commit of `release/X.Y` which passed steps 2 and 3, and push the tag:

```bash
git fetch origin
git tag -a vX.Y.Z origin/release/X.Y -m "Zserio vX.Y.Z"
git push origin vX.Y.Z
```

The tag starts *Release vX.Y.Z - publishes to PyPI and Maven Central*. It builds all legs again, then
`github_release` uploads the two zips to the draft and publishes it.

**Check before step 6:** `github_release` is green and

```bash
gh release view vX.Y.Z -R ndsev/zserio --json isDraft,assets -q '.isDraft, [.assets[].name]'
```

prints `false` and both `zserio-X.Y.Z-bin.zip` and `zserio-X.Y.Z-runtime-libs.zip`.

**If it fails:**

* `build` fails: nothing is published yet. Delete the tag, fix `release/X.Y` and start again at step 2. The
  ruleset `release tags immutable` blocks the deletion, so a repository admin switches it off for that moment:

  ```bash
  ID=$(gh api repos/ndsev/zserio/rulesets -q '.[] | select(.name == "release tags immutable") | .id')
  gh api -X PUT repos/ndsev/zserio/rulesets/${ID} -f enforcement=disabled
  git push origin :refs/tags/vX.Y.Z && git tag -d vX.Y.Z
  gh api -X PUT repos/ndsev/zserio/rulesets/${ID} -f enforcement=active
  ```
* `Tag vX.Y.Z does not match the built zserio version`: the core version on the tagged commit is not `X.Y.Z`.
  Delete the tag as above and fix the version, step 1.
* The release exists but an asset is missing: upload it by
  `gh release upload vX.Y.Z <zip> -R ndsev/zserio` from the run's artifact `linux-release-X.Y.Z`
  (`gh run download <run-id> -R ndsev/zserio -n linux-release-X.Y.Z`).

Once the release is published, do not delete or move the tag: PyPI, the Conan recipe and the tutorials
download from it.

### 6. PyPI

The job `wheel` builds the wheel with `zserio-pypi` from the GitHub release just published and tests it. The
job *Release: upload to PyPI* waits for the `pypi` environment approval: open the run, *Review deployments*,
tick `pypi`, *Approve and deploy*. It uploads by trusted publishing, no PyPI token is stored.

**Check before step 7:** *Release: upload to PyPI* is green and the release is installable:

```bash
python3 -m venv /tmp/zserio-check && /tmp/zserio-check/bin/pip install zserio==X.Y.Z
/tmp/zserio-check/bin/zserio --version
```

prints `Core version X.Y.Z`.

**If it fails:**

* `wheel` fails: nothing is on PyPI yet. Fix `zserio-pypi` and re-run the failed jobs by
  `gh run rerun <run-id> --failed -R ndsev/zserio`; the wheel is built from the published GitHub release, the
  tag stays.
* The upload fails with a trusted publisher error: fix the publisher on pypi.org (step 0) and re-run the
  failed jobs.
* A wrong file reached PyPI: a version can never be uploaded again on PyPI. Yank it on pypi.org
  (*Manage* → *Releases* → *Options* → *Yank*) and release `X.Y.Z+1`.

### 7. Central Portal publish

The job *Release: upload to the Central Portal, publish by hand* signs the zserio and zserio-runtime jars and
uploads them as one deployment, which waits validated. Publishing is a button:
[Deployments](https://central.sonatype.com/publishing/deployments) → the deployment `X.Y.Z` → *Publish*.

**Check before step 8:** the deployment changes from *Validated* to *Published*; then the jars appear on
Maven Central, which can take a while:

```bash
curl -sSf -o /dev/null -w "%{http_code}\n" \
        https://repo1.maven.org/maven2/io/github/ndsev/zserio/X.Y.Z/zserio-X.Y.Z.pom
```

prints `200`. `post_release.sh` downloads the jar from Maven Central for the Java tutorial, so wait for it.

**If it fails:**

* The job fails: the log names the failed validation rule; nothing is published. Drop the failed deployment on
  the Deployments page, fix the cause and re-run the failed jobs.
* The deployment is *Validated* but wrong: *Drop* it instead of *Publish*. A version published on Maven
  Central can never be changed or deleted.

### 8. zserio.org

The jobs *Release: deploy zserio.org / build* and *Release: deploy zserio.org / Deploy to zserio.org* run the
Pages workflow [`.github/workflows/pages.yml`](../.github/workflows/pages.yml) with the runtime libs of the
release and deploy the site. They need nothing from you.

**Check before step 9:** both jobs are green and the runtime docs of the release are online:

```bash
curl -sSf -o /dev/null -w "%{http_code}\n" https://zserio.org/doc/runtime/X.Y.Z/cpp/
```

prints `200`, likewise for `java/` and `python/`.

**If it fails:**

* `Built version X.Y.Z is not a release newer than ...`: a newer release than `X.Y.Z` is already published,
  e.g. for a patch release of an older `X.Y`. The site is not deployed; decide by hand which ref to deploy.
* The deploy job fails: check that the Pages source is *GitHub Actions*, see step 0.
* Any other failure: re-deploy from the tag by the Pages workflow, which takes the runtime libs from the
  published release:

  ```bash
  gh workflow run pages.yml -R ndsev/zserio -f source_ref=vX.Y.Z -f deploy=true
  ```

### 9. `post_release.sh`

[`scripts/post_release.sh`](../scripts/post_release.sh) updates the repositories which depend on the release, each
in a local clone next to this one (`../zserio-conan-center-index`, `../zserio-extension-sample`,
`../zserio-tutorial-cpp`, `../zserio-tutorial-java`, `../zserio-tutorial-python`, `../zserio-streamlit`, or the
directories in `ZSERIO_*_DIR`, see `scripts/post_release.sh -e`). It commits in each repository and stops
with the push command to run after you have checked the commit; it never pushes.

```bash
scripts/post_release.sh -v X.Y.Z
scripts/post_release.sh -v X.Y.Z tutorial_java tutorial_python    # only some repositories
```

It needs `git`, GNU `sed` and `tail` (on macOS `SED=gsed TAIL=gtail`), `shasum` for `conan`, `cmake` for
`tutorial_cpp`, `mvn` and `java` for `tutorial_java`, `python3` with `virtualenv` for `tutorial_python`. The
`conan` target downloads the release assets from GitHub, `tutorial_java` the jar from Maven Central (step 7)
and `tutorial_python` the wheel from PyPI (step 6).

**Check before step 10:** for each repository, review the commit the script made, run the command it prints
(e.g. `conan create recipes/zserio/all/conanfile.py --version X.Y.Z` for Conan), then run the printed push
command. For Conan, open a pull request from the pushed branch `zserio-X.Y.Z` to `conan-io/conan-center-index`.

**If it fails:** the script stops at the first failing repository with the reason (e.g.
`Sources in '...' are not generated by Zserio core X.Y.Z`). Fix the cause and run the script again for that
repository and the ones after it; repositories already up to date are reported and skipped.

### 10. Matching `zserio-cpp17` release

Zserio core loads only an extension which expects the same major.minor version and not a newer patch version,
so zserio `X.Y.Z` ignores a C++17 extension built for an older `X.Y`. Every core release `X.Y.0` therefore needs
a C++17 extension release `A.B.C` which expects `X.Y.Z`; a patch release `X.Y.Z` needs one only when the
extension must change. zserio-cpp17 has no release workflow; its release is made by hand.

1. On `release/A.B` of `ndsev/zserio-cpp17`, set in
   `extension/src/zserio/extension/cpp17/Cpp17ExtensionVersion.java` the extension version
   (`CPP17_EXTENSION_VERSION_STRING`, `CPP17_EXTENSION_VERSION_NUMBER`) to `A.B.C` and
   `EXPECTED_ZSERIO_VERSION_STRING` to `X.Y.Z`.
2. Once zserio `vX.Y.Z` is published (step 5), run the zserio-cpp17 CI on `release/A.B`:

   ```bash
   gh workflow run build_linux.yml -R ndsev/zserio-cpp17 --ref release/A.B
   gh workflow run build_windows.yml -R ndsev/zserio-cpp17 --ref release/A.B
   ```

   The extension jobs build against the newest published zserio release and fail with
   `Zserio core ... would not load the C++17 extension` when the versions do not match.
3. Download the artifacts of the green Linux run and pack the three release assets:

   ```bash
   gh run download <run-id> -R ndsev/zserio-cpp17 -n zserio-cpp17-java8 -D cpp17-bin
   gh run download <run-id> -R ndsev/zserio-cpp17 -n zserio-java8 -D cpp17-bundle
   gh run download <run-id> -R ndsev/zserio-cpp17 -n zserio-runtime-cpp -D cpp17-runtime
   (cd cpp17-bin && zip -r ../zserio-cpp17-A.B.C-bin.zip .)
   (cd cpp17-bundle && zip -r ../zserio-cpp17-A.B.C-bundle.zip .)
   (cd cpp17-runtime && zip -r ../zserio-cpp17-A.B.C-runtime-lib.zip .)
   ```

   `-bin.zip` holds `zserio_cpp17.jar` with its javadocs and sources jars, `-bundle.zip` holds `zserio.jar`
   (core with the C++17 extension) with its javadocs and sources jars, `-runtime-lib.zip` the C++17 runtime
   library.
4. Tag and publish the release with its notes (format of the
   [previous releases](https://github.com/ndsev/zserio-cpp17/releases), naming the zserio version it is built
   for):

   ```bash
   gh release create vA.B.C -R ndsev/zserio-cpp17 --target release/A.B --title "vA.B.C" \
           --notes-file release-notes.md \
           zserio-cpp17-A.B.C-bin.zip zserio-cpp17-A.B.C-bundle.zip zserio-cpp17-A.B.C-runtime-lib.zip
   ```

**Check:** `java -jar zserio.jar -h` from `zserio-cpp17-A.B.C-bundle.zip` lists
`C++17 Generator version A.B.C`, and `gh release view vA.B.C -R ndsev/zserio-cpp17` shows the three assets.

**If it fails:**

* The CI reports that core would not load the extension: `EXPECTED_ZSERIO_VERSION_STRING` does not match the
  published zserio release; fix it by a pull request into `release/A.B` and run the CI again.
* The bundle does not list the C++17 generator: the zips were packed from a run against another core version;
  repack them from a run after step 5.
* A wrong asset was published: replace it by
  `gh release upload vA.B.C <zip> --clobber -R ndsev/zserio-cpp17`.

### 11. Merge into `master`

Once the tag run of step 5 is green, `master` takes the release by a pull request from `release/X.Y`, merged by
*Create a merge commit*. The merge commit keeps the tagged commit and all commits before it unchanged in the
history of `master`, so `vX.Y.Z` is reachable from `master`. *Squash and merge* and *Rebase and merge* would
put rewritten copies on `master`: the tag would point to a commit `master` does not contain, and a later merge
of `release/X.Y` would bring the originals back next to their copies. The ruleset of step 0 therefore allows
only merge commits into `master`.

Pull requests into `release/X.Y` do not close their issues, as GitHub closes an issue only when its closing
keyword reaches the default branch `master`. The release pull request carries these keywords for all of them:

```bash
gh pr list -R ndsev/zserio --base release/X.Y --state merged --limit 500 --json body -q '.[].body' \
        | grep -oiE '(close[sd]?|fix(e[sd])?|resolve[sd]?) #[0-9]+' | sed 's/^[^#]*/Closes /' | sort -u \
        > release-closes.md
gh pr create -R ndsev/zserio --base master --head release/X.Y --title "Release X.Y.Z" \
        --body-file release-closes.md
```

Merge it on GitHub by *Create a merge commit*, or `gh pr merge <number> -R ndsev/zserio --merge`.

**Check:** `git fetch origin && git merge-base --is-ancestor vX.Y.Z origin/master` exits 0, and the issues
listed in `release-closes.md` are closed. After that, `release/X.Y` is no longer needed for the history of
`master`; keep it while a patch release `X.Y.Z+1` may still come from it.

**If it fails:**

* The pull request has conflicts: `master` got commits which are not on `release/X.Y`. Resolve them in the merge
  as for any pull request; the tagged commit stays unchanged.
* The merge button offers only *Squash and merge* or *Rebase and merge*: the ruleset or the repository setting
  is missing, see step 0. Do not merge with another method.

The release of zserio `X.Y.Z` is complete when step 11 is.

## zserio.org

The site [zserio.org](https://zserio.org) is built by Jekyll from the zserio sources of a release with the
configuration [`.github/pages/_config.yml`](../.github/pages/_config.yml): the README, the documents in `doc`
(this guide included) and the runtime library documentation of every published version. It is deployed to GitHub
Pages by the workflow [`pages.yml`](../.github/workflows/pages.yml); the repository Pages source is *GitHub
Actions* and the custom domain is in [`CNAME`](../CNAME).

* The release workflow deploys the site of each release tag, see [step 8](#8-zserioorg) of Releasing.
* The runtime docs up to 2.19.0 come from the archive kept by the tag `web-pages-archive`, the newer ones from the
  `zserio-X.Y.Z-runtime-libs.zip` of each published release. Of each minor line only the highest patch version is
  published, the older patch versions redirect to it.
* [`scripts/build_web_pages.sh`](../scripts/build_web_pages.sh) assembles the site sources locally,
  `scripts/build_web_pages.sh -h` shows how.
* A push to a branch `pages-dry-run/<ref>` builds the site of `<ref>` without deploying it. A manual run of
  `pages.yml` with `deploy` checked re-deploys the site, e.g. of the latest release tag:

  ```bash
  gh workflow run pages.yml -R ndsev/zserio -f source_ref=vX.Y.Z -f deploy=true
  ```

A change of `doc` reaches zserio.org only with the next deployment, normally the next release.
