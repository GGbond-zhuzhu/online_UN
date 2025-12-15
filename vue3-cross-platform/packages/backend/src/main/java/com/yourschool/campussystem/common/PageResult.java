package com.yourschool.campussystem.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "分页响应结果")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "当前页码", example = "1")
    private Integer page;

    @Schema(description = "每页大小", example = "10")
    private Integer size;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "10")
    private Integer totalPages;

    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    public PageResult() {}

    public PageResult(List<T> list, Integer page, Integer size, Long total) {
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.hasNext = page < this.totalPages;
        this.hasPrevious = page > 1;
    }

    public static <T> PageResult<T> of(List<T> list, Integer page, Integer size, Long total) {
        return new PageResult<>(list, page, size, total);
    }
}