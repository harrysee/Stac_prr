package kr.hs.emirim.w2015.stac_prr.Dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.journal_dialog.view.*
import kr.hs.emirim.w2015.stac_prr.R

class JournalDialog(private val context: Context) {

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }
    private val view: View by lazy {
        View.inflate(context, R.layout.journal_dialog, null)
    }
    private var dialog: AlertDialog? = null

    // 터치 리스너 구현
    private val onTouchListener = View.OnTouchListener { v, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            android.os.Handler().postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    fun setImg(imgresourse: String?): JournalDialog {
        if (imgresourse !=null){
            Glide.with(context)
                .load(imgresourse)
                .into(view.journal_dialog_img)
        }
        return this
    }

    fun setTitle(@StringRes titleId: Int): JournalDialog {
        view.journal_title.text = context.getText(titleId)
        return this
    }
    fun setTitle(title : CharSequence): JournalDialog {
        view.journal_title.text = title
        return this
    }

    fun setMessage(@StringRes msgId:Int) : JournalDialog {
        view.journal_content.text = context.getText(msgId)
        return this
    }

    fun setMessage(msg : CharSequence): JournalDialog {
        view.journal_content.text = msg
        return this
    }

    fun setDeleteBtn(@StringRes btnId : Int, listener: (view: View) -> (Unit)): JournalDialog {
        view.delete_btn.apply {
            this.text = context.getText(btnId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setDeleteBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)): JournalDialog {
        view.delete_btn.apply {
            this.text = btntxt
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setEditBtn(@StringRes btnId : Int, listener: (view: View) -> (Unit)): JournalDialog {
        view.edit_btn.apply {
            this.text = context.getText(btnId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setEditBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)): JournalDialog {
        view.edit_btn.apply {
            this.text = btntxt
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun create(){
        dialog = builder.create()
    }

    fun show(){
        dialog = builder.create()
        dialog?.show()
        val window: Window? = dialog?.window
        dialog?.window?.setLayout(860, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun dismiss(){
        dialog?.dismiss()
    }

}