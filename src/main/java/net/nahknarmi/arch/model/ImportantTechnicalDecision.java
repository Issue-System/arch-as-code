package net.nahknarmi.arch.model;

import java.util.Date;

public class ImportantTechnicalDecision {
    private String id;
    private Date date;
    private String title;
    private String status;
    private String content;

    public ImportantTechnicalDecision() {
    }

    public ImportantTechnicalDecision(String id, Date date, String title, String status, String content) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.status = status;
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
