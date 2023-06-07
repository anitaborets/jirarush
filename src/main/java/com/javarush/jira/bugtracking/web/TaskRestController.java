package com.javarush.jira.bugtracking.web;

import com.javarush.jira.bugtracking.TaskService;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.User;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.web.AbstractProfileController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskRestController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    public static final String REST_URL = "/api/task";

    @GetMapping("api/task")
    public List<Task> getAll(@AuthenticationPrincipal AuthUser authUser) {
        return taskRepository.getBacklog();
    }

    @PostMapping(value = "api/task/tags", params = {"id", "tag"})
    public ResponseEntity<Task> addTags(@RequestParam Long id, @RequestParam String tag) {
        Optional<Task> optional = taskService.getById(id);
        if (optional.isPresent()) {
            Task existed = optional.get();
            Set<String> tags = existed.getTags();
            tags.add(tag);
            existed.setTags(tags);
            return new ResponseEntity<>(taskRepository.save(existed), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

