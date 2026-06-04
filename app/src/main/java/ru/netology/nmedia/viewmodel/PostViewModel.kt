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
import kotlin.concurrent.thread

private val empty = Post()
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

    init {
        loadPosts()
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun saveContent(content: String) {
        edited.value?.let { post ->
            val trimmed = content.trim()

            if (post.content != trimmed) {
                repository.save(
                    post.copy(content = trimmed)
                )
            }
            edited.value = empty
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

