package com.ssafy.indive.view.player

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.indive.R
import com.ssafy.indive.databinding.BottomSheetPlayerBinding
import com.ssafy.indive.utils.PLAYER
import com.ssafy.indive.utils.PLAY_LIST


class PlayerBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPlayerBinding
    private var mode = PLAYER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheet()
        initClickListener()

    }

    private fun initClickListener() {
        binding.btnPlayerList.setOnClickListener {
            if (mode == PLAYER) {
                binding.groupPlayList.visibility = View.VISIBLE
                binding.groupPlayer.visibility = View.GONE
                binding.groupSeekbar.visibility = View.GONE
                mode = PLAY_LIST
            } else {
                binding.groupPlayList.visibility = View.GONE
                binding.groupPlayer.visibility = View.VISIBLE
                binding.groupSeekbar.visibility = View.VISIBLE
                mode = PLAYER
            }
        }
    }

    private fun initBottomSheet() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight()
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    companion object {
        const val TAG = "BottomSheetFragment"
    }
}