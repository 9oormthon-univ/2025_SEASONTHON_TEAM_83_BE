package com.seasonthon.pleanet.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    private Long userId; // 유저 ID (FK)

    private LocalDate attendanceDate; // 출석 날짜

    private LocalDateTime createdAt; // 생성 일시
}
