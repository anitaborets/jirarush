package com.javarush.jira.bugtracking.web;

import com.javarush.jira.bugtracking.TaskService;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.WatchersRepository;
import com.javarush.jira.login.AuthUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping()
public class TaskUIController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    WatchersRepository watchersRepository;
    @Autowired
    ActivityRepository activityRepository;

    @GetMapping("/backlog")
    public String getAll(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam(value = "page", required = false) @Min(0) Integer page,
                         @RequestParam(value = "size", required = false, defaultValue = "2") @Min(0) @Max(100) Integer size) {

        int totalPages = (int) Math.ceil(1.0 * taskRepository.getBacklog().size() / size);
        if (totalPages > 1) {
            List<Integer> pagenumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("page_numbers", pagenumbers);
        }


        model.addAttribute("current_page", page);

        if (page == null || size == null) {
            model.addAttribute("tasks", taskService.getBacklog(0, size));
        } else {
            model.addAttribute("tasks", taskService.getBacklog(page - 1, size));
        }
        return "backlog";
    }

    @GetMapping("/free")
    public String getAllFreeTasks(Model model, @AuthenticationPrincipal AuthUser authUser) {
        long id = authUser.id();
        List<Task> tasksFree = taskService.getAvailableTasks(id);
        model.addAttribute("tasksFree", tasksFree);
        return "free";
    }

    @GetMapping("/assign")
    @Transactional
    public String assigning(@ModelAttribute("task") Task task, BindingResult bindingResult, @AuthenticationPrincipal AuthUser authUser) {
        taskService.assign(task, authUser.id());
        return "redirect:/free";
    }

    @GetMapping("/watch")
    @Transactional
    public String watching(Model model, @ModelAttribute("task") Task task, BindingResult bindingResult, @AuthenticationPrincipal AuthUser authUser) {
        taskService.watch(task, authUser);
        return "redirect:/all";
    }

    @GetMapping("/toTest")
    @Transactional
    public String testing(Model model, @ModelAttribute("task") Task task, BindingResult bindingResult, @AuthenticationPrincipal AuthUser authUser) {
        if (!task.getStatusCode().equals("ready")) {
            taskService.toTest(task, authUser.id());
        }
        return "redirect:/my";
    }

    @GetMapping("/done")
    @Transactional
    public String done(Model model, @ModelAttribute("task") Task task, BindingResult bindingResult, @AuthenticationPrincipal AuthUser authUser) {
        if (!task.getStatusCode().equals("done")) {
            taskService.done(task, authUser.id());
        }
        return "redirect:/my";
    }

    @GetMapping("/all")
    public String getTasks(Model model, @AuthenticationPrincipal AuthUser authUser) {
        long id = authUser.id();
        model.addAttribute("tasks", taskService.getTasksForListening(id));
        return "all";
    }

    @GetMapping("/my")
    public String myTasks(Model model, @AuthenticationPrincipal AuthUser authUser) {
        model.addAttribute("tasks", taskService.getMyTasks(authUser.id()));
        return "my";
    }

    @GetMapping("/tag")
    public String addTag(@ModelAttribute("task") Task task, @ModelAttribute("tag") String tag, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "redirect:/my";
        }
        Optional<Task> optional = taskService.getById(task.getId());
        if (optional.isPresent()) {
            Task existed = optional.get();
            Set<String> tags = existed.getTags();
            tags.add(tag);
            existed.setTags(tags);
            taskService.updateTask(existed);
        }
        return "redirect:/my";
    }

    @GetMapping("/time")
    public String modal(Model model, @RequestParam("id") Long id) {
        Map<String, Integer> times = taskService.getTime(id);
        StringBuilder builder = new StringBuilder();
        builder.append("Time info, days: ");
        for (Map.Entry<String, Integer> entry : times.entrySet()) {
            if (entry.getValue() != 0) {
                builder.append(entry.getKey()).append(" : ").append(entry.getValue().toString()).append(" ");
            }
        }
        model.addAttribute("show", true);
        model.addAttribute("times", builder);
        return "all";
    }
}
