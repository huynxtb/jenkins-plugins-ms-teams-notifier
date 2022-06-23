# Read Me

[//]: # (This repo has been moved to [jenkinsci organization]&#40;https://github.com/jenkinsci/notifier-plugin&#41;. Please submit issues/PRs there.)

# Microsoft Teams Notifier

Microsoft Teams Notifier provides a bridge between Jenkins and Microsoft Teams through the built-in webhook functionality.
- Get success and fail messages about your job
- Link to build artifacts
## The purpose

The Jenkins Microsoft Teams Notifier plugin was made to share results of a build to a Microsoft Teams channel using the webhooks that Microsoft Teams provides.

## Download

You'll have to manually install the plugin via the advanced tab of your plugin settings.
A Jenkins plugin repo build will be available soon.

## Usage

This plugin uses the post-build feature to execute a request.

After installing, go to your job's configure section and add the **Microsoft Teams Notifier** item. Then proceed to enter your webhook URL.

![Post-build dropdown with Microsoft Teams Notifier Webhooks selected](https://imgur.com/28tUU2b.png)

There are a few options you can choose from:

- Webhook URL
  - The URL of the webhook provided by Microsoft Teams ([Get Webhook URL here](https://docs.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook)).
- Advanced:
  - Branch
    - If set, the branch will show up on the Microsoft Teams message.
  - Title
    - If set, the title will show up on the Microsoft Teams message. By default, the title is project name
  - Web URL
    - If set, the web url will show up on the Microsoft Teams message
  - Description
    - If set, the web url will show up on the Microsoft Teams message
  - Time Zone
    - By default, this will set time zone is UTC ([Get Time Zone here](https://docs.oracle.com/middleware/1221/wcs/tag-ref/MISC/TimeZones.html)).

![Advanced tab in the config](https://imgur.com/ucTgisL.png)

## Pipeline

Microsoft Teams Notifier supports Jenkins Pipeline. The only required parameter is webhookURL (the URL of the webhook, of course) - but there isn't much point of sending nothing.

### Parameters

- webhookURL (required)
  - The URL of the webhook (pretty self-explanatory) provided by Microsoft Teams ([Get Webhook URL here](https://docs.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook)). 
- title (required)
  - The title of the message.
- result (required)
  - Sets notice message (SUCCESS - green, UNSTABLE - yellow, FAILURE - red, ABORTED - grey).
- branchName
  - If set, current branch build will show. 
- commitId
  - If set, current commit build will show.
- description
  - If set, the description will show on the message.
- webUrl
  - If set, the web url will show on the message.
- jobLink
  - If set, the job url will show on the message.
- buildNumber
  - If set, current build number build will show.
- timeZone
  - By default, this will set time zone is UTC ([Get Time Zone here](https://docs.oracle.com/middleware/1221/wcs/tag-ref/MISC/TimeZones.html)).

### Example

```
pipeline {
  agent any

  post {
    always {
      	msTeamsNotifier webhookURL: 'YOUR_WEBHOOK_URL', title: JOB_NAME, branchName: GIT_BRANCH, commitId: GIT_COMMIT.substring(0, 7), description: 'This is job test', result: currentBuild.currentResult, webUrl: 'https://www.jenkins.io/', jobLink: BUILD_URL, buildNumber: currentBuild.number, timeZone: 'Asia/Bangkok'
    }
  }
}
```
