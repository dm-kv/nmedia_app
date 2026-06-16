package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val published: Long,
    val content: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean = false,
    val video: String? = null,
)

