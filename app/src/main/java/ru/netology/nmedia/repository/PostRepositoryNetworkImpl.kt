package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post


class PostRepositoryNetworkImpl: PostRepository {

    override fun get(): List<Post> {
        return PostApi.service.get()
            .execute()
            .let {
                if (it.isSuccessful) {
                    it.body() ?: throw RuntimeException("body is null")
                } else {
                    throw RuntimeException(it.message())
                }

            }
    }

    override fun getAsync(callback: PostRepository.PostCallback<List<Post>>) {
        PostApi.service.get().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        404 -> callback.onError(RuntimeException("Post not found"))
                        500 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return
                }

                val body = response.body()
                if (body == null) {
                    callback.onError(RuntimeException("body is null"))
                } else {
                    callback.onSuccess(body)
                }
            }

            override fun onFailure(call: Call<List<Post>>, e: Throwable) {
                callback.onError(e as Exception)
            }
        })
    }


    override fun likeById(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.PostCallback<Post>
    ) {
        val call = if (likedByMe) {
            PostApi.service.dislikeById(id)
        } else {
            PostApi.service.likeById(id)
        }

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        404 -> callback.onError(RuntimeException("Post not found"))
                        500 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return
                }

                val body = response.body()
                if (body == null) {
                    callback.onError(RuntimeException("body is null"))
                } else {
                    callback.onSuccess(body)
                }
            }

            override fun onFailure(call: Call<Post>, e: Throwable) {
                callback.onError(e as Exception)
            }
        })
    }

    override fun shareById(id: Long) {

    }

    override fun save(post: Post, callback: PostRepository.PostCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        when (response.code()) {
                            404 -> callback.onError(RuntimeException("Post not found"))
                            500 -> callback.onError(RuntimeException("Server error"))
                            else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                        }
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        callback.onError(RuntimeException("body is null"))
                    } else {
                        callback.onSuccess(body)
                    }
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e as Exception)
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.PostCallback<Unit>) {
        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        when (response.code()) {
                            404 -> callback.onError(RuntimeException("Post not found"))
                            500 -> callback.onError(RuntimeException("Server error"))
                            else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                        }
                        return
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, e: Throwable) {
                    callback.onError(e as Exception)
                }
            })
    }
}
