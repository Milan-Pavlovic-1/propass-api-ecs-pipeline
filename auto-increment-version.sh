#!/bin/bash

#get highest tag number
VERSION=`git describe --abbrev=0 --tags`

echo "${VERSION}"

# Change comma (,) to whitespace and add under braces
VERSION_BITS=(`echo $VERSION | tr '.-' ' '`)


#get number parts and increase last one by 1
VNUM1=${VERSION_BITS[0]}
#echo "${VNUM1}"

VNUM2=${VERSION_BITS[1]}
#echo "${VNUM2}"

VNUM3=${VERSION_BITS[2]}
echo "${VNUM3}"

VNUM4=${VERSION_BITS[3]}
echo "${VNUM4}"



# Check for #major or #minor in commit message and increment the relevant version number
MAJOR=`git log --format=%B -n 1 HEAD | grep '#major'`
MINOR=`git log --format=%B -n 1 HEAD | grep '#minor'`

if [ "$MAJOR" ]; then
    echo "Update major version"
    VNUM1=$((VNUM1+1))
    VNUM2=0
    VNUM3=0
elif [ "$MINOR" ]; then
    echo "Update minor version"
    VNUM2=$((VNUM2+1))
    VNUM3=0
else
    echo "Update patch version"
    VNUM4=$((VNUM4+1)) 
fi
echo "${VNUM4}"



#create new tag
NEW_TAG="$VNUM1.$VNUM2.$VNUM3-$VNUM4"
echo "${NEW_TAG}"



echo "Updating $VERSION to $NEW_TAG"


#get current hash and see if it already has a tag
GIT_COMMIT=`git rev-parse HEAD`
NEEDS_TAG=`git describe --contains $GIT_COMMIT`

docker tag propass-api:latest 906330555725.dkr.ecr.eu-central-1.amazonaws.com/propass-api:$NEW_TAG
docker tag propass-api:latest 906330555725.dkr.ecr.eu-central-1.amazonaws.com/propass-api:$NEW_TAG

#only tag if no tag already (would be better if the git describe command above could have a silent option)
if [ -z "$NEEDS_TAG" ]; then
    echo "Tagged with $NEW_TAG (Ignoring fatal:cannot describe - this means commit is untagged)"
    git tag $NEW_TAG
    git push --tags
else
    echo "Already a tag on this commit"
    sleep 10s
fi