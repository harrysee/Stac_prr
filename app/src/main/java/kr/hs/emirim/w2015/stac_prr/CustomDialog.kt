package kr.hs.emirim.w2015.stac_prr

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.custom_dialog.view.*

class CustomDialog(private val context: Context) {

    private val builder:AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }
    private val view : View by lazy {
        View.inflate(context,R.layout.custom_dialog,null)
    }
    private var dialog : AlertDialog? =null

    //터치 리스너 구현
    private val onTouchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            Handler().postDelayed({
                dismiss()
            }, 5)
        }
        false
    }
    
    //메시지 text 넣기
    fun setMessage(@StringRes msgId:Int) : CustomDialog{
        view.messageTextView.text = context.getText(msgId)
        return this
    }
    fun setMessage(msg : CharSequence):CustomDialog{
        view.messageTextView.text = msg
        return this
    }
    
    //부정버튼 text 넣기
    fun setNegativeBtn(@StringRes btnId : Int,listener: (view: View) -> (Unit)):CustomDialog{
        view.negativeButton.apply {
            this.text = context.getText(btnId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }
    fun setNegativeBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)):CustomDialog{
        view.negativeButton.apply {
            this.text = btntxt
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }
    //긍정버튼 text 넣기
    fun setPositiveBtn(@StringRes btnId : Int,listener: (view: View) -> (Unit)):CustomDialog{
        view.positiveButton.apply {
            this.text = context.getText(btnId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }
    fun setPositiveBtn(btntxt : CharSequence,listener: (view: View) -> (Unit)):CustomDialog{
        view.positiveButton.apply {
            this.text = btntxt
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }
    /*fun setsize():CustomDialog{

    }*/
    // 그 외 부가 함수
    fun create(){
        dialog = builder.create()
    }
    fun show(){
        dialog = builder.create()
        //dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //dialog?.window?.setLayout(256,149)
        dialog?.show()
        val window: Window? = dialog?.window
        dialog?.window?.setLayout(860, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    fun dismiss(){
        dialog?.dismiss()
    }
}