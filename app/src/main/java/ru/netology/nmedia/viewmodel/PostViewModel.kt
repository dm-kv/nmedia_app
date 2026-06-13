package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.Long
import kotlin.concurrent.thread


private val empty = Post(
    id = 0,
    author = "",
    published = 0,
    content = "",
    likes = 0,
    shares = 0,
    likedByMe = false,
    video = null,
)
class PostViewModel(application: Application): AndroidViewModel(application) {


    private val draft_message = MutableLiveData<String?>()
    val draftMessage: LiveData<String?> = draft_message

    fun saveDraft(message: String) {
        draft_message.value = message
    }

    fun clearDraft() {
        draft_message.value = null
    }

    private val repository: PostRepository = PostRepositoryNetworkImpl()
    private val _data = MutableLiveData(FeedModel())

    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun likeById(id: Long) {
        thread {
            try {
                val updatedPost = repository.likeById(id)
                val currentData = _data.value

                if (currentData != null) {
                    val updatedPosts: List<Post> = currentData.posts.map { post ->
                        (if (post.id == id) updatedPost else post) as Post
                    }
                    _data.postValue(
                        currentData.copy(
                            posts = updatedPosts,
                            needsReload = true
                        )
                    )
                }
            } catch (e: Exception) {
                loadPosts()
                e.printStackTrace()
            }
        }
    }
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun saveContent(content: String) {
        thread {
            try {
                edited.value?.let { post ->
                    val trimmed = content.trim()
                    if (post.content != trimmed) {
                        val result = repository.save(post.copy(content = trimmed))
                        println(result)

                        _postCreated.postValue(Unit)
                        edited.postValue(empty)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))

            _data.postValue(try {
                val posts: List<Post> = repository.get()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (_: Exception) {
                FeedModel(error = true)
            })
        }
    }
}

