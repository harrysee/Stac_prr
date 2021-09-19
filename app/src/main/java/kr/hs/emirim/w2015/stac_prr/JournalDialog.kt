package kr.hs.emirim.w2015.stac_prr

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.HandlerCompat.postDelayed
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.journal_dialog.view.*
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalAdapter
import java.util.logging.Handler

class JournalDialog(private val context: Context) {

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }
    private val view: View by lazy {
        View.inflate(context, R.layout.journal_dialog, null)
    }
    private var dialog: AlertDialog? = null

    // 터치 리스너 구현
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { v, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            dismiss()
        }
        false
    }

    fun setTitle(@StringRes titleId: Int): JournalDialog {
        view.journal_title.text = context.getText(titleId)
        return this
    }

    fun setMessage(@StringRes msgId:Int) : JournalDialog{
        view.journal_content.text = context.getText(msgId)
        return this
    }

    fun setMessage(msg : CharSequence):JournalDialog{
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

    fun setDeleteBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)):JournalDialog{
        view.delete_btn.apply {
            this.text = btntxt
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setEditBtn(@StringRes btnId : Int, listener: (view: View) -> (Unit)):JournalDialog{
        view.edit_btn.apply {
            this.text = context.getText(btnId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setEditBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)):JournalDialog{
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