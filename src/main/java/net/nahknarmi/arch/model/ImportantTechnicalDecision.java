package net.nahknarmi.arch.model;

import com.google.api.client.util.DateTime;

public class ImportantTechnicalDecision {
    private String elementId;
    private String id;
    private DateTime date;
    private String title;
    private String status;
    private String content;
    private String format;


    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return this.elementId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getDate() {
        return this.date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }
}
