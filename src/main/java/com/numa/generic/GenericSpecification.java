// Created by Arjunsingh Rajpurohit.
package com.numa.generic;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class GenericSpecification {

    @Autowired
    EntityMapper entityMapper;



    //	<-------------------------------------- Crud Method -------------------------------------->

    //getById
    public <T> T getEntityById(Long id, JpaRepository<T, Long> repository, String entityName) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(entityName + " not found with ID: " + id));
    }

    //get all with search and pagination
    public <T> Page<T> getAllEntities(Class<T> entityClass, JpaSpecificationExecutor<T> repository, int page, int size, String searchQuery, String sortBy, String sortOrder) {
        Specification<T> spec = (size == 0 || searchQuery == null) ? Specification.where(null) : searchByQuery(searchQuery, entityClass);
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        return repository.findAll(spec, pageable);
    }

    //get all with filter
    public <T> List<T> getAllWithFilters(Class<T> entityClass, JpaSpecificationExecutor<T> repository, Map<String, Object> rawFilters) {
        Specification<T> finalSpec = Specification.where(null);

        if (rawFilters != null) {
            for (Map.Entry<String, Object> entry : rawFilters.entrySet()) {
                String fieldPath = entry.getKey();
                Object value = entry.getValue();
                if (value == null) continue;

                finalSpec = finalSpec.and((root, query, cb) -> {
                    try {
                        Path<?> path = root;
                        for (String part : fieldPath.split("\\.")) {
                            path = path.get(part);
                        }
                        return cb.equal(path, value);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        throw new RuntimeException("Invalid filter field path: " + fieldPath, e);
                    }
                });
            }
        }

        return repository.findAll(finalSpec);
    }

    //get all with filter search and pagination
    public <T> Page<T> getAllWithFilterSearchAndPage(Class<T> entityClass, JpaSpecificationExecutor<T> repository, Map<String, Object> rawFilters, int page, int size, String searchQuery, String sortBy, String sortOrder) {
        Specification<T> finalSpec = Specification.where(null);

        // Apply non-null filters
        if (rawFilters != null) {
            for (Map.Entry<String, Object> entry : rawFilters.entrySet()) {
                String fieldPath = entry.getKey();
                Object value = entry.getValue();
                if (value == null) continue;

                finalSpec = finalSpec.and((root, query, cb) -> {
                    try {
                        Path<?> path = root;
                        for (String part : fieldPath.split("\\.")) {
                            path = path.get(part);
                        }
                        return cb.equal(path, value);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        throw new RuntimeException("Invalid filter field path: " + fieldPath, e);
                    }
                });
            }
        }

        // Add dynamic search
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            Specification<T> searchSpec = searchByQuery(searchQuery, entityClass);
            finalSpec = finalSpec.and(searchSpec);
        }

        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        return repository.findAll(finalSpec, pageable);
    }

    //save and update
    public <T, D, R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>>
    void saveOrUpdateEntity(Long id, D dto, T newEntity, R repository) {
        saveOrUpdateEntity(id, dto, newEntity, repository, null);
    }

    public <T, D, R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>>
    void saveOrUpdateEntity(Long id, D dto, T newEntity, R repository, Map<String, Object> backendFields) {
        T entity = (id != null)
                ? getEntityById(id, repository, newEntity.getClass().getSimpleName())
                : newEntity;

        entityMapper.map(dto, entity);

        if (backendFields != null) {
            Class<?> clazz = entity.getClass();
            for (Map.Entry<String, Object> entry : backendFields.entrySet()) {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(entity, entry.getValue());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("Skipping backend field: " + entry.getKey());
                }
            }
        }

        repository.save(entity);
    }

    //save and update with response
    public <T, D, R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>>
    T saveOrUpdateEntityWithResponse(Long id, D dto, T newEntity, R repository) {
        return saveOrUpdateEntityWithResponse(id, dto, newEntity, repository, null);
    }

    public <T, D, R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>>
    T saveOrUpdateEntityWithResponse(Long id, D dto, T newEntity, R repository, Map<String, Object> backendFields) {
        T entity = (id != null)
                ? getEntityById(id, repository, newEntity.getClass().getSimpleName())
                : newEntity;

        entityMapper.map(dto, entity, true);

        if (backendFields != null) {
            Class<?> clazz = entity.getClass();
            for (Map.Entry<String, Object> entry : backendFields.entrySet()) {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(entity, entry.getValue());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("Skipping backend field: " + entry.getKey());
                }
            }
        }

        return repository.save(entity);
    }



    //	<-------------------------------------- Helper Method -------------------------------------->

    //getAllEntities
    public Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        int pageSize = size > 0 ? Math.min(size, 50) : Integer.MAX_VALUE;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return "updatedAt".equalsIgnoreCase(sortBy)
                ? PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("createdAt")))
                : PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
    }

    //getAllEntities
    public static <T> Specification<T> searchByQuery(String searchQuery, Class<T> entityClass) {
        return (root, query, criteriaBuilder) -> {
            if (searchQuery == null || searchQuery.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            String likeSearch = "%" + searchQuery.toLowerCase() + "%";

            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();

                // Handle Joins for Mapped Entities
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    Join<Object, Object> join = root.join(fieldName, JoinType.LEFT);

                    for (Field relatedField : fieldType.getDeclaredFields()) {
                        relatedField.setAccessible(true);
                        if (relatedField.getType().equals(String.class)) {
                            predicates.add(criteriaBuilder.like(
                                    criteriaBuilder.lower(join.get(relatedField.getName())),
                                    likeSearch
                            ));
                        }
                    }
                }

                // Handle String fields
                if (fieldType.equals(String.class)) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), likeSearch));
                }
                // Handle numeric fields
                else if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE) ||
                        fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
                    try {
                        Long parsedValue = Long.parseLong(searchQuery);
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    } catch (NumberFormatException ignored) {}
                } else if (fieldType.equals(Double.class) || fieldType.equals(Double.TYPE)) {
                    try {
                        Double parsedValue = Double.parseDouble(searchQuery);
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    } catch (NumberFormatException ignored) {}
                } else if (fieldType.equals(Float.class) || fieldType.equals(Float.TYPE)) {
                    try {
                        Float parsedValue = Float.parseFloat(searchQuery);
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    } catch (NumberFormatException ignored) {}
                } else if (fieldType.equals(BigDecimal.class)) {
                    try {
                        BigDecimal parsedValue = new BigDecimal(searchQuery).stripTrailingZeros();
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    } catch (NumberFormatException ignored) {}
                }
                // Handle Boolean fields
                else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
                    if (searchQuery.equalsIgnoreCase("true") || searchQuery.equalsIgnoreCase("false")) {
                        Boolean parsedValue = Boolean.parseBoolean(searchQuery);
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    }
                }
                // Handle UUID fields
                else if (fieldType.equals(UUID.class)) {
                    try {
                        UUID parsedValue = UUID.fromString(searchQuery);
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedValue));
                    } catch (IllegalArgumentException ignored) {}
                }
                // Handle Date fields
                else if (fieldType.equals(Date.class)) {
                    handleDateField(searchQuery, predicates, criteriaBuilder, root, fieldName, "Date");
                }
                // Handle LocalDate fields
                else if (fieldType.equals(LocalDate.class)) {
                    handleDateField(searchQuery, predicates, criteriaBuilder, root, fieldName, "LocalDate");
                }
                // Handle LocalDateTime fields
                else if (fieldType.equals(LocalDateTime.class)) {
                    handleDateField(searchQuery, predicates, criteriaBuilder, root, fieldName, "LocalDateTime");
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void handleDateField(String searchQuery, List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<T> root, String fieldName, String dateType) {
        // Supported date formats
        String[] datePatterns = {"yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "yyyyMMdd", "ddMMyyyy",
                "dd.MM.yyyy", "yyyy.MM.dd", "dd/MM/yyyy", "yyyy", "MM", "dd"};

        // Parse and match exact dates
        for (String pattern : datePatterns) {
            try {
                switch (dateType) {
                    case "Date" -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                        Date parsedDate = dateFormat.parse(searchQuery);
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.function("DATE", Date.class, root.get(fieldName)), parsedDate));
                        return;
                    }
                    case "LocalDate" -> {
                        LocalDate parsedDate = LocalDate.parse(searchQuery, DateTimeFormatter.ofPattern(pattern));
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedDate));
                        return;
                    }
                    case "LocalDateTime" -> {
                        LocalDateTime parsedDateTime = LocalDateTime.parse(searchQuery, DateTimeFormatter.ofPattern(pattern));
                        predicates.add(criteriaBuilder.equal(root.get(fieldName), parsedDateTime));
                        return;
                    }
                }
            } catch (ParseException | DateTimeParseException ignored) {}
        }

        // Partial matching for year/month/day if no exact match
        try {
            int numericQuery = Integer.parseInt(searchQuery);
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get(fieldName)), numericQuery),
                    criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get(fieldName)), numericQuery),
                    criteriaBuilder.equal(criteriaBuilder.function("DAY", Integer.class, root.get(fieldName)), numericQuery)
            ));
        } catch (NumberFormatException ignored) {}
    }
}