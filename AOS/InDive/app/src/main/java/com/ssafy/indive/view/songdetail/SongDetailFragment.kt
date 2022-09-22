package com.ssafy.indive.view.songdetail

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.indive.R
import com.ssafy.indive.base.BaseFragment
import com.ssafy.indive.databinding.FragmentSongDetailBinding
import com.ssafy.indive.model.dto.Comment
import com.ssafy.indive.view.home.RecentMusicAdapter

class SongDetailFragment : BaseFragment<FragmentSongDetailBinding>(R.layout.fragment_song_detail) {

    private val songDetailViewModel: SongDetailViewModel by viewModels()

    override fun init() {
        binding.songdetailVM = songDetailViewModel
        initCommentList()
        initClickListener()

    }

    private fun initCommentList() {
        songDetailViewModel.getComments()
        binding.rvComment.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvComment.adapter = CommentAdapter(object : CommentAdapter.CommentCLickListener {
            override fun clickEdit(comment: Comment) {
                showToast("수정")
            }

            override fun clickRemove(commentSeq: Long) {
                showToast("삭제")

            }

            override fun clickReport(commentSeq: Long) {
                showToast("신고")

            }

        })
    }

    private fun initClickListener() {
        binding.tvDetailLyricsExpand.setOnClickListener {
            binding.clSongLyricsChild.visibility = View.VISIBLE
            binding.tvDetailLyricsExpand.visibility = View.GONE
        }

        binding.tvDetailLyricsFold.setOnClickListener {
            binding.clSongLyricsChild.visibility = View.GONE
            binding.tvDetailLyricsExpand.visibility = View.VISIBLE
        }
    }
}