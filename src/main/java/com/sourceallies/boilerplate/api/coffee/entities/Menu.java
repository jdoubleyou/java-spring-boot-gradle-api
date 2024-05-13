package com.sourceallies.boilerplate.api.coffee.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menus_id_seq")
    @SequenceGenerator(name = "menus_id_seq", allocationSize = 1)
    Integer id;
    String name;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    ZonedDateTime createdDate;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    ZonedDateTime lastUpdatedDate;
}
