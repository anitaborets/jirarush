package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import com.javarush.jira.bugtracking.internal.model.Watcher;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.UserBelongRepository;
import com.javarush.jira.bugtracking.internal.repository.WatchersRepository;
import com.javarush.jira.bugtracking.to.ActivityTo;
import com.javarush.jira.bugtracking.to.ObjectType;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.User;
import com.javarush.jira.login.internal.UserRepository;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.javarush.jira.bugtracking.internal.model.Activity.newActivity;
import static com.javarush.jira.common.Constants.*;

@Service
@Slf4j
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {
    @Autowired
    TaskMapper taskMapper;
    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    @DomainEvents
    public Collection<Object> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    @AfterDomainEventPublication
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    @Autowired
    UserBelongRepository userBelongRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WatchersRepository watchersRepository;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        super(repository, mapper);
    }

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

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

    public List<Task> getAvailableTasks(long id) {
        return repository.getAvailableTasks(id);
    }

    public List<Task> getTasksForListening(long id) {
        return repository.getAll();
    }

    public List<Task> getMyTasks(long id) {
        List<Task> myTasks = repository.getMyTask(id);
        if (!myTasks.isEmpty()) {
            return myTasks;
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional
    public void add(TaskTo task) {
        //todo
        System.out.println(task);
        //System.out.println(task.isEnabled());

        // if(task.isEnabled()) {

        Task taskForSave = mapper.toEntity(task);
        //taskForSave.setEnabled(true);

        repository.save(taskForSave);
        // }

        log.warn("task was added, title: " + task.getTitle());
    }

    @Transactional
    public void assign(Task task, long id) {
        if (task != null && task.getId() != null) {
            Optional<Task> optional = repository.findById(task.getId());
            if (optional.isPresent()) {
                UserBelong userBelong = new UserBelong();
                userBelong.setUserId(id);
                userBelong.setObjectId(task.getId());
                userBelong.setObjectType(ObjectType.TASK);
                userBelong.setUserTypeCode("user");
                userBelong.setStartpoint(LocalDateTime.now());
                userBelongRepository.save(userBelong);
                Activity activity = newActivity(optional.get(), userRepository.getExisted(id), ASSIGNATION, TASK_WAS_ASSIGN, ASSIGNATION, "normal", "in_progress", "task", 5);
                activityRepository.save(activity);
                log.warn("Task was asign, task id: " + task.getId() + ", user id:" + id);
            }
        }
    }

    @Transactional
    public void watch(Task task, AuthUser authUser) {
        if (task != null && task.getId() != null && repository.findById(task.getId()).isPresent()) {
            long rows = watchersRepository.count();
            Watcher watcher = new Watcher(authUser.getUser(), task);
            watchersRepository.save(watcher);
            if (!(rows == watchersRepository.count())) {
                registerEvent(watcher);
                Activity activity = newActivity(task, authUser.getUser(), NEW_WATCHER, NEW_WATCHER, NEW_WATCHER, "normal", "in_progress", "task", 5);
                activityRepository.save(activity);
                log.warn("New watcher was added, task id: " + task.getId());
            }
        }
    }
}
