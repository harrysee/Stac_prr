package kr.hs.emirim.w2015.stac_prr

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.journal_dialog.view.*

class ImageDialog (private val context: Context) {
    private val builder: android.app.AlertDialog.Builder by lazy {
        android.app.AlertDialog.Builder(context).setView(view)
    }
    private val view: View by lazy {
        View.inflate(context, R.layout.journal_dialog, null)
    }
    private var dialog: android.app.AlertDialog? = null

    // 터치 리스너 구현
    private val onTouchListener = View.OnTouchListener { v, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            android.os.Handler().postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    fun setImg(imgresourse: String?): ImageDialog {
        if (imgresourse != null) {
            Glide.with(context)
                .load(imgresourse)
                .into(view.journal_dialog_img)
        }
        return this
    }

    fun create() {
        dialog = builder.create()
    }

    fun show() {
        dialog = builder.create()
        dialog?.show()
        val window: Window? = dialog?.window
        dialog?.window?.setLayout(860, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}