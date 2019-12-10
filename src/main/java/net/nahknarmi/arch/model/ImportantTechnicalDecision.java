package net.nahknarmi.arch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ImportantTechnicalDecision {
    private String id;
    private Date date;
    private String title;
    private String status;
    private String content;

    ImportantTechnicalDecision() {
    }
}
