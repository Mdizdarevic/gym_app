package hr.algebra.gym_app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_instances")
public class ClassInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_template_id")
    private ClassTemplate classTemplate;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "instructor_name", nullable = false, length = 120)
    private String instructorName;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED";

    @OneToMany(mappedBy = "classInstance", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ClassTemplate getClassTemplate() {
        return classTemplate;
    }

    public void setClassTemplate(ClassTemplate classTemplate) {
        this.classTemplate = classTemplate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}