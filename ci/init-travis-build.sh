#!/bin/bash

echo -e "Branch: ${TRAVIS_BRANCH}"
echo -e "************************************"
echo -e "Pull request branch: ${TRAVIS_PULL_REQUEST_BRANCH}"
echo -e "Pull Request: ${TRAVIS_PULL_REQUEST}"
echo -e "Pull Request SHA: ${TRAVIS_PULL_REQUEST_SHA}"
echo -e "************************************"
echo -e "Build directory: ${TRAVIS_BUILD_DIR}"
echo -e "Build id: ${TRAVIS_BUILD_ID}"
echo -e "Build type: ${TRAVIS_EVENT_TYPE}"
echo -e "Build number: ${TRAVIS_BUILD_NUMBER}"
echo -e "************************************"
echo -e "Job id: ${TRAVIS_JOB_ID}"
echo -e "Job number: ${TRAVIS_JOB_NUMBER}"
echo -e "************************************"
echo -e "Repo slug: ${TRAVIS_REPO_SLUG}"
echo -e "JAVA_HOME: ${JAVA_HOME}"
echo -e "JAVA_OPTS: ${JAVA_OPTS}"
echo -e "OS name: ${TRAVIS_OS_NAME}"
echo -e "************************************"
echo -e "Commit: ${TRAVIS_COMMIT}"
echo -e "Commit Range: ${TRAVIS_COMMIT_RANGE}"
echo -e "Commit Message: ${TRAVIS_COMMIT_MESSAGE}"
echo -e "************************************"
echo -e "User: ${USER}"
echo -e "User HOME directory: ${HOME}"
echo -e "Secure environment variables: ${TRAVIS_SECURE_ENV_VARS}"
echo -e "************************************"

echo -e "Stopping current services...\n"
sudo service mysql stop
sudo service postgresql stop

echo -e "Setting build environment...\n"
sudo mkdir -p /etc/cas/config /etc/cas/saml /etc/cas/services

echo -e "Installing Java...\n"
wget https://github.com/sormuras/bach/raw/master/install-jdk.sh && chmod +x install-jdk.sh
export JAVA_HOME=$(./install-jdk.sh --emit-java-home -F 11 -c | tail --lines 1) && echo $JAVA_HOME

chmod -R 777 ./ci/*.sh

echo -e "Configuring Gradle wrapper...\n"
mkdir -p ~/.gradle && echo "org.gradle.daemon=false" >> ~/.gradle/gradle.properties
chmod -R 777 ./gradlew

echo "Home directory: $HOME"

echo "Gradle Home directory:"
./gradlew gradleHome --no-daemon

rvm install 2.6.3
rvm use 2.1.1
ruby -v
which ruby

echo -e "Configured build environment\n"
