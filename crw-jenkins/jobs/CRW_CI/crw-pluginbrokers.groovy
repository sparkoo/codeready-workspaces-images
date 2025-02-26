///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// 
///// THIS FILE IS DEPRECATED and not used; for 2.10+, see template_2.x.jenkinsfile
///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// ///// 

def JOB_BRANCHES = ["2.9":"v3.4.x"] 
def JOB_DISABLED = ["2.9":true]
for (JB in JOB_BRANCHES) {
    SOURCE_BRANCH=JB.value
    JOB_BRANCH=""+JB.key
    MIDSTM_BRANCH="crw-" + JOB_BRANCH.replaceAll(".x","") + "-rhel-8"
    jobPath="${FOLDER_PATH}/${ITEM_NAME}_" + JOB_BRANCH
    pipelineJob(jobPath){
        disabled(JOB_DISABLED[JB.key]) // on reload of job, disable to avoid churn
        UPSTM_NAME="che-plugin-broker"
        MIDSTM_NAME="pluginbroker"
        SOURCE_REPO="eclipse/" + UPSTM_NAME

        description('''

Deprecated! Use <a href=../crw-pluginbroker-artifacts_''' + JOB_BRANCH + '''>crw-pluginbroker-artifacts</a> 
and <a href=../crw-pluginbroker-metadata_''' + JOB_BRANCH + '''>crw-pluginbroker-metadata</a> builds instead.
<p>
Artifact builder + sync job; triggers brew after syncing
<ul>
<li>Upstream: <a href=https://github.com/''' + SOURCE_REPO + '''>''' + UPSTM_NAME + '''</a></li>
<li>Midstream 1: <a href=https://github.com/redhat-developer/codeready-workspaces-images/tree/''' + MIDSTM_BRANCH + '''/codeready-workspaces-pluginbroker-artifacts/>''' + MIDSTM_NAME + '''-artifacts</a></li>
<li>Midstream 2: <a href=https://github.com/redhat-developer/codeready-workspaces-images/tree/''' + MIDSTM_BRANCH + '''/codeready-workspaces-pluginbroker-metadata/>''' + MIDSTM_NAME + '''-metadata</a></li>
<li>Downstream 1: <a href=http://pkgs.devel.redhat.com/cgit/containers/codeready-workspaces-''' + MIDSTM_NAME + '''-artifacts?h=''' + MIDSTM_BRANCH + '''>''' + MIDSTM_NAME + '''-artifacts</a></li>
<li>Downstream 2: <a href=http://pkgs.devel.redhat.com/cgit/containers/codeready-workspaces-''' + MIDSTM_NAME + '''-metadata?h=''' + MIDSTM_BRANCH + '''>''' + MIDSTM_NAME + '''-metadata</a></li>
</ul>

<p>If <b style="color:green">downstream job fires</b>, see 
<a href=../sync-to-downstream_''' + JOB_BRANCH + '''/>sync-to-downstream</a>, then
<a href=../get-sources-rhpkg-container-build_''' + JOB_BRANCH + '''/>get-sources-rhpkg-container-build</a>. <br/>
   If <b style="color:orange">job is yellow</b>, no changes found to push, so no container-build triggered. </p>
<p>
Results: <a href=http://quay.io/crw/pluginbroker-metadata-rhel8>quay.io/crw/pluginbroker-metadata-rhel8</a> and
  <a href=http://quay.io/crw/pluginbroker-artifacts-rhel8>quay.io/crw/pluginbroker-artifacts-rhel8</a>
        ''')

        properties {
            ownership {
                primaryOwnerId("nboldt")
            }

            githubProjectUrl("https://github.com/" + SOURCE_REPO)

            // disabled because no changes in the branch / run this manually 
            // pipelineTriggers {
            //     triggers{
            //         pollSCM{
            //             scmpoll_spec("H H/24 * * *") // every 24hrs
            //         }
            //     }
            // }

            disableResumeJobProperty()
        }

        logRotator {
            daysToKeep(5)
            numToKeep(5)
            artifactDaysToKeep(2)
            artifactNumToKeep(1)
        }

        parameters{
            stringParam("SOURCE_BRANCH", SOURCE_BRANCH)
            stringParam("MIDSTM_BRANCH", MIDSTM_BRANCH)
            if (JOB_BRANCH.equals("2.9")) { 
                stringParam("UPDATE_BASE_IMAGES_FLAGS"," -maxdepth 1 --tag \"1\\\\.14|8\\\\.[0-9]-\" ", "Pass additional flags to updateBaseImages, eg., '--tag 1.14'")
            }
            booleanParam("FORCE_BUILD", false, "If true, trigger a rebuild even if no changes were pushed to pkgs.devel")
        }

        // Trigger builds remotely (e.g., from scripts), using Authentication Token = CI_BUILD
        authenticationToken('CI_BUILD')

        definition {
            cps{
                sandbox(true)
                script(readFileFromWorkspace('jobs/CRW_CI/crw-pluginbrokers_'+JOB_BRANCH+'.jenkinsfile'))
            }
        }
    }
}