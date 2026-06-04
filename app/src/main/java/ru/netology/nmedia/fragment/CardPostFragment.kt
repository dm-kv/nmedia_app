package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.utils.LongArg
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.NewPostFragment.Companion.contentArg
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue


class CardPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        val binding = FragmentCardPostBinding.inflate(layoutInflater)
        val listener = object : PostListener {
            
            override fun onContent(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_cardPostFragment,
                    Bundle().apply {
                        idArg = post.id
                    }
                )
            }
            
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_cardPostFragment_to_newPostFragment2,
                    Bundle().apply {
                        contentArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigate(R.id.action_cardPostFragment_to_feedFragment2)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                val chooser =
                    Intent.createChooser(intent, getString(R.string.description_post_share))
                startActivity(chooser)
                viewModel.shareById(post.id)
            }

            override fun onVideo(post: Post) {
                val intentVideo = Intent(Intent.ACTION_VIEW, post.video?.toUri())
                startActivity(intentVideo)
            }
        }

        val holder = PostViewHolder(binding.cardPost, listener)
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post = state.posts.find { it.id == arguments?.idArg }
            if (post != null) {
                holder.bind(post)
            } else {
                "Post not found"
            }
        }
        return binding.root
    }

    companion object {
        var Bundle.idArg by LongArg
    }
}