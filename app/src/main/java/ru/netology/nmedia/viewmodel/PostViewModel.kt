package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.utils.SingleLiveEvent



private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
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
        val currentState = _data.value ?: return
        val posts = currentState.posts

        val post = posts.find { it.id == id } ?: return
        val likedByMe = post.likedByMe

        repository.likeById(id, likedByMe, object : PostRepository.PostCallback<Post> {
            override fun onSuccess(result: Post) {
                val refreshState = _data.value ?: return
                val updatedPosts = refreshState.posts.map {
                    if (it.id == result.id) result else it
                }
                _data.postValue(refreshState.copy(posts = updatedPosts))
            }

            override fun onError(error: Throwable) {
                _data.postValue(currentState)
            }
        })
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) {
        val currentState = _data.value ?: return
        _data.postValue(currentState.copy(posts = currentState.posts.filter { it.id != id }))

        repository.removeById(id, object : PostRepository.PostCallback<Unit> {
            override fun onSuccess(result: Unit) {
            }

            override fun onError(error: Throwable) {
                _data.postValue(currentState)
            }
        })
    }

    fun save(content: String) {
        edited.value?.let {
            repository.save(it.copy(content = content), object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {

                    _postCreated.postValue(Unit)
                }

                override fun onError(error: Throwable) {
                    _data.value
                }
            })
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }


    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAsync(object : PostRepository.PostCallback<List<Post>>{
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(error: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
}

