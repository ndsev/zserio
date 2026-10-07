# Releasing Zserio

This document walks a zserio release `X.Y.Z` from the branch `release/X.Y` to the published artifacts and
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
[`.github/workflows/release.yml`](.github/workflows/release.yml) on a pushed tag `vX.Y.Z`. A pushed tag cannot
be taken back once PyPI has the wheel, so everything before the tag is about making that run boring.

Organisations that use zserio may run their own checks against `release/X.Y`; they are outside this document.

## Overview

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

## 0. Prerequisites

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

**Check before step 1:** every row matches. The dry run in step 3 runs both environments from one ref, so that
ref must be allowed in `pypi` and in `maven-central`.

**If it fails:** a repository admin fixes the setting under *Settings* → *Environments* or *Settings* →
*Pages*. Do not start the release with a mismatch: a rejected environment fails the tag run half way.

## 1. Branch `release/X.Y`

All work for the release goes to `release/X.Y`, which starts from `master`:

```bash
git fetch origin
git switch -c release/X.Y origin/master
git push -u origin release/X.Y
```

Changes for the release reach the branch by pull requests with base `release/X.Y`. Before the release, bump the
versions the release notes will name, one commit per version type:

```bash
scripts/update_version.sh -m core X.Y.Z
scripts/update_version.sh -m cpp A.B.C       # only for extensions which changed, also java, python, ...
git push origin release/X.Y
```

`scripts/update_version.sh -h` lists the version types (`core`, `bin`, `json`, `cpp`, `java`, `python`,
`doc`, `xml`).

**Check before step 2:** `grep VERSION_STRING compiler/core/src/zserio/tools/ZserioVersion.java` on
`origin/release/X.Y` shows `"X.Y.Z"`. The release workflow takes the version only from the built
`zserio-X.Y.Z-bin.zip` and refuses a tag which does not match it.

**If it fails:** fix the version by another `update_version.sh` commit on `release/X.Y`.

## 2. Green CI

A push to `release/X.Y` runs the Linux and Windows workflows with all legs (pull requests run only the legs
their paths affect, see [CONTRIBUTING.md](CONTRIBUTING.md)).

```bash
gh run list -R ndsev/zserio --branch release/X.Y --limit 10
```

**Check before step 3:** the newest *Linux* and *Windows* runs on the head commit of `release/X.Y` are
successful. `gh run view <run-id> -R ndsev/zserio` shows the job list.

**If it fails:** fix the failure by a pull request into `release/X.Y` and wait for the next push run. A flaky
job is re-run by `gh run rerun <run-id> --failed -R ndsev/zserio`; record in the pull request that it was
flaky.

## 3. Dry run of `release.yml`

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

Run the dry run again after each fix, until it is green on the head commit of `release/X.Y`.

## 4. Draft release notes

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

## 5. Tag

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

* `build` fails: nothing is published yet. Delete the tag (`git push origin :refs/tags/vX.Y.Z` and
  `git tag -d vX.Y.Z`), fix `release/X.Y` and start again at step 2.
* `Tag vX.Y.Z does not match the built zserio version`: the core version on the tagged commit is not `X.Y.Z`.
  Delete the tag as above and fix the version, step 1.
* The release exists but an asset is missing: upload it by
  `gh release upload vX.Y.Z <zip> -R ndsev/zserio` from the run's artifact `linux-release-X.Y.Z`
  (`gh run download <run-id> -R ndsev/zserio -n linux-release-X.Y.Z`).

Once the release is published, do not delete or move the tag: PyPI, the Conan recipe and the tutorials
download from it.

## 6. PyPI

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

## 7. Central Portal publish

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

## 8. zserio.org

The jobs *Release: deploy zserio.org / build* and *Release: deploy zserio.org / Deploy to zserio.org* run the
Pages workflow [`.github/workflows/pages.yml`](.github/workflows/pages.yml) with the runtime libs of the
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

## 9. `post_release.sh`

[`scripts/post_release.sh`](scripts/post_release.sh) updates the repositories which depend on the release, each
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

## 10. Matching `zserio-cpp17` release

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

The release of zserio `X.Y.Z` is complete when step 10 is.
