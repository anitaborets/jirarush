package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.to.TaskTo;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {
    public TaskService(TaskRepository repository, TaskMapper mapper) {
        super(repository, mapper);
    }

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

    // public List<TaskTo> getBacklog() {
    //  return mapper.toToList(repository.getBacklog());
    //}

    @Transactional(readOnly = true)
    public Page<TaskTo> getBacklog(Integer page, Integer size) {
        if (page >= 0 && size >= 0) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("title"));
            List<TaskTo> tasks = mapper.toToList(repository.getBacklog(pageable));
            return new PageImpl<>(tasks.subList(page, size), pageable, tasks.size());

        } else {
            Pageable pageable = PageRequest.of(1, 3, Sort.by("title"));
            List<TaskTo> tasks = mapper.toToList(repository.getBacklog(pageable));
            return new PageImpl<>(tasks.subList(page, size), pageable, tasks.size());
        }

    }

    public List<String> getBacklogWithoutUser() {
        return repository.getBacklogUserNull();
    }
}
