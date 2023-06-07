package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ActivityRepository extends BaseRepository<Activity> {

    @Query(value = "SELECT EXTRACT(DAY FROM (current_date - updated))" +
            "FROM activity " +
            "WHERE task_id=? AND description=? LIMIT 1", nativeQuery = true)
    Integer timeToCurrentDate(long taskId, String description);

    @Query(value = "SELECT extract(day from (? - updated))" +
            "FROM activity " +
            "WHERE task_id=? AND status_code=?", nativeQuery = true)
    Integer timeBetweenDates(Date date,long taskId, String statusCode);

    @Query(value = "SELECT * FROM activity WHERE task_id=? AND status_code='in_progress' LIMIT 1",
            nativeQuery = true)
    Optional<Activity> getTaskWithStatusInProgress(long taskId);

    @Query(value = "SELECT * FROM activity WHERE task_id=?",
            nativeQuery = true)
    List<Activity> getAll(long taskId);
}

