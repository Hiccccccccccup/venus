package com.jozz.venus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public class ESResult {
    /* 文档数据量 */
    private final Integer total;
    /* 结果序列化文本 */
    private final String content;

    public static ESResult of(Integer total, String content) {
        return new ESResult(total, content);
    }

}
