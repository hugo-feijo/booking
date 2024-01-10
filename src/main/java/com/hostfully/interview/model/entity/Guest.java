package com.hostfully.interview.model.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Guest {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Guest id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private UUID id;

    @Schema(description = "Guest name", example = "Alan Wake", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String name;

    @Schema(description = "When the guest was created", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate createdAt;

    @Schema(description = "When the last time the guest was updated", example = "2021-01-10")
    private LocalDate updateAt;
}
