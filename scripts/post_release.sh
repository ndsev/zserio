#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check global variables.
set_post_release_global_variables()
{
    exit_if_argc_ne $# 9
    local PARAM_MAVEN="$1"; shift
    local PARAM_PYPI="$1"; shift
    local PARAM_CONAN="$1"; shift
    local PARAM_EXTENSION_SAMPLE="$1"; shift
    local PARAM_TUTORIAL_CPP="$1"; shift
    local PARAM_TUTORIAL_JAVA="$1"; shift
    local PARAM_TUTORIAL_PYTHON="$1"; shift
    local PARAM_STREAMLIT="$1"; shift
    local PARAM_WEB_PAGES="$1"; shift

    if [[ ${PARAM_TUTORIAL_CPP} == 1 ]] ; then
        # CMAKE to use, defaults to "cmake" if not set
        CMAKE="${CMAKE:-cmake}"
        if [ ! -f "`which "${CMAKE}"`" ] ; then
            stderr_echo "Cannot find cmake! Set CMAKE environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_MAVEN} == 1 || ${PARAM_TUTORIAL_JAVA} == 1 ]] ; then
        # ANT to use, defaults to "ant" if not set
        ANT="${ANT:-ant}"
        if [ ! -f "`which "${ANT}"`" ] ; then
            stderr_echo "Cannot find ant! Set ANT environment variable."
            return 1
        fi

        # MVN to use, defaults to "mvn" if not set
        MVN="${MVN:-mvn}"
        if [ ! -f "`which "${MVN}"`" ] ; then
            stderr_echo "Cannot find mvn! Set MVN environment variable."
            return 1
        fi

        # check java binary
        if [ -n "${JAVA_HOME}" ] ; then
            JAVA_BIN="${JAVA_HOME}/bin/java"
        fi
        JAVA_BIN="${JAVA_BIN:-java}"
        if [ ! -f "`which "${JAVA_BIN}"`" ] ; then
            stderr_echo "Cannot find java! Set JAVA_HOME or JAVA_BIN environment variable."
            return 1
        fi
    fi

    # GIT to use, defaults to "git" if not set
    GIT="${GIT:-git}"
    if [ ! -f "`which "${GIT}"`" ] ; then
        stderr_echo "Cannot find git! Set GIT environment variable."
        return 1
    fi

    # UNZIP to use, defaults to "unzip" if not set
    UNZIP="${UNZIP:-unzip}"
    if [ ! -f "`which "${UNZIP}"`" ] ; then
        stderr_echo "Cannot find unzip! Set UNZIP environment variable."
        return 1
    fi

    if [[ ${PARAM_MAVEN} == 1 ]] ; then
        # GPG to use, defaults to "gpg" if not set
        GPG="${GPG:-gpg}"
        if [ ! -f "`which "${GPG}"`" ] ; then
            stderr_echo "Cannot find gpg! Set GPG environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_CONAN} == 1 ]] ; then
        # SHASUM to use, defaults to "shasum" if not set
        SHASUM="${SHASUM:-shasum}"
        if [ ! -f "`which "${SHASUM}"`" ] ; then
            stderr_echo "Cannot find shasum! Set SHASUM environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_PYPI} == 1 || ${PARAM_TUTORIAL_PYTHON} == 1 ]] ; then
        # python to use, defaults to "python3" if not set
        PYTHON="${PYTHON:-python3}"
        if [ ! -f "`which "${PYTHON}"`" ] ; then
            stderr_echo "Cannot find Python! Set PYTHON environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_PYPI} == 1 ]] ; then
        # Zserio PyPi directory to use, defaults to "${SCRIPT_DIR}/../../zserio-pypi" if not set
        ZSERIO_PYPI_DIR="${ZSERIO_PYPI_DIR:-${SCRIPT_DIR}/../../zserio-pypi}"
        if [ ! -d "${ZSERIO_PYPI_DIR}" ] ; then
            stderr_echo "Cannot find Zserio PyPi directory! Set ZSERIO_PYPI_DIR environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_CONAN} == 1 ]] ; then
        # Directory wiht Zserio fork of conan-center-index to use,
        # defaults to "${SCRIPT_DIR}/../../zserio-conan-center-index" if not set
        ZSERIO_CONAN_DIR="${ZSERIO_CONAN_DIR:-${SCRIPT_DIR}/../../zserio-conan-center-index}"
        if [ ! -d "${ZSERIO_CONAN_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Conan Center Index directory! Set ZSERIO_PYPI_DIR environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_EXTENSION_SAMPLE} == 1 ]] ; then
        # Zserio Extension Sample directory to use, defaults to "${SCRIPT_DIR}/../../zserio-extension-sample"
        # if not set
        ZSERIO_EXTENSION_SAMPLE_DIR="${ZSERIO_EXTENSION_SAMPLE_DIR:-${SCRIPT_DIR}/../../zserio-extension-sample}"
        if [ ! -d "${ZSERIO_EXTENSION_SAMPLE_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Extension Sample directory! Set ZSERIO_EXTENSION_SAMPLE_DIR" \
                    "environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_CPP} == 1 ]] ; then
        # Zserio Tutorial Cpp directory to use, defaults to "${SCRIPT_DIR}/../../zserio-tutorial-cpp" if not set
        ZSERIO_TUTORIAL_CPP_DIR="${ZSERIO_TUTORIAL_CPP_DIR:-${SCRIPT_DIR}/../../zserio-tutorial-cpp}"
        if [ ! -d "${ZSERIO_TUTORIAL_CPP_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Tutorial Cpp directory! Set ZSERIO_TUTORIAL_CPP_DIR" \
                    "environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_JAVA} == 1 ]] ; then
        # Zserio Tutorial Java directory to use, defaults to "${SCRIPT_DIR}/../../zserio-tutorial-java"
        # if not set
        ZSERIO_TUTORIAL_JAVA_DIR="${ZSERIO_TUTORIAL_JAVA_DIR:-${SCRIPT_DIR}/../../zserio-tutorial-java}"
        if [ ! -d "${ZSERIO_TUTORIAL_JAVA_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Tutorial Java directory! Set ZSERIO_TUTORIAL_JAVA_DIR" \
                    "environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_PYTHON} == 1 ]] ; then
        # Zserio Tutorial Python directory to use, defaults to "${SCRIPT_DIR}/../../zserio-tutorial-python"
        # if not set
        ZSERIO_TUTORIAL_PYTHON_DIR="${ZSERIO_TUTORIAL_PYTHON_DIR:-${SCRIPT_DIR}/../../zserio-tutorial-python}"
        if [ ! -d "${ZSERIO_TUTORIAL_PYTHON_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Tutorial Python directory! Set ZSERIO_TUTORIAL_PYTHON_DIR" \
                    "environment variable."
            return 1
        fi
    fi

    if [[ ${PARAM_STREAMLIT} == 1 ]] ; then
        # Zserio Streamlit directory to use, defaults to "${SCRIPT_DIR}/../../zserio-streamlit" if not set
        ZSERIO_STREAMLIT_DIR="${ZSERIO_STREAMLIT_DIR:-${SCRIPT_DIR}/../../zserio-streamlit}"
        if [ ! -d "${ZSERIO_STREAMLIT_DIR}" ] ; then
            stderr_echo "Cannot find Zserio Streamlit directory! Set ZSERIO_STREAMLIT_DIR environment variable."
            return 1
        fi
    fi

    return 0
}

# Print help on the environment variables used for this post release script.
print_release_help_env()
{
    cat << EOF
Uses the following environment variables for update after release:
    CMAKE    CMake executable to use. Default is "cmake".
    ANT      Ant executable to use. Default is "ant".
    MVN      Mvn executable to use. Default is "mvn".
    JAVA_BIN Java executable to use. Default is "java".
    GIT      Git executable to use. Default is "git".
    UNZIP    Unzip executable to use. Default is "unzip".
    GPG      Gpg executable to use. Default is "gpg".
    SHASUM   Shasum exetuable to use. Default is "shasum".
    PYTHON   Python executable to use. Default is "python3".

    GITHUB_TOKEN                GitHub token authentication to use during looking for the latest release on GitHub.
                                Default is without authentication.

    ZSERIO_PYPI_DIR             Zserio PyPi project directory. Default is "../../zserio-pypi".
    ZSERIO_CONAN_DIR            Zserio Conan Center Index dirctory. Default is "../../zserio-conan-center-index".
    ZSERIO_EXTENSION_SAMPLE_DIR Zserio Extension Sample project directory. Default is "../../zserio-extension-sample".
    ZSERIO_TUTORIAL_CPP_DIR     Zserio C++ Tutorial project directory. Default is "../../zserio-tutorial-cpp".
    ZSERIO_TUTORIAL_JAVA_DIR    Zserio Java Tutorial project directory. Default is "../../zserio-tutorial-java".
    ZSERIO_TUTORIAL_PYTHON_DIR  Zserio Python Tutorial project directory. Default is "../../zserio-tutorial-python".

    Either set these directly, or create 'scripts/build-env.sh' that sets these.
    It's sourced automatically if it exists.

EOF
}

# Upload Zserio jar together with runtime jars to Maven central repository.
upload_maven()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local ZSERIO_DEPLOY_CHECK_DIR="${ZSERIO_BUILD_DIR}/deploy/check"
    "${MVN}" dependency:copy \
            -Dmaven.repo.local="${ZSERIO_DEPLOY_CHECK_DIR}" \
            -Dartifact=io.github.ndsev:zserio:${ZSERIO_VERSION} \
            -DoutputDirectory="${ZSERIO_DEPLOY_CHECK_DIR}" 2>&1 >/dev/null
    if [ $? -ne 0 ] ; then
        echo "Uploading the latest Zserio release from GitHub to Maven central repository"
        "${ANT}" -f "${ZSERIO_PROJECT_ROOT}/build.xml" \
                -Dzserio.build_dir="${ZSERIO_BUILD_DIR}" \
                -Dzserio.deploy.snapshot_flag=no \
                -Dmaven.executable="${MVN}" \
                -Dgpg.executable="${GPG}" \
                deploy
        local ANT_RESULT=$?
        if [ ${ANT_RESULT} -ne 0 ] ; then
            stderr_echo "Ant failed with return code ${ANT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the staged repository at" \
                "https://s01.oss.sonatype.org/#stagingRepositories!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push Close button, wait and then push Release button" \
                "to copy artifacts to Maven Central!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    else
        echo $'\e[1;33m'"Zserio ${ZSERIO_VERSION} has been already deployed in Maven repository."$'\e[0m'
        echo
    fi

    return 0
}

# Upload Zserio PyPi repository after new Zserio release.
upload_pypi()
{
    exit_if_argc_ne $# 2
    local PYPI_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local GREP_RESULT=`"${PYTHON}" -m pip install zserio== 2>&1 >/dev/null | grep ${ZSERIO_VERSION}`
    if [ $? -ne 0 -o -z "${GREP_RESULT}" ] ; then
        "${PYPI_DIR}/scripts/build.sh" -p
        if [ $? -ne 0 ] ; then
            stderr_echo "Failure to build Zserio PyPi!"
            return 1
        fi

        "${PYPI_DIR}/scripts/test.sh"
        if [ $? -ne 0 ] ; then
            stderr_echo "Failure to test Zserio PyPi!"
            return 1
        fi

        "${PYPI_DIR}/scripts/upload.sh"
        if [ $? -ne 0 ] ; then
            stderr_echo "Failure to upload Zserio PyPi!"
            return 1
        fi
        echo
    else
        echo $'\e[1;33m'"Zserio ${ZSERIO_VERSION} has been already deployed in PyPi repository."$'\e[0m'
        echo
    fi

    return 0
}

# Update Zserio fork of conan-center-index after new Zserio release
update_conan()
{
    exit_if_argc_ne $# 2
    local CONAN_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local RELEASE_BRANCH="zserio-${ZSERIO_VERSION}"
    local GIT_MESSAGE="zserio: Add new Zserio release ${ZSERIO_VERSION}"
    local GREP_RESULT=`"${GIT}" -C "${CONAN_DIR}" log ${RELEASE_BRANCH} | grep "${GIT_MESSAGE}"`
    if [ $? -ne 0 -o -z "${GREP_RESULT}" ] ; then
        echo "Adding version ${ZSERIO_VERSION} to Zserio Conan Center Index."

        local DOWNLOAD_DIR="${CONAN_DIR}/download/"
        mkdir -p "${DOWNLOAD_DIR}"

        echo -ne "Downloading release assets and calculating sha256 checksums..."

        local ZSERIO_RUNTIME_LIBS_ZIP_NAME="zserio-${ZSERIO_VERSION}-runtime-libs.zip"
        get_zserio_runtime_libs "${ZSERIO_VERSION}" "${DOWNLOAD_DIR}" "${ZSERIO_RUNTIME_LIBS_ZIP_NAME}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        local ZSERIO_RUNTIME_LIBS_SHA256=$(${SHASUM} -a 256 "${DOWNLOAD_DIR}/${ZSERIO_RUNTIME_LIBS_ZIP_NAME}" | cut -d' ' -f1)

        local ZSERIO_BIN_ZIP_NAME=zserio-${ZSERIO_VERSION}-bin.zip
        get_zserio_bin "${ZSERIO_VERSION}" "${DOWNLOAD_DIR}" "${ZSERIO_BIN_ZIP_NAME}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        local ZSERIO_BIN_SHA256=$(${SHASUM} -a 256 "${DOWNLOAD_DIR}/${ZSERIO_BIN_ZIP_NAME}" | cut -d' ' -f1)

        local ZSERIO_LICENSE_NAME="LICENSE-${ZSERIO_VERSION}"
        get_zserio_license "${ZSERIO_VERSION}" "${DOWNLOAD_DIR}" "${ZSERIO_LICENSE_NAME}"
        local ZSERIO_LICENSE_SHA256=$(${SHASUM} -a 256 "${DOWNLOAD_DIR}/${ZSERIO_LICENSE_NAME}" | cut -d' ' -f1)

        echo "Done"

        echo "Syncing master with upstream."
        "${GIT}" -C "${CONAN_DIR}" fetch upstream
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi

        echo "Preparing release branch '${RELEASE_BRANCH}'."
        "${GIT}" -C "${CONAN_DIR}" branch ${RELEASE_BRANCH} upstream/master
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}"
            return 1
        fi

        echo "Switching to the release branch '${RELEASE_BRANCH}'."
        "${GIT}" -C "${CONAN_DIR}" checkout ${RELEASE_BRANCH}
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}"
            return 1
        fi

        local CONFIG_YML=${ZSERIO_CONAN_DIR}/recipes/zserio/config.yml
        local OLD_VERSIONS=$(tail "${CONFIG_YML}" -n +2)
        cat > ${CONFIG_YML} << EOF
versions:
  "${ZSERIO_VERSION}":
    folder: all
${OLD_VERSIONS}
EOF

        local CONANDATA_YML=${ZSERIO_CONAN_DIR}/recipes/zserio/all/conandata.yml
        local OLD_SOURCES=$(tail "${CONANDATA_YML}" -n +2)
        local ZSERIO_RUNTIME_LIBS_URL
        get_zserio_runtime_libs_url "${ZSERIO_VERSION}" ZSERIO_RUNTIME_LIBS_URL
        local ZSERIO_BIN_URL
        get_zserio_bin_url "${ZSERIO_VERSION}" ZSERIO_BIN_URL
        local ZSERIO_LICENSE_URL
        get_zserio_license_url "${ZSERIO_VERSION}" ZSERIO_LICENSE_URL
        cat > ${CONANDATA_YML} << EOF
sources:
  "${ZSERIO_VERSION}":
    runtime:
      url: "${ZSERIO_RUNTIME_LIBS_URL}"
      sha256: "${ZSERIO_RUNTIME_LIBS_SHA256}"
    compiler:
      url: "${ZSERIO_BIN_URL}"
      sha256: "${ZSERIO_BIN_SHA256}"
    license:
      url: "${ZSERIO_LICENSE_URL}"
      sha256: "${ZSERIO_LICENSE_SHA256}"
${OLD_SOURCES}
EOF

        echo "Committing update of Zserio Conan Center Index."
        "${GIT}" -C "${ZSERIO_CONAN_DIR}" commit -a -m "${GIT_MESSAGE}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}"
            return 1
        fi

        echo $'\e[1;33m'"Don't forget to check the Zserio Conan Center Index repository!"$'\e[0m'
        echo $'\e[1;33m'"Run: 'conan create zserio/recipes/all/conanfile.py --version ${ZSERIO_VERSION}'"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin and make a pull request to upstream!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    else
        echo $'\e[1;33m'"Zserio fork of Conan center index already up to date."$'\e[0m'
        echo
    fi

    return 0
}

# Update Zserio Extension Sample repository after new Zserio release.
update_extension_sample()
{
    exit_if_argc_ne $# 2
    local EXTENSION_SAMPLE_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local EXTENSION_FILE="${EXTENSION_SAMPLE_DIR}/src/zserio/extension/sample/SampleExtension.java"
    echo -ne "Updating version to ${ZSERIO_VERSION} in Zserio Extension Sample..."
    sed -i -e 's/[2-9]\+\.[0-9]\+\.[0-9]\+\(\-[A-Za-z0-9]\+\)\?/'"${ZSERIO_VERSION}"'/' "${EXTENSION_FILE}"
    local SED_RESULT=$?
    if [ ${SED_RESULT} -ne 0 ] ; then
        stderr_echo "Sed failed with return code ${SED_RESULT}!"
        return 1
    fi
    echo "Done"
    echo

    "${GIT}" -C "${EXTENSION_SAMPLE_DIR}" diff --exit-code > /dev/null
    if [ $? -eq 0 ] ; then
        echo $'\e[1;33m'"Zserio Extension Sample already up to date."$'\e[0m'
    else
        echo "Committing update of Zserio Extension Sample."
        "${GIT}" -C "${EXTENSION_SAMPLE_DIR}" commit -a \
                -m "Change expected zserio core version to ${ZSERIO_VERSION}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the Zserio Extension Sample repository!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    fi
    echo

    return 0
}

# Update Zserio Tutorial Cpp repository after new Zserio release.
update_tutorial_cpp()
{
    exit_if_argc_ne $# 2
    local TUTORIAL_CPP_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    echo "Updating generated sources in Zserio Tutorial Cpp."
    echo
    local TUTORIAL_CPP_BUILD_DIR="${TUTORIAL_CPP_DIR}/build"
    rm -rf "${TUTORIAL_CPP_BUILD_DIR}"
    mkdir -p "${TUTORIAL_CPP_BUILD_DIR}"
    pushd "${TUTORIAL_CPP_BUILD_DIR}" > /dev/null
    "${CMAKE}" -DREGENERATE_CPP_SOURCES=ON ..
    local CMAKE_RESULT=$?
    popd > /dev/null
    if [ ${CMAKE_RESULT} -ne 0 ] ; then
        stderr_echo "CMake failed with return code ${CMAKE_RESULT}!"
        return 1
    fi
    echo

    "${GIT}" -C "${TUTORIAL_CPP_DIR}" diff --exit-code > /dev/null
    if [ $? -eq 0 ] ; then
        echo $'\e[1;33m'"Zserio Tutorial Cpp already up to date."$'\e[0m'
    else
        echo "Committing update of Zserio Tutorial Cpp."
        "${GIT}" -C "${TUTORIAL_CPP_DIR}" commit -a -m "Update generated sources to version ${ZSERIO_VERSION}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the Zserio Tutorial Cpp repository!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    fi
    echo

    return 0
}

# Update Zserio Tutorial Java repository after new Zserio release.
update_tutorial_java()
{
    exit_if_argc_ne $# 2
    local TUTORIAL_JAVA_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    echo "Updating generated sources in Zserio Tutorial Java."
    echo
    local TUTORIAL_JAVA_BUILD_DIR="${TUTORIAL_JAVA_DIR}/build"
    ${MVN} dependency:copy -Dmaven.repo.local="${TUTORIAL_JAVA_BUILD_DIR}/download" \
            -Dartifact=io.github.ndsev:zserio:LATEST \
            -DoutputDirectory="${TUTORIAL_JAVA_BUILD_DIR}" \
            -Dmdep.stripVersion=true
    local MVN_RESULT=$?
    if [ ${MVN_RESULT} -ne 0 ] ; then
        stderr_echo "Maven download failed with return code ${MVN_RESULT}!"
        return 1
    fi
    "${JAVA_BIN}" -jar "${TUTORIAL_JAVA_BUILD_DIR}/zserio.jar" -src "${TUTORIAL_JAVA_DIR}" tutorial.zs \
            -java "${TUTORIAL_JAVA_DIR}/src"
    local ZSERIO_RESULT=$?
    if [ ${ZSERIO_RESULT} -ne 0 ] ; then
        stderr_echo "Zserio compilation failed with return code ${ZSERIO_RESULT}!"
        return 1
    fi
    echo

    "${GIT}" -C "${TUTORIAL_JAVA_DIR}" diff --exit-code > /dev/null
    if [ $? -eq 0 ] ; then
        echo $'\e[1;33m'"Zserio Tutorial Java already up to date."$'\e[0m'
    else
        echo "Committing update of Zserio Tutorial Java."
        "${GIT}" -C "${TUTORIAL_JAVA_DIR}" commit -a -m "Update generated sources to version ${ZSERIO_VERSION}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the Zserio Tutorial Java repository!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    fi
    echo

    return 0
}

# Update Zserio Tutorial Python repository after new Zserio release.
update_tutorial_python()
{
    exit_if_argc_ne $# 2
    local TUTORIAL_PYTHON_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    echo "Updating generated sources in Zserio Tutorial Python."
    echo
    local TUTORIAL_PYTHON_BUILD_DIR="${TUTORIAL_PYTHON_DIR}/build"
    rm -rf "${TUTORIAL_PYTHON_BUILD_DIR}"
    mkdir -p "${TUTORIAL_PYTHON_BUILD_DIR}"

    "${PYTHON}" -m virtualenv "${TUTORIAL_PYTHON_BUILD_DIR}"
    local PYTHON_RESULT=$?
    if [ ${PYTHON_RESULT} -ne 0 ] ; then
        stderr_echo "Python failed with return code ${PYTHON_RESULT}!"
        return 1
    fi
    if [ -f "${TUTORIAL_PYTHON_BUILD_DIR}/bin/activate" ] ; then
        . "${TUTORIAL_PYTHON_BUILD_DIR}/bin/activate"
    else
        . "${TUTORIAL_PYTHON_BUILD_DIR}/Scripts/activate"
    fi
    pip install zserio=="${ZSERIO_VERSION}"
    zserio -src "${TUTORIAL_PYTHON_DIR}" tutorial.zs -python "${TUTORIAL_PYTHON_DIR}/src"
    local ZSERIO_RESULT=$?
    if [ ${ZSERIO_RESULT} -ne 0 ] ; then
        stderr_echo "Zserio failed with return code ${ZSERIO_RESULT}!"
        return 1
    fi
    echo

    "${GIT}" -C "${TUTORIAL_PYTHON_DIR}" diff --exit-code > /dev/null
    if [ $? -eq 0 ] ; then
        echo $'\e[1;33m'"Zserio Tutorial Python already up to date."$'\e[0m'
    else
        echo "Committing update of Zserio Tutorial Python."
        "${GIT}" -C "${TUTORIAL_PYTHON_DIR}" commit -a -m "Update generated sources to version ${ZSERIO_VERSION}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the Zserio Tutorial Python repository!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    fi
    echo

    return 0
}

# Update Zserio Streamlit repository after new Zserio release.
update_streamlit()
{
    exit_if_argc_ne $# 2
    local STREAMLIT_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local REQUIREMENTS_FILE="${STREAMLIT_DIR}/requirements.txt"
    echo -ne "Updating version to ${ZSERIO_VERSION} in Zserio Streamlit..."
    sed -i -e 's/zserio==[2-9]\+\.[0-9]\+\.[0-9]\+\(\-[A-Za-z0-9]\+\)\?/'"zserio==${ZSERIO_VERSION}"'/' \
            "${REQUIREMENTS_FILE}"
    local SED_RESULT=$?
    if [ ${SED_RESULT} -ne 0 ] ; then
        stderr_echo "Sed failed with return code ${SED_RESULT}!"
        return 1
    fi
    echo "Done"
    echo

    "${GIT}" -C "${STREAMLIT_DIR}" diff --exit-code > /dev/null
    if [ $? -eq 0 ] ; then
        echo $'\e[1;33m'"Zserio Streamlit already up to date."$'\e[0m'
    else
        echo "Committing update of Zserio Streamlit."
        "${GIT}" -C "${STREAMLIT_DIR}" commit -a \
                -m "Change required zserio version to ${ZSERIO_VERSION}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo $'\e[1;33m'"Don't forget to check the Zserio Streamlit repository!"$'\e[0m'
        echo $'\e[1;33m'"If it is ok, push changes to origin!"$'\e[0m'
        read -n 1 -s -r -p "Press any key to continue..."
        echo
    fi
    echo

    return 0
}

# Update Zserio Web Pages branch after new Zserio release.
update_web_pages()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local GIT_MESSAGE="Add generated v${ZSERIO_VERSION} runtime documentation"
    local GREP_RESULT=`"${GIT}" log web-pages | grep "${GIT_MESSAGE}"`
    if [ $? -ne 0 -o -z "${GREP_RESULT}" ] ; then
        echo "Merging master into Zserio Web Pages branch."

        "${GIT}" checkout web-pages
        if [ $? -ne 0 ] ; then
            "${GIT}" checkout -b web-pages
            local GIT_RESULT=$?
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        "${GIT}" fetch --tags
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        "${GIT}" rebase v${ZSERIO_VERSION}
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo

        echo "Updating sources in Zserio Web Pages branch."
        echo
        local WEB_PAGES_BUILD_DIR="${ZSERIO_BUILD_DIR}/web_pages"
        rm -rf "${WEB_PAGES_BUILD_DIR}"
        mkdir -p "${WEB_PAGES_BUILD_DIR}"

        echo -ne "Downloading Zserio runtime libraries from GitHub..."
        get_zserio_runtime_libs ${ZSERIO_VERSION} "${WEB_PAGES_BUILD_DIR}" "runtime-libs.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo "Done"

        echo -ne "Unzipping Zserio runtime libraries..."
        "${UNZIP}" -q "${WEB_PAGES_BUILD_DIR}"/runtime-libs.zip -d "${WEB_PAGES_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            stderr_echo "Cannot unzip zserio runtime libraries to ${WEB_PAGES_BUILD_DIR}!"
            return 1
        fi
        mkdir -p "${WEB_PAGES_BUILD_DIR}"/runtime_libs/java/zserio_doc
        "${UNZIP}" -q "${WEB_PAGES_BUILD_DIR}"/runtime_libs/java/zserio_runtime_javadocs.jar \
                -d "${WEB_PAGES_BUILD_DIR}"/runtime_libs/java/zserio_doc -x META-INF/*
        if [ $? -ne 0 ] ; then
            stderr_echo "Cannot unzip zserio runtime javadocs jar!"
            return 1
        fi
        echo "Done"

        echo -ne "Copying Zserio runtime libraries version ${ZSERIO_VERSION}..."
        local DEST_RUNTIME_DIR="${ZSERIO_PROJECT_ROOT}/doc/runtime/${ZSERIO_VERSION}"
        mkdir -p "${DEST_RUNTIME_DIR}"/cpp
        cp -r "${WEB_PAGES_BUILD_DIR}"/runtime_libs/cpp/zserio_doc/* "${DEST_RUNTIME_DIR}"/cpp
        if [ $? -ne 0 ] ; then
            return 1
        fi
        mkdir -p "${DEST_RUNTIME_DIR}"/java
        cp -r "${WEB_PAGES_BUILD_DIR}"/runtime_libs/java/zserio_doc/* "${DEST_RUNTIME_DIR}"/java
        if [ $? -ne 0 ] ; then
            return 1
        fi
        mkdir -p "${DEST_RUNTIME_DIR}"/python
        cp -r "${WEB_PAGES_BUILD_DIR}"/runtime_libs/python/zserio_doc/* "${DEST_RUNTIME_DIR}"/python
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo "Done"

        echo -ne "Creating Zserio runtime library GitHub badges..."
        create_github_badge_jsons "${DEST_RUNTIME_DIR}" "${ZSERIO_VERSION}"
        echo "Done"

        echo -ne "Copying Zserio runtime libraries latest version..."
        local DEST_LATEST_DIR="${ZSERIO_PROJECT_ROOT}/doc/runtime/latest"
        rm -rf "${DEST_LATEST_DIR}"
        mkdir -p "${DEST_LATEST_DIR}"
        cp -r "${DEST_RUNTIME_DIR}"/* "${DEST_LATEST_DIR}"
        echo "Done"

        # This is necessary because Jekyll ignores Python runtime doc directories that start with underscores.
        echo -ne "Creating Jekyll configuration file..."
        create_jekyll_config_file "${ZSERIO_PROJECT_ROOT}"
        echo "Done"

        echo
        echo "Committing changes to Zserio Web Pages branch."
        "${GIT}" -C "${ZSERIO_PROJECT_ROOT}" add -A
        "${GIT}" -C "${ZSERIO_PROJECT_ROOT}" commit -a -m "${GIT_MESSAGE}"
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        "${GIT}" -C "${ZSERIO_PROJECT_ROOT}" push --set-upstream origin web-pages
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        "${GIT}" -C "${ZSERIO_PROJECT_ROOT}" checkout master
        local GIT_RESULT=$?
        if [ ${GIT_RESULT} -ne 0 ] ; then
            stderr_echo "Git failed with return code ${GIT_RESULT}!"
            return 1
        fi
        echo
    else
        echo $'\e[1;33m'"Zserio Web Pages already up to date."$'\e[0m'
        echo
    fi

    return 0
}

# Create JSON configuration files for all GitHub badges
create_github_badge_jsons()
{
    exit_if_argc_ne $# 2
    local ZSERIO_RUNTIME_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local CLANG_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/cpp/coverage/clang
    local CLANG_LINES_COVERAGE=`cat "${CLANG_COVERAGE_DIR}"/coverage_report.txt | grep TOTAL | \
            tr -s ' ' | cut -d' ' -f 10`
    create_github_badge_json "${CLANG_COVERAGE_DIR}"/coverage_github_badge.json \
            "C++ clang runtime ${ZSERIO_VERSION} coverage" "${CLANG_LINES_COVERAGE}"

    local GCC_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/cpp/coverage/gcc
    local GCC_LINES_COVERAGE=`cat "${GCC_COVERAGE_DIR}"/coverage_report.txt | grep lines: | \
            tr -s ' ' | cut -d' ' -f 2`
    create_github_badge_json "${GCC_COVERAGE_DIR}"/coverage_github_badge.json \
            "C++ gcc runtime ${ZSERIO_VERSION} coverage" "${GCC_LINES_COVERAGE}"

    local JAVA_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/java/coverage
    local JAVA_COVERAGE_REPORT=`cat "${JAVA_COVERAGE_DIR}"/jacoco_report.xml`
    local JAVA_LINES_MISSED=`echo ${JAVA_COVERAGE_REPORT##*INSTRUCTION} | cut -d'"' -f3`
    local JAVA_LINES_COVERED=`echo ${JAVA_COVERAGE_REPORT##*INSTRUCTION} | cut -d'"' -f5`
    local JAVA_LINES_VALID=$((${JAVA_LINES_COVERED} - ${JAVA_LINES_MISSED}))
    local JAVA_LINES_COVERAGE=$((10000 * ${JAVA_LINES_VALID} / ${JAVA_LINES_COVERED}))
    create_github_badge_json "${JAVA_COVERAGE_DIR}"/coverage_github_badge.json \
            "Java runtime ${ZSERIO_VERSION} coverage" \
            "${JAVA_LINES_COVERAGE:0:-2}.${JAVA_LINES_COVERAGE: -2}%"

    local PYTHON_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/python/coverage
    local PYTHON_LINES_VALID=`cat "${PYTHON_COVERAGE_DIR}"/coverage_report.xml | grep lines-covered | \
            cut -d' ' -f 4 | cut -d= -f2 | tr -d \"`
    local PYTHON_LINES_COVERED=`cat "${PYTHON_COVERAGE_DIR}"/coverage_report.xml | grep lines-covered | \
            cut -d' ' -f 5 | cut -d= -f2 | tr -d \"`
    local PYTHON_LINES_COVERAGE=$((10000 * ${PYTHON_LINES_VALID} / ${PYTHON_LINES_COVERED}))
    create_github_badge_json "${PYTHON_COVERAGE_DIR}"/coverage_github_badge.json \
            "Python runtime ${ZSERIO_VERSION} coverage" \
            "${PYTHON_LINES_COVERAGE:0:-2}.${PYTHON_LINES_COVERAGE: -2}%"
}

# Create JSON configuration file for GitHub badge
create_github_badge_json()
{
    exit_if_argc_ne $# 3
    local BADGE_JSON_FILE="$1"; shift
    local BADGE_LABEL="$1"; shift
    local BADGE_MESSAGE="$1"; shift

    cat > "${BADGE_JSON_FILE}" << EOF
{
    "schemaVersion": 1,
    "label": "${BADGE_LABEL}",
    "message": "${BADGE_MESSAGE}",
    "color": "green"
}
EOF
}

# Create Jekyll configuration file
create_jekyll_config_file()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"; shift

    echo "theme: jekyll-theme-slate" > "${ZSERIO_PROJECT_ROOT}"/_config.yml
    echo "exclude: 3rdparty" >> "${ZSERIO_PROJECT_ROOT}"/_config.yml
    echo "include:" >> "${ZSERIO_PROJECT_ROOT}"/_config.yml
    echo "  - _images" >> "${ZSERIO_PROJECT_ROOT}"/_config.yml
    echo "  - _modules" >> "${ZSERIO_PROJECT_ROOT}"/_config.yml
    echo "  - _static" >> "${ZSERIO_PROJECT_ROOT}"/_config.yml
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Update all Zserio dependent repositories after Zserio release.

Usage:
    $0 [-h] [-e] [-o <dir>] [repository]

Arguments:
    -h, --help       Show this help.
    -e, --help-env   Show help for enviroment variables.
    -o <dir>, --output-directory <dir>
                     Output directory where build and distr are located.

Repository can be empty for all repositories or arbitrary combination of
    maven            Upload Zserio jar together with runtime jars to Maven central repository
    pypi             Upload Zserio PyPi repository after new Zserio release
    conan            Update Zserio fork of conan-center-index after new Zserio release
    extension_sample Update Zserio Extension Sample repository after new Zserio release
    tutorial_cpp     Update Zserio Tutorial Cpp repository after new Zserio release
    tutorial_java    Update Zserio Tutorial Java repository after new Zserio release
    tutorial_python  Update Zserio Tutorial Python repository after new Zserio release
    streamlit        Update Zserio Streamlit repository after new Zserio release
    web_pages        Update Zserio Web Pages branch after new Zserio release

Examples:
    $0

EOF
}

# Parse all command line arguments.
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
# 3 - Environment help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    local NUM_OF_ARGS=10
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local PARAM_OUT_DIR_OUT="$1"; shift
    local PARAM_MAVEN_OUT="$1"; shift
    local PARAM_PYPI_OUT="$1"; shift
    local PARAM_CONAN_OUT="$1"; shift
    local PARAM_EXTENSION_SAMPLE_OUT="$1"; shift
    local PARAM_TUTORIAL_CPP_OUT="$1"; shift
    local PARAM_TUTORIAL_JAVA_OUT="$1"; shift
    local PARAM_TUTORIAL_PYTHON_OUT="$1"; shift
    local PARAM_STREAMLIT_OUT="$1"; shift
    local PARAM_WEB_PAGES_OUT="$1"; shift

    eval ${PARAM_MAVEN_OUT}=0
    eval ${PARAM_PYPI_OUT}=0
    eval ${PARAM_CONAN_OUT}=0
    eval ${PARAM_EXTENSION_SAMPLE_OUT}=0
    eval ${PARAM_TUTORIAL_CPP_OUT}=0
    eval ${PARAM_TUTORIAL_JAVA_OUT}=0
    eval ${PARAM_TUTORIAL_PYTHON_OUT}=0
    eval ${PARAM_STREAMLIT_OUT}=0
    eval ${PARAM_WEB_PAGES_OUT}=0

    local NUM_PARAMS=0
    local ARG="$1"
    while [ $# -ne 0 ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-o" | "--output-directory")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing output directory!"
                    echo
                    return 1
                fi
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-"*)
                stderr_echo "Invalid switch '${ARG}'!"
                echo
                return 1
                ;;

            "maven")
                eval ${PARAM_MAVEN_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "pypi")
                eval ${PARAM_PYPI_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "conan")
                eval ${PARAM_CONAN_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "extension_sample")
                eval ${PARAM_EXTENSION_SAMPLE_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "tutorial_cpp")
                eval ${PARAM_TUTORIAL_CPP_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "tutorial_java")
                eval ${PARAM_TUTORIAL_JAVA_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "tutorial_python")
                eval ${PARAM_TUTORIAL_PYTHON_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "streamlit")
                eval ${PARAM_STREAMLIT_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            "web_pages")
                eval ${PARAM_WEB_PAGES_OUT}=1
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift 1
                ;;

            *)
                stderr_echo "Invalid parameter '${ARG}'!"
                echo
                return 1
                ;;
        esac
        ARG="$1"
    done

    if [[ ${NUM_PARAMS} == 0 ]] ; then
        eval ${PARAM_MAVEN_OUT}=1
        eval ${PARAM_PYPI_OUT}=1
        eval ${PARAM_CONAN_OUT}=1
        eval ${PARAM_EXTENSION_SAMPLE_OUT}=1
        eval ${PARAM_TUTORIAL_CPP_OUT}=1
        eval ${PARAM_TUTORIAL_JAVA_OUT}=1
        eval ${PARAM_TUTORIAL_PYTHON_OUT}=1
        eval ${PARAM_STREAMLIT_OUT}=1
        eval ${PARAM_WEB_PAGES_OUT}=1
    fi

    return 0
}

# Main entry of the script to make Zserio release.
main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."

    # parse command line arguments
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_MAVEN
    local PARAM_PYPI
    local PARAM_CONAN
    local PARAM_EXTENSION_SAMPLE
    local PARAM_TUTORIAL_CPP
    local PARAM_TUTORIAL_JAVA
    local PARAM_TUTORIAL_PYTHON
    local PARAM_STREAMLIT
    local PARAM_WEB_PAGES
    parse_arguments PARAM_OUT_DIR PARAM_MAVEN PARAM_PYPI PARAM_CONAN PARAM_EXTENSION_SAMPLE PARAM_TUTORIAL_CPP \
            PARAM_TUTORIAL_JAVA PARAM_TUTORIAL_PYTHON PARAM_STREAMLIT PARAM_WEB_PAGES "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_release_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    # get the output directory (the absolute path is necessary)
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    # set global variables
    set_post_release_global_variables ${PARAM_MAVEN} ${PARAM_PYPI} ${PARAM_CONAN} ${PARAM_EXTENSION_SAMPLE} \
            ${PARAM_TUTORIAL_CPP} ${PARAM_TUTORIAL_JAVA} ${PARAM_TUTORIAL_PYTHON} ${PARAM_STREAMLIT} \
            ${PARAM_WEB_PAGES}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get latest zserio version
    local ZSERIO_VERSION
    get_latest_zserio_version ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    echo "Updating dependent repositories after new Zserio release ${ZSERIO_VERSION}."
    echo
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"

    if [[ ${PARAM_MAVEN} == 1 ]] ; then
        upload_maven "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_PYPI} == 1 ]] ; then
        upload_pypi "${ZSERIO_PYPI_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_CONAN} == 1 ]] ; then
        update_conan "${ZSERIO_CONAN_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_EXTENSION_SAMPLE} == 1 ]] ; then
        update_extension_sample "${ZSERIO_EXTENSION_SAMPLE_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_CPP} == 1 ]] ; then
        update_tutorial_cpp "${ZSERIO_TUTORIAL_CPP_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_JAVA} == 1 ]] ; then
        update_tutorial_java "${ZSERIO_TUTORIAL_JAVA_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_TUTORIAL_PYTHON} == 1 ]] ; then
        update_tutorial_python "${ZSERIO_TUTORIAL_PYTHON_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_STREAMLIT} == 1 ]] ; then
        update_streamlit "${ZSERIO_STREAMLIT_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_WEB_PAGES} == 1 ]] ; then
        update_web_pages "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    return 0
}

main "$@"
