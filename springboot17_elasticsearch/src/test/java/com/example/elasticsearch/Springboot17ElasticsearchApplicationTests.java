package com.example.elasticsearch;

import com.example.elasticsearch.bean.Book;
import com.example.elasticsearch.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Springboot17ElasticsearchApplicationTests {

    @Autowired
    BookRepository bookRepository;

    // 测试在es中保存一个BOOK对象
    @Test
    void contextLoads() {

        Book book = new Book();
        book.setId(2);
        book.setAuthor("吴承恩");
        book.setBookName("西游记");
        //book.setAuthor("我也不知道谁写的");
        //book.setBookName("西厢记");

        bookRepository.index(book);
    }

    // 测试findByBookNameLike
    @Test
    public void test1(){
        for (Book book : bookRepository.findByBookNameLike("西")) {
            System.out.println(book);
        }
    }

}
