package hr.algebra.gym_app.dto;

import jakarta.validation.constraints.NotNull;

public class BookingForm {

    @NotNull(message = "Please select a member.")
    private Long memberId;

    @NotNull(message = "Please select a class session.")
    private Long classInstanceId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getClassInstanceId() {
        return classInstanceId;
    }

    public void setClassInstanceId(Long classInstanceId) {
        this.classInstanceId = classInstanceId;
    }
}