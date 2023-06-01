package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
public interface TaskRepository extends BaseRepository<Task> {

    //@Query("SELECT t FROM Task t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.sprint LEFT JOIN FETCH t.activities")
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.sprint")
    List<Task> getAll();

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project WHERE t.sprint = NULL")
    List<Task> getBacklog(Pageable pageable);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project WHERE t.sprint = NULL")
    List<Task> getBacklog();

    @Query(value = "SELECT * " +
            "FROM task WHERE task.id NOT IN (SELECT user_belong.object_id FROM user_belong)",
            nativeQuery = true)
    List<Task> getAvailableTasks(long userId);

    @Query(value = "SELECT task.id,task.title,task.description,task.project_id,task.priority_code,task.status_code," +
            "task.type_code,task.startpoint,task.endpoint,task.estimate,task.parent_id,task.sprint_id,task.updated FROM task " +
            "LEFT JOIN task_tag ON task.id = task_tag.task_id INNER JOIN user_belong ON task.id = user_belong.object_id WHERE user_belong.user_id = ? GROUP BY task.id",
            nativeQuery = true)
    List<Task> getMyTask(long userId);

    @Query(value = "SELECT * FROM task INNER JOIN user_belong ON task.id = user_belong.object_id",
            nativeQuery = true)
    List<Task> getForListen(long userId);
}

