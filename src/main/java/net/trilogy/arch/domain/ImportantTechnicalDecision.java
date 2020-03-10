package net.trilogy.arch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportantTechnicalDecision {
    @NonNull private String id;
    @NonNull private Date date;
    @NonNull private String title;
    @NonNull private String status;
    @NonNull private String content;
}
