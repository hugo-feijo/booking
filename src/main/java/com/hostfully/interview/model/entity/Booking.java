package com.hostfully.interview.model.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Booking {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Booking id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private UUID id;

    @Schema(description = "Property that this booking belongs", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @ManyToOne
    private Property property;

    @Schema(description = "When the booking start", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "When the booking end", example = "2022-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate endDate;

    @Schema(description = "Booking status", example = "CONFIRMED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Schema(description = "When the booking was created", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate createdAt;

    @Schema(description = "When the last time the booking was updated", example = "2021-01-10")
    private LocalDate updateAt;

    @Schema(description = "Guest that belongs to this booking", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="BOOKING_ID")
    private List<Guest> guests;
}
