package com.example.demo.src;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class Criteria {
    private Integer pageNum;
    private Integer size;

    public Criteria() {
        this.pageNum = 1;
        this.size = 10;
    }

    public Criteria(Integer pageNum, Integer size) {
        this.pageNum = pageNum;
        this.size = size;
    }


}
