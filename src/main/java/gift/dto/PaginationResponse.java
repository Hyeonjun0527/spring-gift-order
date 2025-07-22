package gift.dto;

import java.util.List;

public record PaginationResponse<T>(
    List<T> content,
    int totalPages,
    int currentPages,
    int pageSize,
    long totalElements
) {

}
