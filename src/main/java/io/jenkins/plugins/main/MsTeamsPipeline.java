package io.jenkins.plugins.main;

import hudson.Extension;
import hudson.model.TaskListener;
import io.jenkins.plugins.constants.AppConst;
import io.jenkins.plugins.dto.AttachmentDto;
import io.jenkins.plugins.dto.ContentDto;
import io.jenkins.plugins.dto.MainBodyDto;
import io.jenkins.plugins.dto.SectionDto;
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
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @DataBoundConstructor
    public MsTeamsPipeline(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    public String getWebhookURL() {
        return webhookURL;
    }

    public String getBranchName() {
        return branchName;
    }

    @DataBoundSetter
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getTitle() {
        return title;
    }

    @DataBoundSetter
    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobLink() {
        return jobLink;
    }

    @DataBoundSetter
    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getDescription() {
        return description;
    }

    @DataBoundSetter
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommitId() {
        return commitId;
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
        return result;
    }

    public String getWebUrl() {
        return webUrl;
    }

    @DataBoundSetter
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    @DataBoundSetter
    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public static class MsTeamsPipelineExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;
        @Inject
        transient MsTeamsPipeline pipeline;

        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Void run() throws AppException {
            String liDateBuild = "<li>Build date: " + StringHelper.toDateTimeNow() + "</li>";
            String liCommitId = "";
            String liJobLink = "";
            String liWebLink = "";
            String liBranchName = "";
            String bNumber = pipeline.getBuildNumber() != 0 ? " #" + pipeline.getBuildNumber() : "";
            listener.getLogger().println("Starting Microsoft Teams Notifier");

            if (pipeline.getWebhookURL() == null)
                throw new AppException("Webhook Url is required. Following this way: msTeamsNotifier webhookURL: 'YOUR_WEBHOOK'");

            if(!Validation.isUrl(pipeline.getWebhookURL()))
                throw new AppException("Webhook URL invalid.");

            if (pipeline.getTitle() == null)
                throw new AppException("Title is required. Following this way: msTeamsNotifier title: 'YOUR_TITLE'");

            if (pipeline.getResult() == null)
                throw new AppException("Result is required. Following this way: msTeamsNotifier result: currentBuild.currentResult");

            ArrayList<SectionDto> sections = new ArrayList<SectionDto>();
            sections.add(new SectionDto(StringHelper.getHeader(pipeline.getResult(), bNumber)));

            if (pipeline.getDescription() != null)
                sections.add(new SectionDto("<p>" + pipeline.getDescription() + "</p>"));

            if (pipeline.getBranchName() != null)
                liBranchName = "<li>Branch: " + pipeline.getBranchName() + "</li>";

            if (pipeline.getCommitId() != null)
                liCommitId = "<li>Commit ID: " + pipeline.getCommitId() + "</li>";

            if (pipeline.getWebUrl() != null){
                if(!Validation.isUrl(pipeline.getWebUrl()))
                    throw new AppException("Web Url invalid.");
                liWebLink = "<li>Web URL: <a href='" + pipeline.getWebUrl() + "'>Go to site</a></li>";
            }

            if (pipeline.getJobLink() != null){
                if(!Validation.isUrl(pipeline.getJobLink()))
                    throw new AppException("Job Link invalid.");
                liJobLink = "<li>View build: <a href='" + pipeline.getJobLink() + "'>Go to view</a></li>";
            }

            if (liBranchName.length() > 0) sections.add(new SectionDto(liBranchName));
            if (liCommitId.length() > 0) sections.add(new SectionDto(liCommitId));
            sections.add(new SectionDto(liDateBuild));
            if (liWebLink.length() > 0) sections.add(new SectionDto(liWebLink));
            if (liJobLink.length() > 0) sections.add(new SectionDto(liJobLink));

            ContentDto contentDto = new ContentDto(pipeline.getTitle(), sections);
            ArrayList<AttachmentDto> attachments = new ArrayList<AttachmentDto>();
            attachments.add(new AttachmentDto(contentDto));
            MainBodyDto mainBodyDto = new MainBodyDto(attachments);

            try {
                WebhookCaller caller = new WebhookCaller(pipeline.getWebhookURL(), mainBodyDto);
                caller.send();

                listener.getLogger().println(AppConst.APP_NAME + " " + AppConst.VERSION + " - " + AppConst.AUTHOR);
                listener.getLogger().println("Sending notification to Microsoft Teams.");
            } catch (Exception e) {e.printStackTrace(listener.getLogger());}
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

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Send a message to Webhook URL";
        }
    }
}
