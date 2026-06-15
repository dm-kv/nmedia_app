package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {


    fun get(): List<Post>
    fun likeById(id: Long, likedByMe: Boolean, callback: PostCallback<Post>)
    fun shareById(id: Long)
    fun removeById(id: Long, callback: PostCallback<Unit>)
    fun save(post: Post, callback: PostCallback<Post>)


    fun getAsync(callback: PostCallback<List<Post>>)


    interface PostCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: Throwable)
    }
}