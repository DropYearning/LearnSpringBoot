package com.example.elasticsearch.repository;

import com.example.elasticsearch.bean.Book;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

// 范型<要搜索的数据封装类， 主键类型>
public interface BookRepository extends ElasticsearchRepository<Book, Integer> {

    // 按照书名模糊查询（需要方法名匹配命名规范）
    public List<Book> findByBookNameLike(String bookName);

}
