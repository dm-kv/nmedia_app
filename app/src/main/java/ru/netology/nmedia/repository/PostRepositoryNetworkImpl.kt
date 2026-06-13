package ru.netology.nmedia.repository


import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.nmedia.dto.Post
import okhttp3.Call
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody

class PostRepositoryNetworkImpl: PostRepository {
    private companion object{
        const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
        val jsonType = "application/json".toMediaType()
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private  val postType = object : TypeToken<List<Post>>()  {}.type

    override fun get(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts")
            .build()

        val call:Call = client.newCall(request)

        val response = call.execute()

        return gson.fromJson(response.body.string(), postType)
    }

    override fun likeById(id: Long) {
        val currentPosts = get()
        val post = currentPosts.find { it.id == id }

        if (post?.likedByMe == true) {
            removeLike(id)
        } else {
            addLike(id)
        }
    }


    private fun addLike(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts/$id/likes")
            .post(RequestBody.create(null, ""))
            .build()

        val call: Call = client.newCall(request)
        val response = call.execute()

        return gson.fromJson(response.body?.string(), Post::class.java)
    }

    private fun removeLike(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts/$id/likes")
            .delete()
            .build()

        val call: Call = client.newCall(request)
        val response = call.execute()

        return gson.fromJson(response.body?.string(), Post::class.java)
    }



    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts/$id")
            .delete()
            .build()

        val call:Call = client.newCall(request)

        call.execute()
    }

    override fun save(post: Post): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts")
            .post(
                gson.toJson(post).toRequestBody(
                jsonType),
                )
            .build()

        val call:Call = client.newCall(request)

        val response = call.execute()

        return gson.fromJson(response.body.string(), Post::class.java)
    }
}

