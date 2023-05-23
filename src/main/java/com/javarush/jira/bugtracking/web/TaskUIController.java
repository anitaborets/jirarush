package com.javarush.jira.bugtracking.web;

import com.javarush.jira.bugtracking.TaskService;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("task", new Task());
        return "add";
    }

    @PostMapping("/add")
    public String add(Task task, BindingResult bindingResult) {
        System.out.println(task.getStatusCode());

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "add";
        }
        taskRepository.save(task);
        return "add";
    }

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

    //7.	Добавить возможность подписываться на задачи
    @GetMapping("/back")
    public String getAllFreeTasks(Model model) {
        List<String> tasksFree = taskService.getBacklogWithoutUser();
        for (String str :
                tasksFree) {
            System.out.println(str);

        }
        model.addAttribute("tasksFree", tasksFree);
        return "backlog";
    }
}
