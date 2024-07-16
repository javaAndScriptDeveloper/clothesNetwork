package com.example.company.service.impl;

import com.example.company.model.search.SearchRequest;
import com.example.company.service.SearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final EntityManager entityManager;

    @Override
    public <T> List<T> search(SearchRequest<T> searchRequest) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(searchRequest.getClazz());
        var root = criteriaQuery.from(searchRequest.getClazz());

        var predicate = generatePredicate(root, criteriaBuilder, searchRequest.getFilteringFields());
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private <T> Predicate generatePredicate(
            Root<T> root, CriteriaBuilder criteriaBuilder, List<SearchRequest.FilteringField> filteringFields) {
        return filteringFields.stream()
                .map(filteringField -> {
                    var path = generatePath(root, filteringField);
                    return switch (filteringField.getOperator()) {
                        case EQUALS -> criteriaBuilder.equal(path, filteringField.getFieldValue());
                    };
                })
                .reduce(criteriaBuilder::and)
                .orElse(criteriaBuilder.conjunction());
    }

    private <T> Path<?> generatePath(Root<T> root, SearchRequest.FilteringField filteringField) {
        var fieldName = filteringField.getFieldName();
        return Arrays.stream(fieldName.split("\\.")).reduce((Path<?>) root, Path::get, (p1, p2) -> p1);
    }
}
