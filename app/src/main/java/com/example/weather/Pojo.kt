package com.example.weather

data class Pojo(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)