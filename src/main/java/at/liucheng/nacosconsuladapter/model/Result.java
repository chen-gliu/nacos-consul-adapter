package at.liucheng.nacosconsuladapter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * 结果封装类
 *
 * @author lc
 * @Date 2021-6-1
 */
@Getter
@AllArgsConstructor
public class Result<T> {
    private T data;
    private long changeIndex;
}