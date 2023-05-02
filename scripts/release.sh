#!/bin/sh

# check if the npm user is configured.
npmuser=$(npm whoami)
npmlisted=$(npm org ls appcues | grep -q "$npmuser")
if [[ $? != 0 ]]
then
	echo "Your npm user $npmuser is not currently part of the Appcues npm organization."
	exit 1
fi

# check if `gh` tool is installed.
if ! command -v gh &> /dev/null
then
	echo "Github CLI tool is required, but could not be found."
	echo "Install it via: $ brew install gh"
	exit 1
fi

# check if `gh` tool has auth access.
# command will return non-zero if not auth'd.
authd=$(gh auth status -t)
if [[ $? != 0 ]]
then
	echo "ex: $ gh auth login"
	exit 1
fi

# check that we're on the `main` branch
branch=$(git rev-parse --abbrev-ref HEAD)
if [ $branch != 'main' ]
then
	echo "The 'main' must be the current branch to make a release."
	echo "You are currently on: $branch"
	exit 1
fi

if [ -n "$(git status --porcelain)" ]
then
  echo "There are uncommited changes. Please commit and create a pull request or stash them.";
  exit 1
fi

# remove quotes
version=$(npm pkg get version | sed 's/"//g')

echo "Appcues module current version: $version"

# no args, so give usage.
if [ $# -eq 0 ]
then
	echo "Release automation script"
	echo ""
	echo "Usage: $ ./release.sh <version>"
	echo "   ex: $ ./release.sh \"1.0.2\""
	exit 0
fi

newVersion="${1}"
echo "Preparing to release $newVersion..."

versionComparison=$(./scripts/semver.sh $newVersion $version)

if [ $versionComparison != '1' ]
then
	echo "New version must be greater than previous version ($version)."
	exit 1
fi

read -r -p "Are you sure you want to release $newVersion? [y/N] " response
case "$response" in
	[yY][eE][sS]|[yY])
		;;
	*)
		exit 1
		;;
esac

# update package.json version.
npm version $newVersion -git-tag-version false
# update example/package.json version.
(cd ./example && npm version $newVersion -git-tag-version false)

# commit the version change.
git commit -am "🗃 Update version to $newVersion"
git push

# get the commits since the last release, filtering ones that aren't relevant.
changelog=$(git log --pretty=format:"- [%as] %s (%h)" $(git describe --tags --abbrev=0 @^)..@ --abbrev=7 | sed '/[🔧🎬📸✅💡📝]/d')
tempFile=$(mktemp)
# write changelog to temp file.
echo "$changelog" >> $tempFile

# gh release will make both the tag and the release itself.
gh release create $newVersion -F $tempFile -t $newVersion

# remove the tempfile.
rm $tempFile

npm publish
