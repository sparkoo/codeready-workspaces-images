#!/bin/bash
#
# Copyright (c) 2018-2021 Red Hat, Inc.
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#

# script to remove previous release's jobs from Jenkins

# defaults
JENKINSURL="https://main-jenkins-csb-crwqe.apps.ocp4.prod.psi.redhat.com"

usage() {
  echo "
Remove Jenkins job associated with previous release.

Usage: $0 [-t 2.y] 
Example: $0 -t 2.previous-release
"
  exit
}

usageToken()
{
  echo "To delete jobs, you must:
  1. open ${JENKINSURL}/me/configure
  2. create an API token
  3. store your kerberos username and token in file /tmp/token, in format:

username:token
"
  exit
}


# commandline args
while [[ "$#" -gt 0 ]]; do
  case $1 in
    '-t') CRW_VERSION="$2"; shift 1;; # 2.y # version of job to delete from Jenkins
  esac
  shift 1
done

if [[ ! ${CRW_VERSION} ]]; then usage; fi

# TODO update this after removing 2.9 jobs - no more crw-pluginbrokers or crw-theia-containers jobs
if [[ -f /tmp/token ]]; then
    USERTOKEN=$(cat /tmp/token)
    for d in \
        crw-configbump\
        crw-dashboard\
        crw-deprecated\
        crw-devfileregistry\
        crw-devworkspace-controller\
        crw-devworkspace\
        crw-imagepuller\
        crw-jwtproxy\
        crw-machineexec\
        crw-operator-metadata\
        crw-operator\
        crw-pluginbroker-artifacts\
        crw-pluginbroker-metadata\
        crw-pluginbrokers\
        crw-pluginregistry\
        crw-server\
        crw-theia-akamai\
        crw-theia-sources\
        crw-traefik\
        crwctl\
        get-sources-rhpkg-container-build\
        push-latest-container-to-quay\
        sync-to-downstream\
        update-digests-in-registries-and-metadata\
        Releng/job/build-all-images\
        Releng/job/send-email-qe-build-list\
        ; do 
        result=$(curl -sSL -X DELETE "${JENKINSURL}/job/CRW_CI/job/${d}_${CRW_VERSION}/" --user "${USERTOKEN}" | grep -E "Unauthorized|Authentization|401|URI")
        if [[ $result ]]; then
            echo "ERROR!"
            echo "$result"
            echo
            #usageToken
        else
            echo "Deleted: ${JENKINSURL}/job/CRW_CI/job/${d}_${CRW_VERSION}/"
        fi
    done
else
  usageToken
fi
