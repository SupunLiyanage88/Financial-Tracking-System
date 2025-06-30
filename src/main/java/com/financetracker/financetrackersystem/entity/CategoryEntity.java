package com.financetracker.financetrackersystem.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryEntity {
    private String id;
    private String categoryName;

}
