package com.sourceallies.boilerplate.api.data.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Builder
@Data
public class AccountHierarchy {
    Integer id;
    String name;
    @Builder.Default
    List<AccountHierarchy> children = Collections.emptyList();
}
