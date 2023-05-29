package com.javarush.jira.ref;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping()
public class AdminUIReferenceController {
    static final String URL = "/ui/admin/references";

    @GetMapping("/ui/admin/references")
    public String getRefTypes(Model model) {
        model.addAttribute("refTypes", RefType.values());
        return "references";
    }
}

