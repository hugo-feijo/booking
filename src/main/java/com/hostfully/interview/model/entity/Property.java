package com.hostfully.interview.model.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Property {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Property id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private UUID id;

    @Schema(description = "Property name", example = "New Unit", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String name;
    //TODO: add created and updated date
}
