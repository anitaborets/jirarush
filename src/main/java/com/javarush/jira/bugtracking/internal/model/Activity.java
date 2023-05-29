package com.javarush.jira.bugtracking.internal.model;

import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.common.model.BaseEntity;
import com.javarush.jira.common.util.validation.Description;
import com.javarush.jira.common.util.validation.NoHtml;
import com.javarush.jira.common.util.validation.Title;
import com.javarush.jira.login.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    @NoHtml
    @Nullable
    @Size(max = 4096)
    @Column(name = "comment")
    private String comment;

    @UpdateTimestamp
    @Nullable
    @Column(name = "updated")
    private LocalDateTime updated;

    //    history of task's status have been changed
    @Nullable
    @Column(name = "status_code")
    private String statusCode;

    @Nullable
    @Column(name = "priority_code")
    private String priorityCode;

    @Nullable
    @Column(name = "type_code")
    private String typeCode;

    @Nullable
    @Size(max = 1024)
    @Column(name = "title", nullable = false)
    protected String title;

    @Nullable
    @Size(max = 4096)
    @Column(name = "description")
    private String description;

    @Nullable
    @Positive
    @Column(name = "estimate")
    private Integer estimate;

    public static Activity newActivity(Task task, User user, String title, String comment, String description, String priorityCode, String statusCode, String typeCode, Integer estimate) {
        Activity activity = new Activity();
        activity.setTask(task);
        activity.setAuthor(user);
        activity.setTitle(task.getTitle());
        activity.setComment(comment);
        activity.setDescription(description);
        activity.setUpdated(LocalDateTime.now());
        activity.setPriorityCode(priorityCode);
        activity.setStatusCode(task.getStatusCode());
        activity.setEstimate(estimate);
        activity.setTypeCode(typeCode);
        return activity;
    }
}