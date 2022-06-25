package io.jenkins.plugins.dto;

import io.jenkins.plugins.constants.MessageConst;
import io.jenkins.plugins.constants.ResultConst;
import io.jenkins.plugins.enums.StatusColor;

public class BindingControlDto {
    private String status;
    private String normalStatus;
    private String color;
    private String title;
    private String description;
    private String jobLink;
    private String webUrl;
    public BindingControlDto(String status, String title, String description) {
        this.status = status;
        switch (status){
            case ResultConst.SUCCESS:
                this.color = StatusColor.GREEN.getColor();
                this.normalStatus = MessageConst.SUCCESS;
                break;
            case ResultConst.ABORTED:
                this.color = StatusColor.GRAY.getColor();
                this.normalStatus = MessageConst.ABORTED;
                break;
            case ResultConst.FAILURE:
                this.color = StatusColor.RED.getColor();
                this.normalStatus = MessageConst.FAILURE;
                break;
            case ResultConst.UNSTABLE:
                this.color = StatusColor.YELLOW.getColor();
                this.normalStatus = MessageConst.UNSTABLE;
                break;
        }
        this.title = title;
        this.description = description;
    }

    public String getJobLink() {return jobLink;}

    public void setJobLink(String jobLink) {this.jobLink = jobLink;}

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getNormalStatus() {return normalStatus;}

    public void setNormalStatus(String normalStatus) {this.normalStatus = normalStatus;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public String getColor() {return color;}

    public void setColor(String color) {this.color = color;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}
}
