package com.ssafy.indive.view.mystudio

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.indive.MainViewModel
import com.ssafy.indive.MoreDialogFragment
import com.ssafy.indive.R
import com.ssafy.indive.base.BaseFragment
import com.ssafy.indive.databinding.FragmentMyStudioBinding
import com.ssafy.indive.model.dto.Notice
import com.ssafy.indive.model.response.MusicDetailResponse
import com.ssafy.indive.utils.USER
import com.ssafy.indive.view.genre.genrelist.GenreListAdapter
import com.ssafy.indive.view.genre.genrelist.GenreListFragmentDirections
import com.ssafy.indive.view.login.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MyStudioFragment"

@AndroidEntryPoint
class MyStudioFragment : BaseFragment<FragmentMyStudioBinding>(R.layout.fragment_my_studio) {
    private val memberViewModel: MemberViewModel by viewModels()
    private val myStudioViewModel: MyStudioViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var notice: String = ""
    private var listener: (String) -> (Unit) = {
        memberViewModel.writeNotice(sharedPreferences.getLong(USER, 0), Notice(it))
    }

    override fun init() {
        binding.apply {
            memberVM = memberViewModel
            myStudioVM = myStudioViewModel
        }

        initClickListener()
        initViewModel()
        initViewModelCallback()
        initMyMusicList()
    }

    private fun initMyMusicList() {
        val playListener: (MusicDetailResponse) -> (Unit) = {
            mainViewModel.insert(it.musicSeq)
            mainViewModel.successGetEvent=it.musicSeq
        }

        val moreListener: (MusicDetailResponse) -> (Unit) = {
            MoreDialogFragment(object : MoreDialogFragment.MoreDialogClickListener {
                override fun clickDetail() {
                    val action = GenreListFragmentDirections.actionGenreListFragmentToSongDetailFragment2(it.musicSeq)
                    findNavController().navigate(action)
                }

                override fun clickStudio() {
                    findNavController().navigate(R.id.action_genreListFragment_to_userStudioFragment)
                }

                override fun clickReport() {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("http://pf.kakao.com/_lxeAjxj")
                    startActivity(i)
                }

            }).show(requireActivity().supportFragmentManager, "MoreDialog")
        }
        binding.rvMyMusic.adapter = GenreListAdapter(playListener, moreListener)
    }

    private fun initViewModel() {
        memberViewModel.memberDetail(sharedPreferences.getLong(USER, 0))
        myStudioViewModel.getMusicList()
    }

    private fun initViewModelCallback() {
        memberViewModel.notice.observe(viewLifecycleOwner) {
            if (it != null) {
                notice = it
            }

        }
        memberViewModel.noticeSuccess.observe(viewLifecycleOwner) {
            initViewModel()
        }
    }

    private fun initClickListener() {
        binding.apply {
            btnEditProfile.setOnClickListener {
                findNavController().navigate(R.id.action_myStudioFragment_to_editProfileFragment)
            }
            btnAddsong.setOnClickListener {
                findNavController().navigate(R.id.action_myStudioFragment_to_addSongFirstFragment)
            }
            btnRanking.setOnClickListener {
                findNavController().navigate(R.id.action_myStudioFragment_to_rankingFragment)
            }
            ivAddNft.setOnClickListener {
                val action = MyStudioFragmentDirections.actionMyStudioFragmentToAddRewardFragment(0)
                findNavController().navigate(action)
            }
            ivEditNotice.setOnClickListener {
                NoticeDialog(notice, listener).show(parentFragmentManager, "")
            }
        }

    }
}