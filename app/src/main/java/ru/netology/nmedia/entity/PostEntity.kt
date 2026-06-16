package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val published: Long = 0,
    val content: String = "",
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean = false,
    val video: String? = null,
) {
    fun toPost(): Post = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        published = published,
        content = content,
        likes = likes,
        shares = shares,
        likedByMe = likedByMe,
        video = video,
    )

    companion object {
        fun fromPost(post: Post): PostEntity = with(post) {
            PostEntity(
                id = id,
                author = author,
                authorAvatar = authorAvatar,
                published = published,
                content = content,
                likes = likes,
                shares = shares,
                likedByMe = likedByMe,
                video = video,
            )
        }
    }
}