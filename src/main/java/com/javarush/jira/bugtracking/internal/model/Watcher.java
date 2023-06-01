package com.javarush.jira.bugtracking.internal.model;

import com.javarush.jira.common.model.BaseEntity;
import com.javarush.jira.common.util.validation.NoHtml;
import com.javarush.jira.login.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "watchers")
@Getter
@Setter
@Immutable
@NoArgsConstructor
public class Watcher {
    //реализация промежуточной сущности, идея из книги Java Persistence API
    @EmbeddedId
    protected Id id = new Id();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User watcher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;

    public Watcher(User watcher, Task task) {
        this.watcher = watcher;
        this.task = task;
        this.id.taskId = task.getId();
        this.id.userId = watcher.getId();
    }

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "user_id")
        protected Long userId;
        @Column(name = "task_id")
        protected Long taskId;

        public Id(Long userId, Long taskId) {
            this.userId = userId;
            this.taskId = taskId;

        }

        public Id() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(userId, id.userId) && Objects.equals(taskId, id.taskId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, taskId);
        }
    }
}