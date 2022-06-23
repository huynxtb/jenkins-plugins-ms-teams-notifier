package io.jenkins.plugins.main;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import io.jenkins.plugins.constants.AppConst;
import io.jenkins.plugins.dto.AttachmentDto;
import io.jenkins.plugins.dto.ContentDto;
import io.jenkins.plugins.dto.MainBodyDto;
import io.jenkins.plugins.dto.SectionDto;
import io.jenkins.plugins.exception.AppException;
import io.jenkins.plugins.util.StringHelper;
import io.jenkins.plugins.util.Validation;
import io.jenkins.plugins.util.WebhookCaller;
import jenkins.model.JenkinsLocationConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MsTeamsBuilder extends Notifier {
    private final String webhookURL;
    private final String branchName;
    private final String title;
    private final String webUrl;
    private final String description;
    private final String timeZone;

    @DataBoundConstructor
    public MsTeamsBuilder(String webhookURL, String branchName, String title, String description, String webUrl, String timeZone) {
        this.webhookURL = webhookURL;
        this.title = title;
        this.description = description;
        this.webUrl = webUrl;
        this.branchName = branchName;
        this.timeZone = timeZone;
    }

    public String getWebhookURL() {return this.webhookURL;}

    public String getBranchName() {return this.branchName;}

    public String getTitle() {return this.title;}

    public String getWebUrl() {return this.webUrl;}

    public String getDescription() {return this.description;}

    public String getTimeZone() {return this.timeZone;}

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        final EnvVars env = build.getEnvironment(listener);
        String timeZoneId = "UTC";

        if (this.timeZone != null) {
            if(this.timeZone.length() > 0){
                timeZoneId = this.timeZone;
            }
        }

        String buildNumber = " #" + env.get("BUILD_NUMBER");
        String commitId = env.get("COMMIT_ID");
        String jobName = "";
        JenkinsLocationConfiguration globalConfig = JenkinsLocationConfiguration.get();
        Result result = null;
        Result buildResult = build.getResult();
        String liDateBuild = "<li>Build date: " + StringHelper.toDateTimeNow(timeZoneId) + "</li>";
        String liCommitId = "";
        String liJobLink = globalConfig.getUrl() + build.getUrl();
        String liWebLink = "";
        String liBranchName = "";

        if (buildResult != null && !buildResult.isCompleteBuild()) return true;
        if (buildResult != null && buildResult.isBetterOrEqualTo(Result.SUCCESS)) result = Result.SUCCESS;
        if (buildResult != null && buildResult.isWorseThan(Result.SUCCESS)) result = Result.UNSTABLE;
        if (buildResult != null && buildResult.isWorseThan(Result.UNSTABLE)) result = Result.FAILURE;

        ArrayList<SectionDto> sections = new ArrayList<SectionDto>();
        try {
            if (result != null)
                sections.add(new SectionDto(StringHelper.getHeader(result.toString(), buildNumber)));
        } catch (AppException e) {
            throw new RuntimeException(e);
        }

        if (this.description != null)
            if (this.description.length() > 0)
                sections.add(new SectionDto("<p>" + this.description + "</p>"));

        if (this.branchName != null)
            if (this.branchName.length() > 0)
                liBranchName = "<li>Branch: " + this.branchName + "</li>";

        if (commitId != null)
            if (commitId.length() > 0)
                liCommitId = "<li>Commit ID: " + commitId + "</li>";

        if (this.webUrl != null) {
            if (this.webUrl.length() > 0)
                if (!Validation.isUrl(this.webUrl)){
                    listener.getLogger().println("Web Url invalid.");
                    return false;
                }
            liWebLink = "<li>Web URL: <a href='" + this.webUrl + "'>Go to site</a></li>";
        }

        if (liJobLink.length() > 0) {
            if(globalConfig.getUrl()!=null){
                if (!Validation.isUrl(liJobLink)){
                    listener.getLogger().println("Job Link invalid.");
                    return false;
                }
            }
            liJobLink = "<li>View build: <a href='" + liJobLink + "'>Go to view</a></li>";
        }

        if (this.title != null) {
            if (this.title.length() > 0)
                jobName = this.title;
        }else{
            jobName = build.getProject().getDisplayName();
        }

        if (liBranchName.length() > 0) sections.add(new SectionDto(liBranchName));
        if (liCommitId.length() > 0) sections.add(new SectionDto(liCommitId));
        sections.add(new SectionDto(liDateBuild));
        if (liWebLink.length() > 0) sections.add(new SectionDto(liWebLink));
        if (liJobLink.length() > 0) sections.add(new SectionDto(liJobLink));

        ContentDto contentDto = new ContentDto(jobName, sections);
        ArrayList<AttachmentDto> attachments = new ArrayList<AttachmentDto>();
        attachments.add(new AttachmentDto(contentDto));
        MainBodyDto mainBodyDto = new MainBodyDto(attachments);

        try {
            WebhookCaller caller = new WebhookCaller(this.webhookURL, mainBodyDto);
            caller.send();

            listener.getLogger().println(AppConst.APP_NAME + " " + AppConst.VERSION + " - " + AppConst.AUTHOR);
            listener.getLogger().println("Sending notification to Microsoft Teams.");
        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            return false;
        }

        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public FormValidation doCheckWebhookURL(@QueryParameter String value) {
            if (value == null || value.length() == 0)
                return FormValidation.error("Webhook URL is required.");
            if (!Validation.isUrl(value))
                return FormValidation.error("Please enter a valid Webhook URL.");
            return FormValidation.ok();
        }

        public FormValidation doCheckWebUrl(@QueryParameter String value) {
            if (value != null)
                if (value.length() > 0)
                    if (!Validation.isUrl(value))
                        return FormValidation.error("Please enter a valid Web URL.");
            return FormValidation.ok();
        }

        public String getDisplayName() {
            return AppConst.APP_NAME;
        }

        public String getVersion() {
            return AppConst.VERSION;
        }
    }
}
