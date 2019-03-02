#!/usr/bin/env bash

PROJECTS=~/projects

PROJECT=architecture-builder
SERVICE=budget-service

ROOT_DIR=${PROJECTS}/${PROJECT}
SERVICE_DIR=${ROOT_DIR}/builder/${SERVICE}


# generate service
cd ${ROOT_DIR}
./gradlew bootRun

cd -

if [ -d "${SERVICE_DIR}" ]
then
    if [ -d ${SERVICE} ]
    then
        echo
        echo -n " * Removing existing $SERVICE directory... "
        rm -r ${SERVICE}
        echo "done"
    fi
    mv ${SERVICE_DIR} .
else
    echo "No service directory."
    exit 1
fi

cd ${SERVICE}

echo " * Starting new service..."

./gradlew bootRun
