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
public class Block {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Block id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private UUID id;

    @Schema(description = "Property that this block belongs", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(cascade = CascadeType.ALL)
    private Property property;

    @Schema(description = "When the booking start", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "When the booking end", example = "2022-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate endDate;

    @Schema(description = "When the block was created", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate createdAt;

    @Schema(description = "When the last time the block was updated", example = "2021-01-10")
    private LocalDate updateAt;
}
