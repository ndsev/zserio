#!/usr/bin/env bash

# Maps changed paths to the CI legs they can affect.
#
# Reads changed file paths (one per line, relative to the repository root) from stdin and prints
# one "<leg>=true|false" line per leg: zserio, cpp, java, python, doc, xml. The zserio leg builds
# the compiler every other leg needs, so it runs whenever any other leg runs.
#
# Unknown paths run all legs, so a path missing here costs CI time, never coverage.

LEGS=(cpp java python doc xml)

declare -A SELECTED
for LEG in "${LEGS[@]}" ; do
    SELECTED[${LEG}]=0
done

select_all()
{
    local LEG
    for LEG in "${LEGS[@]}" ; do
        SELECTED[${LEG}]=1
    done
}

select_path()
{
    local FILE_PATH="$1"; shift

    case "${FILE_PATH}" in
        # shared by all legs, listed first so that e.g. .github/*.md does not count as documentation
        compiler/core/*|scripts/*|cmake/*|.github/*)
            select_all
            ;;

        # documentation only, no build leg
        doc/*|*.md|LICENSE|CNAME)
            ;;

        # the Python leg builds the C++ runtime for its C++ extension (python_cpp)
        compiler/extensions/cpp/*|3rdparty/cpp/*)
            SELECTED[cpp]=1
            SELECTED[python]=1
            ;;
        compiler/extensions/java/*)
            SELECTED[java]=1
            ;;
        compiler/extensions/python/*)
            SELECTED[python]=1
            ;;
        compiler/extensions/doc/*)
            SELECTED[doc]=1
            ;;
        compiler/extensions/xml/*)
            SELECTED[xml]=1
            ;;

        # language specific test sources, schemas shared by all languages fall through to all legs
        test/*/cpp/*)
            SELECTED[cpp]=1
            ;;
        test/*/java/*)
            SELECTED[java]=1
            ;;
        test/*/python/*)
            SELECTED[python]=1
            ;;

        # test schemas, build files and everything unknown
        *)
            select_all
            ;;
    esac
}

main()
{
    local FILE_PATH
    while IFS= read -r FILE_PATH || [[ -n "${FILE_PATH}" ]] ; do
        if [[ -n "${FILE_PATH}" ]] ; then
            select_path "${FILE_PATH}"
        fi
    done

    local ANY=0
    local LEG
    for LEG in "${LEGS[@]}" ; do
        if [[ ${SELECTED[${LEG}]} -eq 1 ]] ; then
            ANY=1
        fi
    done

    echo "zserio=$( [[ ${ANY} -eq 1 ]] && echo true || echo false )"
    for LEG in "${LEGS[@]}" ; do
        echo "${LEG}=$( [[ ${SELECTED[${LEG}]} -eq 1 ]] && echo true || echo false )"
    done
}

main "$@"
