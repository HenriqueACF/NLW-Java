package com.example.passin.dto.attendee;

import java.time.LocalDateTime;

public record AttendeeDetails(String id, String email, String attendeeEmail, LocalDateTime createdAt, LocalDateTime checkedInAt) {
}
