package com.example.mymoviesapp.interfaces

import android.app.Dialog
import android.app.DownloadManager
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity.DOWNLOAD_SERVICE
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.R.anim.fade_in
import com.example.mymoviesapp.R.anim.fade_out
import com.example.mymoviesapp.R.string.app_name
import com.example.mymoviesapp.R.string.copied_to_clipboard
import com.example.mymoviesapp.R.string.error_download_image
import com.example.mymoviesapp.R.string.init_download_image
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.APIClient.IMAGE_PATH_ORIGINAL
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File.separator

object MovieConstants {
    const val POPULAR_ID = -1
    const val LOADING_ID = -2

    const val NORMAL_DISPLAY = -3
    const val LOADING_DISPLAY = -4
    const val LOOK_MORE_DISPLAY = -5

    const val SEE_MOVIES_BY_GENRE = -6
    const val SEE_RECOMMENDED_MOVIES = -7
    const val SEE_MOVIE_CAST = -8

    const val SEE_MORE_TYPE_KEY = "see_more_type_key"
    const val SEE_MORE_ID_KEY = "see_more_id_key"
    const val SEE_MORE_TITLE_KEY = "see_more_title_key"
    const val SEE_MORE_SUBTITLE_KEY = "see_more_subtitle_key"
    const val DETAILS_KEY = "details_key"

    fun View.show(visibility: Int, context: Context) {

        val isVisible: Boolean = this.visibility == VISIBLE
        if (!isVisible && visibility == VISIBLE) {
            this.visibility = VISIBLE
            val fadeIn =
                AnimationUtils.loadAnimation(context, fade_in)
            this.startAnimation(fadeIn)

        } else if (isVisible && visibility != VISIBLE) {
            val fadeOut =
                AnimationUtils.loadAnimation(context, fade_out)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    this@show.visibility = visibility
                }
            })
            this.startAnimation(fadeOut)
        }
    }

    fun openDialog(
        context: Context,
        imagePath: String,
        title: String = "",
        subtitle: String = ""
    ): Dialog {
        val detailsDialog = Dialog(context)
        detailsDialog.setContentView(R.layout.dialog_poster)

        val dialogImage: AppCompatImageView = detailsDialog.findViewById(R.id.ivDialogImage)

        if (title.isNotEmpty()) {
            val dialogTitle: AppCompatTextView = detailsDialog.findViewById(R.id.tvDialogTitle)
            dialogTitle.text = title
            dialogTitle.visibility = VISIBLE
        }

        if (subtitle.isNotEmpty()) {
            val dialogSubtitle: AppCompatTextView =
                detailsDialog.findViewById(R.id.tvDialogSubtitle)
            dialogSubtitle.text = subtitle
            dialogSubtitle.visibility = VISIBLE
        }

        Picasso.get()
            .load(APIClient.IMAGE_PATH_W780 + imagePath)
            .into(dialogImage, object : Callback {
                override fun onSuccess() {
                    dialogImage.visibility = VISIBLE
                    detailsDialog
                        .findViewById<ShimmerFrameLayout>(R.id.dialogLoadingLayout)
                        .visibility = View.GONE
                    detailsDialog
                        .findViewById<AppCompatImageView>(R.id.ivDialogImageNotFound)
                        .visibility = View.GONE
                }

                override fun onError(e: java.lang.Exception?) {
                    dialogImage.visibility = View.GONE
                    detailsDialog
                        .findViewById<ShimmerFrameLayout>(R.id.dialogLoadingLayout)
                        .visibility = View.GONE
                    detailsDialog
                        .findViewById<AppCompatImageView>(R.id.ivDialogImageNotFound)
                        .visibility = VISIBLE
                }
            })

        if (title.isNotEmpty() && subtitle.isNotEmpty()) {
            val btnOptions: AppCompatImageButton = detailsDialog.findViewById(R.id.btnDialogOptions)
            btnOptions.visibility = VISIBLE
            btnOptions.setOnClickListener {
                Log.i("ññ", "DialogClick")
                val popup = PopupMenu(context, it)

                popup.inflate(R.menu.dialog_menu)

                popup.setOnMenuItemClickListener { menu ->
                    when (menu.itemId) {
                        R.id.menuDialogCopyName -> {
                            copyToClipboard(context, title)
                        }

                        R.id.menuDialogCopyCharacter -> {
                            copyToClipboard(context, subtitle)
                        }

                        R.id.menuDialogSavePhoto -> {
                            downloadImage(context, imagePath, "$title Photo")
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        }

        detailsDialog.show()

        return detailsDialog
    }

    fun downloadImage(context: Context, imagePath: String, imageName: String): Boolean {
        try {
            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val posterURL = Uri.parse(IMAGE_PATH_ORIGINAL + imagePath)

            val appName = context.getString(app_name)
            val imageFileName = imageName.lowercase().replace(" ", "_") + ".jpg"
            val imageFileDir = separator + appName + separator + imageFileName

            val request = DownloadManager.Request(posterURL)
            request.setAllowedNetworkTypes(NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setTitle(imageName)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(DIRECTORY_PICTURES, imageFileDir)

            downloadManager.enqueue(request)
            Toast.makeText(context, context.getString(init_download_image), LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, context.getString(error_download_image), LENGTH_SHORT).show()
        }
        return true
    }

    fun copyToClipboard(context: Context, text: String): Boolean {
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText("text", text)

        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, context.getString(copied_to_clipboard), LENGTH_SHORT).show()

        return true
    }
}