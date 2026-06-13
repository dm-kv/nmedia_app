package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import android.view.View
import ru.netology.nmedia.databinding.CardPostBinding


interface PostListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onVideo(post: Post)
    fun onContent(post: Post)
}

fun checkTheDigit(digit: Int,) = when(digit) {
    in 0..999 -> digit.toString()
    in 1000..9999 -> (digit.toDouble() / 1000).toBigDecimal().setScale(1, RoundingMode.DOWN).toString() + "K"
    in 10000..999999 -> (digit / 1000).toString() + "K"
    else -> (digit.toDouble() / 1000000).toBigDecimal().setScale(1, RoundingMode.DOWN).toString() + "M"
}

class PostsAdapter(
    private val listener: PostListener,
    ): ListAdapter<Post, PostViewHolder>(PostDiffCallback)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding,listener,)
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        val post = getItem(position)
        viewHolder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener,
    ): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with (binding) {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = checkTheDigit(post.likes)
            share.text = checkTheDigit(post.shares)

            if (post.video == null) {
                binding.playVideoGroup.visibility = View.GONE
            } else {
                binding.playVideoGroup.visibility = View.VISIBLE
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_post)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
            like.setOnClickListener {
                listener.onLike(post)
            }
            share.setOnClickListener {
                listener.onShare(post)
            }
            play.setOnClickListener {
                listener.onVideo(post)
            }
            backgroundVideo.setOnClickListener {
                listener.onVideo(post)
            }
            content.setOnClickListener {
                listener.onContent(post)
            }
        }
    }
}
object PostDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(p0: Post, p1: Post) = p0 ==p1
}


