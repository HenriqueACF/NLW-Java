package com.example.passin.services;

import com.example.passin.domain.attendee.Attendee;
import com.example.passin.domain.attendee.exceptions.AttendeeAlreadyExistException;
import com.example.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import com.example.passin.domain.checkin.CheckIn;
import com.example.passin.dto.attendee.AttendeeBadgeResponseDTO;
import com.example.passin.dto.attendee.AttendeeDetails;
import com.example.passin.dto.attendee.AttendeesListResponseDTO;
import com.example.passin.dto.attendee.AttendeeBadgeDTO;
import com.example.passin.repositories.AttendeeRepository;
import com.example.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckInRepository checkInRepository;

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeeDetailsList = attendeeList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInRepository.findByAttendeeId(attendee.getId());
            LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return new AttendeeDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkedInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeeDetailsList);

    }

    public void verifyAttendeeSubscription(String email,String eventId){
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);

        if(isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyExistException("O participante já está registrado");
    }

    public Attendee registerAttendee(Attendee newAttendee){
        this.attendeeRepository.save(newAttendee);
        return newAttendee;
    }

    //RETORNO DO CRACHA DO PARTICIPANTE
    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder){
        Attendee attendee = this.attendeeRepository.findById(attendeeId).orElseThrow(()-> new AttendeeNotFoundException("Participante não encontrado com ID: " + attendeeId));

        var uri = uriComponentsBuilder.path("/attendees/{attendeeId}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(attendee.getName(), attendee.getEmail(), uri, attendee.getEvent().getId());
        return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);

    }
}
