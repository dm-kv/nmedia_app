package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PostRepositoryFileImpl(private val context: Context): PostRepository {
    private val gson = Gson()
    private var posts = readPosts()
        set(value) {
            field = value
            sync()
        }
    private var nextId = (posts.maxByOrNull { it.id }?.id ?: 0L) + 1L
    private val data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = if (it.likedByMe) it.likes - 1 else it.likes + 1)
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = ++nextId, author = "DK", published = "29.03.26")) + posts
        } else {
            posts.map {
                if (it.id == post.id) {
                    it.copy(content = post.content)
                } else {
                    it
                }
            }
        }
        data.value = posts
    }

    private fun readPosts(): List<Post> {
        val file = context.filesDir.resolve(FILE_NAME)
        return if (file.exists()) {
            file.reader().buffered().use {
                gson.fromJson(it, postType)
            }
        } else {
            emptyList<Post>()
        }
    }

    private fun sync() {
        val file = context.filesDir.resolve(FILE_NAME)
        file.writer().buffered().use {
            it.write(gson.toJson(posts))
        }
    }

    private companion object {
        const val FILE_NAME = "posts.json"
        val postType:Type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    }
}