#!/usr/bin/env bash

PROJECTS=~/projects
SERVICE=budget-service

SERVICE_DIR=${PROJECTS}/architecture-builder/builder/${SERVICE}


if [ -d "${SERVICE_DIR}" ]
then
    mv ${SERVICE_DIR} .
else
    echo "No service directory."
    exit 1
fi

cd ${SERVICE}

./gradlew bootRun
