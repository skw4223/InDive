package com.ssafy.indive.view.rewardstore.addreward

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ssafy.indive.R
import com.ssafy.indive.base.BaseFragment
import com.ssafy.indive.databinding.FragmentAddRewardBinding
import com.ssafy.indive.utils.resizeBitmapFormUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class AddRewardFragment : BaseFragment<FragmentAddRewardBinding>(R.layout.fragment_add_reward) {

    lateinit var imgFile : MultipartBody.Part
    private val addRewardViewModel by viewModels<AddRewardViewModel>()
    private val args by navArgs<AddRewardFragmentArgs>()

    override fun init() {
        initClickListener()

        initViewModelCallback()
    }

    private fun initClickListener(){
        binding.btnAddImg.setOnClickListener {
            getImage()
        }
        binding.tvImageText.setOnClickListener {
            getImage()
        }
        binding.btnAddReward.setOnClickListener {
            addRewardViewModel.createNFT(imgFile)
        }
    }

    private fun initViewModelCallback(){
        addRewardViewModel.successMsgEvent.observe(viewLifecycleOwner){
            showToast(it)
        }
    }

    private fun getImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra("crop","true")
        photoResultLauncher.launch(intent)
    }

    private val photoResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {

            binding.btnAddImg.setImageURI(it.data?.data)
            binding.btnAddImg.alpha = 1.0F
            binding.tvImageText.visibility = View.INVISIBLE

            var bitMap : Bitmap? = null

            val uri = it.data?.data
            try{
                if(uri != null){
                    bitMap = resizeBitmapFormUri(uri,requireContext())
                    createMultiPart(bitMap!!)
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

        }
    }

    private fun createMultiPart(bitmap: Bitmap) {
        var imageFile: File? = null
        try {
            imageFile = createFileFromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile!!)
        imgFile = MultipartBody.Part.createFormData("imgFile", imageFile!!.name, requestFile)
    }

    @Throws(IOException::class)
    private fun createFileFromBitmap(bitmap: Bitmap): File {
        val newFile = File(requireActivity().filesDir, "crew_${System.currentTimeMillis()}")
        val fileOutputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)
        fileOutputStream.close()
        return newFile
    }
}