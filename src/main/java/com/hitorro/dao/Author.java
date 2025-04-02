package com.hitorro.dao;

import java.util.List;

public record Author(String author, List<Book> books) {
}
