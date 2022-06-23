package io.jenkins.plugins.dto;

import java.util.ArrayList;

public class MainBodyDto {
    private String type;
    private ArrayList<AttachmentDto> attachments;

    public MainBodyDto(ArrayList<AttachmentDto> attachments) {
        this.attachments = attachments;
        this.type = "message";
    }

    public String getType() {return this.type;}

    public void setType(String type) {this.type = type;}

    public ArrayList<AttachmentDto> getAttachments() {return this.attachments;}

    public void setAttachments(ArrayList<AttachmentDto> attachments) {this.attachments = attachments;}
}
