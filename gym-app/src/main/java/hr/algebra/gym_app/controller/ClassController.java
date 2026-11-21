package hr.algebra.gym_app.controller;

import hr.algebra.gym_app.dto.ClassInstanceForm;
import hr.algebra.gym_app.service.GymService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/classes")
public class ClassController {

    private final GymService gymService;

    public ClassController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public String listClasses(Model model) {
        model.addAttribute("classes", gymService.getAllClassInstances());
        return "classes/list";
    }

    @GetMapping("/new")
    public String showScheduleForm(Model model) {
        model.addAttribute("classInstance", new ClassInstanceForm());
        model.addAttribute("templates", gymService.getActiveTemplates());
        return "classes/form";
    }

    @PostMapping("/save")
    public String saveClassInstance(
            @Valid @ModelAttribute("classInstance") ClassInstanceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("templates", gymService.getActiveTemplates());
            return "classes/form";
        }

        gymService.createClassInstance(
                form.getClassTemplateId(),
                form.getDate(),
                form.getStartTime(),
                form.getInstructor(),
                form.getCapacity());
        redirectAttributes.addFlashAttribute("flashMessage", "Class session scheduled.");
        return "redirect:/classes";
    }
}