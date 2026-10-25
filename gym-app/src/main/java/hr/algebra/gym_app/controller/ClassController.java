package hr.algebra.gym_app.controller;

import hr.algebra.gym_app.dto.ClassInstanceForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/classes")
public class ClassController {

    public ClassController() {}

    @GetMapping
    public String listClasses(Model model) {
        return "classes/list";
    }

    @GetMapping("/new")
    public String showScheduleForm(Model model) {
        model.addAttribute("classInstance", new ClassInstanceForm());
        model.addAttribute("templates", List.of()); 
        return "classes/form";
    }

    @PostMapping("/save")
    public String saveClassInstance(@Valid @ModelAttribute("classInstance") ClassInstanceForm form,
                                    BindingResult bindingResult, 
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("templates", List.of());
            return "classes/form";
        }
        return "redirect:/classes";
    }
}