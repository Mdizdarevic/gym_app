package hr.algebra.gym_app.service;

import hr.algebra.gym_app.model.Booking;
import hr.algebra.gym_app.model.ClassInstance;
import hr.algebra.gym_app.model.ClassTemplate;
import hr.algebra.gym_app.model.Member;
import hr.algebra.gym_app.model.Membership;
import hr.algebra.gym_app.model.MembershipPlan;
import hr.algebra.gym_app.repository.BookingRepository;
import hr.algebra.gym_app.repository.ClassInstanceRepository;
import hr.algebra.gym_app.repository.ClassTemplateRepository;
import hr.algebra.gym_app.repository.MemberRepository;
import hr.algebra.gym_app.repository.MembershipPlanRepository;
import hr.algebra.gym_app.repository.MembershipRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GymService {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;
    private final ClassTemplateRepository classTemplateRepository;
    private final ClassInstanceRepository classInstanceRepository;
    private final BookingRepository bookingRepository;

    public GymService(
            MemberRepository memberRepository,
            MembershipPlanRepository planRepository,
            MembershipRepository membershipRepository,
            ClassTemplateRepository classTemplateRepository,
            ClassInstanceRepository classInstanceRepository,
            BookingRepository bookingRepository) {
        this.memberRepository = memberRepository;
        this.planRepository = planRepository;
        this.membershipRepository = membershipRepository;
        this.classTemplateRepository = classTemplateRepository;
        this.classInstanceRepository = classInstanceRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));
    }

    public List<Membership> getMembershipsByMemberId(Long memberId) {
        return membershipRepository.findByMemberId(memberId);
    }

    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByActiveTrue();
    }

    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    public Membership getMembershipById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found: " + id));
    }

    public List<ClassTemplate> getActiveTemplates() {
        return classTemplateRepository.findByActiveTrue();
    }

    public List<ClassInstance> getAllClassInstances() {
        return classInstanceRepository.findAllByOrderByStartsAtAsc();
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    @Transactional
    public Membership createMembership(Long memberId, Long planId, LocalDate startDate, LocalDate endDate) {
        Member member = getMemberById(memberId);
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Membership plan not found: " + planId));

        Membership membership = new Membership();
        membership.setMember(member);
        membership.setPlan(plan);
        membership.setStatus("ACTIVE");
        membership.setStartDate(startDate != null ? startDate : LocalDate.now());
        membership.setEndDate(endDate);
        return membershipRepository.save(membership);
    }

    @Transactional
    public Membership toggleFreezeMembership(Long membershipId) {
        Membership membership = getMembershipById(membershipId);
        if ("FROZEN".equals(membership.getStatus())) {
            membership.setStatus("ACTIVE");
            membership.setFrozenAt(null);
        } else if ("ACTIVE".equals(membership.getStatus())) {
            membership.setStatus("FROZEN");
            membership.setFrozenAt(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot toggle freeze for membership in status: " + membership.getStatus());
        }
        return membershipRepository.save(membership);
    }

    @Transactional
    public void deleteMembership(Long membershipId) {
        if (!membershipRepository.existsById(membershipId)) {
            throw new IllegalArgumentException("Membership not found: " + membershipId);
        }
        membershipRepository.deleteById(membershipId);
    }

    @Transactional
    public Booking createBooking(Long memberId, Long classInstanceId) {
        Member member = getMemberById(memberId);
        ClassInstance instance = classInstanceRepository.findById(classInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Class instance not found: " + classInstanceId));

        if (!"SCHEDULED".equals(instance.getStatus())) {
            throw new IllegalStateException("Class is not open for booking.");
        }

        long confirmedCount = bookingRepository.countByClassInstanceIdAndStatus(classInstanceId, "CONFIRMED");
        String targetStatus = confirmedCount < instance.getCapacity() ? "CONFIRMED" : "WAITLISTED";

        Booking booking = new Booking();
        booking.setMember(member);
        booking.setClassInstance(instance);
        booking.setStatus(targetStatus);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }

    @Transactional
    public ClassInstance createClassInstance(
            Long classTemplateId,
            LocalDate date,
            LocalTime startTime,
            String instructor,
            Integer capacity) {
        ClassTemplate template = classTemplateRepository.findById(classTemplateId)
                .orElseThrow(() -> new IllegalArgumentException("Class template not found: " + classTemplateId));

        LocalDateTime startsAt = LocalDateTime.of(date, startTime);
        int duration = template.getDurationMinutes() != null ? template.getDurationMinutes() : 60;
        LocalDateTime endsAt = startsAt.plusMinutes(duration);

        ClassInstance instance = new ClassInstance();
        instance.setClassTemplate(template);
        instance.setTitle(template.getTitle());
        instance.setInstructorName(instructor.trim());
        instance.setStartsAt(startsAt);
        instance.setEndsAt(endsAt);
        instance.setCapacity(capacity != null ? capacity : template.getDefaultCapacity());
        instance.setStatus("SCHEDULED");
        return classInstanceRepository.save(instance);
    }
}