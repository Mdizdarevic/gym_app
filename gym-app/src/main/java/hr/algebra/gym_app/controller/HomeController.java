package hr.algebra.gym_app.controller;

import hr.algebra.gym_app.dto.BookingForm;
import hr.algebra.gym_app.model.Booking;
import hr.algebra.gym_app.service.GymService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final GymService gymService;

    public HomeController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping("/profile")
    public String profile(
            @RequestParam(name = "memberId", required = false) Long memberId,
            Model model) {
        var members = gymService.getAllMembers();
        Long selectedId = memberId != null ? memberId : (members.isEmpty() ? null : members.get(0).getId());

        model.addAttribute("members", members);
        model.addAttribute("plans", gymService.getActivePlans());
        if (selectedId != null) {
            model.addAttribute("selectedMember", gymService.getMemberById(selectedId));
            model.addAttribute("memberships", gymService.getMembershipsByMemberId(selectedId));
        } else {
            model.addAttribute("memberships", List.of());
        }
        return "profile/account";
    }

    @PostMapping("/profile/memberships")
    public String addMembership(
            @RequestParam Long memberId,
            @RequestParam Long planId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            RedirectAttributes redirectAttributes) {
        gymService.createMembership(memberId, planId, startDate, endDate);
        redirectAttributes.addFlashAttribute("flashMessage", "Membership added.");
        return "redirect:/profile?memberId=" + memberId;
    }

    @PostMapping("/profile/memberships/{id}/toggle-freeze")
    public String toggleFreeze(
            @PathVariable Long id,
            @RequestParam Long memberId,
            RedirectAttributes redirectAttributes) {
        gymService.toggleFreezeMembership(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Membership freeze status updated.");
        return "redirect:/profile?memberId=" + memberId;
    }

    @PostMapping("/profile/memberships/{id}/delete")
    public String deleteMembership(
            @PathVariable Long id,
            @RequestParam Long memberId,
            RedirectAttributes redirectAttributes) {
        gymService.deleteMembership(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Membership removed.");
        return "redirect:/profile?memberId=" + memberId;
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        var bookings = gymService.getAllBookings();
        model.addAttribute("bookings", bookings);
        model.addAttribute("members", gymService.getAllMembers());
        model.addAttribute("classInstances", gymService.getAllClassInstances());
        if (!model.containsAttribute("bookingForm")) {
            model.addAttribute("bookingForm", new BookingForm());
        }
        return bookings.isEmpty() ? "bookings/empty" : "bookings/list";
    }

    @PostMapping("/bookings")
    public String createBooking(
            @Valid @ModelAttribute("bookingForm") BookingForm bookingForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookings", gymService.getAllBookings());
            model.addAttribute("members", gymService.getAllMembers());
            model.addAttribute("classInstances", gymService.getAllClassInstances());
            return "bookings/list";
        }

        Booking created = gymService.createBooking(bookingForm.getMemberId(), bookingForm.getClassInstanceId());
        redirectAttributes.addFlashAttribute(
                "flashMessage", "Booking created (" + created.getStatus() + ").");
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gymService.cancelBooking(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Booking cancelled.");
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/{id}/delete")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gymService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Booking deleted.");
        return "redirect:/bookings";
    }

    @GetMapping("/memberships")
    public String memberships(Model model) {
        var plans = gymService.getActivePlans();
        var memberships = gymService.getAllMemberships();

        model.addAttribute("plans", plans);
        model.addAttribute("memberships", memberships);
        model.addAttribute("activePlanCount", plans.size());
        return "memberships/manage";
    }

    @GetMapping("/ai/feedback")
    public String aiFeedback() {
        return "ai/feedback";
    }

    @GetMapping("/")
    public String index(Model model) {
        var members = gymService.getAllMembers();
        var memberships = gymService.getAllMemberships();
        var classInstances = gymService.getAllClassInstances();
        var bookings = gymService.getAllBookings();

        long activeMemberships = memberships.stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()))
                .count();

        model.addAttribute("totalMembers", members.size());
        model.addAttribute("activeMemberships", activeMemberships);
        model.addAttribute("totalClasses", classInstances.size());
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("upcomingClasses", classInstances);
        model.addAttribute("recentBookings", bookings);
        return "index";
    }
}