#!/bin/bash

# Increment a version string using Semantic Versioning (SemVer) terminology.

# Parse input parameter.
operation=$1
version=$2

# Validate operation input.
if [[ "$operation" != "major" && "$operation" != "minor" && "$operation" != "patch" ]]; then
  echo "Error: Invalid operation. Use 'major', 'minor', or 'patch'."
  echo "usage: $(basename $0) {major|minor|patch} major.minor.patch"
  exit 1
fi

# Validate version input.
if [[ -z "$version" ]]; then
  echo "Error: Version string is missing."
  echo "usage: $(basename $0) {major|minor|patch} major.minor.patch"
  exit 1
fi

# Build array from version string.
a=( ${version//./ } )

# If version string has the wrong number of members, show usage message.
if [ ${#a[@]} -ne 3 ]; then
  echo "Error: Version string must be in the format major.minor.patch."
  echo "usage: $(basename $0) {major|minor|patch} major.minor.patch"
  exit 1
fi

# Increment version numbers as requested.
case $operation in
  major)
    ((a[0]++))
    a[1]=0
    a[2]=0
    ;;
  minor)
    ((a[1]++))
    a[2]=0
    ;;
  patch)
    ((a[2]++))
    ;;
esac

# Output the incremented version.
echo "${a[0]}.${a[1]}.${a[2]}"
