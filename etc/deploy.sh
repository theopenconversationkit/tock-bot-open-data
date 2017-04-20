#!/usr/bin/env bash
echo "branch : $TRAVIS_BRANCH"
echo "pull_request : $TRAVIS_PULL_REQUEST"
echo "tag : $TRAVIS_TAG"
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" = 'false' ] && [ "$TRAVIS_TAG" = '' ]; then
openssl aes-256-cbc -K $encrypted_56cdd536b337_key -iv $encrypted_56cdd536b337_iv -in codesigning.asc.enc -out codesigning.asc -d
gpg --fast-import etc/codesigning.asc
mvn clean package dokka:javadocJar deploy -Ddeploy --settings etc/deploy-settings.xml
fi