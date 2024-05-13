package com.sourceallies.boilerplate.api.coffee.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

import static com.sourceallies.boilerplate.api.DateConstants.DATE_TIME_FORMAT;

@Entity
@Table(name = "menus")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class Menu {
    @Id
    Integer id;
    String name;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    ZonedDateTime createdDate;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    ZonedDateTime lastUpdatedDate;
}
