#!/bin/bash

echo "stages: [generate_docker_image_stage]"
echo ""

MODULES=$(grep "^include" settings.gradle | sed -e "s/include //" -e "s/'//g" -e "s/, /\n/g" | grep -v "shared-lib")

for MODULE in $MODULES; do
cat <<EOF

build_and_push_$MODULE:
  stage: generate_docker_image_stage
  image: registry.gitlab.com/event-hub-group/event-hub/ci-image:0.1
  services:
    - docker:dind
  variables:
    DOCKER_HOST: tcp://docker:2375/
  before_script:
    - echo "\$CI_JOB_TOKEN" | docker login -u gitlab-ci-token --password-stdin registry.gitlab.com
  script:
    - >-
      ./gradlew :$MODULE:build -x test
      -PREPOSITORY_USERNAME=gitlab-ci-token
      -PREPOSITORY_PASSWORD=$CI_JOB_TOKEN
      -PREPOSITORY_URL=https://gitlab.com/api/v4/projects/$CI_PROJECT_ID/packages/maven
    - docker build -t registry.gitlab.com/\$CI_PROJECT_PATH/$MODULE:\$CI_COMMIT_SHORT_SHA $MODULE
    - docker push registry.gitlab.com/\$CI_PROJECT_PATH/$MODULE:\$CI_COMMIT_SHORT_SHA
  cache:
    paths:
      - .gradle/
EOF
done
