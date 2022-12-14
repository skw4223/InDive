package com.ssafy.indive.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.indive.R
import com.ssafy.indive.model.dto.Music
import com.ssafy.indive.model.dto.SearchArtist
import com.ssafy.indive.model.response.MusicDetailResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

// 다이얼로그 사이즈 조절
fun Context.dialogResize(dialogFragment: DialogFragment, width: Float, height: Float) {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val display = windowManager.defaultDisplay
    val size = Point()

    display.getSize(size)

    val params: ViewGroup.LayoutParams? = dialogFragment.dialog?.window?.attributes
    val deviceWidth = size.x
    val deviceHeight = size.y

    params?.width = (deviceWidth * width).toInt()
    params?.height = (deviceHeight * height).toInt()
    dialogFragment.dialog?.window?.attributes = params as WindowManager.LayoutParams
    dialogFragment.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

}

fun setArtist(list: List<MusicDetailResponse>): List<SearchArtist> {

    val hashSet = HashSet<Pair<Long, String>>()

    list.forEach {
        hashSet.add(Pair(it.artist.memberSeq,it.artist.nickname))
    }

    val mutableList = mutableListOf<SearchArtist>()

    hashSet.forEach {
        mutableList.add(SearchArtist(it.first,it.second))
    }

    return mutableList.toList()
}

// 서버 시간 포매터
fun timeFormatter(time: Long?): String {
    if (time == null) {
        return ""
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    return dateFormat.format(time)
}

// 러닝 제목 포매터
fun timeNameFormatter(time: Long?): String {
    if (time == null) {
        return ""
    }
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 러닝")

    return dateFormat.format(time)
}

// device size
fun getDeviceSize(activity: Activity): Point {
    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return windowManager.currentWindowMetricsPointCompat()
}

fun WindowManager.currentWindowMetricsPointCompat() : Point {
    // R(30) 이상
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsets = currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(
                insets,
                Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
            )
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(
            currentWindowMetrics.bounds.width() - insetsWidth,
            currentWindowMetrics.bounds.height() - insetsHeight
        )
    } else {
        Point().apply {
            defaultDisplay.getSize(this)
        }
    }
}
