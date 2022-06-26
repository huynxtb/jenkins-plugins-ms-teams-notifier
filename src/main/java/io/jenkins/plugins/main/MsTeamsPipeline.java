package io.jenkins.plugins.main;

import hudson.Extension;
import hudson.model.TaskListener;
import io.jenkins.plugins.constants.AppConst;
import io.jenkins.plugins.dto.*;
import io.jenkins.plugins.exception.AppException;
import io.jenkins.plugins.util.StringHelper;
import io.jenkins.plugins.util.Validation;
import io.jenkins.plugins.util.WebhookCaller;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import javax.inject.Inject;
import java.util.ArrayList;

public class MsTeamsPipeline extends AbstractStepImpl {
    private final String webhookURL;
    private String title;
    private String jobLink;
    private String branchName;
    private String commitId;
    private String description;
    private String result;
    private String webUrl;
    private int buildNumber;

    private String timeZone;

    @DataBoundConstructor
    public MsTeamsPipeline(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    public String getWebhookURL() {
        return this.webhookURL;
    }

    public String getBranchName() {
        return this.branchName;
    }

    @DataBoundSetter
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getTitle() {
        return this.title;
    }

    @DataBoundSetter
    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobLink() {
        return this.jobLink;
    }

    @DataBoundSetter
    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getDescription() {
        return this.description;
    }

    @DataBoundSetter
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommitId() {
        return this.commitId;
    }

    @DataBoundSetter
    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    @DataBoundSetter
    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    @DataBoundSetter
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    @DataBoundSetter
    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    @DataBoundSetter
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public static class MsTeamsPipelineExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;
        @Inject
        transient MsTeamsPipeline pipeline;

        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Void run() throws AppException {
            String timeZoneId = (pipeline.getTimeZone() != null) ? pipeline.getTimeZone() : "UTC";
            listener.getLogger().println("Starting Microsoft Teams Notifier");

            if (pipeline.getWebhookURL() == null)
                throw new AppException("Webhook Url is required. Following this way: msTeamsNotifier webhookURL: 'YOUR_WEBHOOK'");

            if (!Validation.isUrl(pipeline.getWebhookURL()))
                throw new AppException("Webhook URL invalid.");

            if (pipeline.getTitle() == null)
                throw new AppException("Title is required. Following this way: msTeamsNotifier title: 'YOUR_TITLE'");

            if (pipeline.getResult() == null)
                throw new AppException("Result is required. Following this way: msTeamsNotifier result: currentBuild.currentResult");

            BindingControlDto dto = new BindingControlDto(pipeline.getResult(), pipeline.getTitle(), pipeline.getDescription());
            ArrayList<FactDto> facts = new ArrayList<>();

            facts.add(new FactDto("Status:", dto.getNormalStatus()));
            facts.add(new FactDto("Build At:", StringHelper.toDateTimeNow(timeZoneId)));
            if (pipeline.getBuildNumber() != 0)
                facts.add(new FactDto("Build Number:", String.valueOf(pipeline.getBuildNumber())));
            if (pipeline.getBranchName() != null) facts.add(new FactDto("Branch:", pipeline.getBranchName()));
            if (pipeline.getCommitId() != null) facts.add(new FactDto("Commit ID:", pipeline.getCommitId()));

            if (pipeline.getJobLink() != null) {
                if (!Validation.isUrl(pipeline.getJobLink()))
                    throw new AppException("Job Link invalid.");
                dto.setJobLink(pipeline.getJobLink());
            }

            if (pipeline.getWebUrl() != null) {
                if (!Validation.isUrl(pipeline.getWebUrl()))
                    throw new AppException("Web Url invalid.");
                dto.setWebUrl(pipeline.getWebUrl());
            }

            try {
                WebhookCaller caller = new WebhookCaller(pipeline.getWebhookURL());
                caller.send(facts, dto);

                listener.getLogger().println(AppConst.APP_NAME + " " + AppConst.VERSION + " - " + AppConst.AUTHOR);
                listener.getLogger().println("Sending notification to Microsoft Teams.");
            } catch (Exception e) {
                e.printStackTrace(listener.getLogger());
            }
            return null;
        }
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(MsTeamsPipelineExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "msTeamsNotifier";
        }

        @Override
        public String getDisplayName() {
            return "Send a message to Webhook URL";
        }
    }
}
