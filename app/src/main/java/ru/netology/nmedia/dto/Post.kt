package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val published: String = "",
    val content: String = "",
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean = false,
    val video: String? = null,
)

