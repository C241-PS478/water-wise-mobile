package bangkit.capstone.waterwise.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import bangkit.capstone.waterwise.R

@SuppressLint("InflateParams")
@Suppress("DEPRECATION")
class CustomToast(context: Context) {
    private val mContext = context
    private var duration: Int = Toast.LENGTH_LONG
    private var type: Const.ToastType = Const.ToastType.SUCCESS
    private var gravity: Int = Gravity.TOP
    private lateinit var message: String

    private val customToast = Toast(context)

    init {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_layout, null)

        customToast.view = layout
        this.setType(type)
        this.setDuration(duration)
        this.setGravity(gravity)
    }

    fun show() {
        if(!this::message.isInitialized) {
            Log.e("CustomToast", "Toast message is not set!")
            throw Exception("Toast message is not set!")
        }
        customToast.show()
    }

    fun setType(type: Const.ToastType = this.type) {
        this.type = type
        val toastIconComponent = customToast.view!!.findViewById<ImageView>(R.id.toast_icon)

        when (type) {
            Const.ToastType.SUCCESS -> {
                toastIconComponent.setImageResource(R.drawable.ic_success)
                setToastBackground(getResColor(R.color.toast_success))
            }
            Const.ToastType.ERROR -> {
                toastIconComponent.setImageResource(R.drawable.ic_failed)
                setToastBackground(getResColor(R.color.toast_error))
            }
            Const.ToastType.WARNING -> {
                toastIconComponent.setImageResource(R.drawable.ic_info)
                setToastBackground(getResColor(R.color.toast_warning))
            }
        }
    }

    fun setDuration(duration: Int = this.duration) {
        customToast.setDuration(duration)
    }

    fun setGravity(gravity: Int = this.gravity , xOffset: Int = 0, yOffset: Int = 0) {
        customToast.setGravity(gravity, xOffset, yOffset)
    }

    fun setMessage(message: String) {
        this.message = message
        customToast.view!!.findViewById<TextView>(R.id.toast_message).text = message
    }

    private fun setToastBackground(strokeColor: Int) {
        val bgDrawable = GradientDrawable().apply {
            setColor(getResColor(R.color.white))
            cornerRadius = 20f
            setStroke(2, strokeColor)
        }
        customToast.view?.background = bgDrawable
    }

    private fun getResColor(color: Int): Int {
        return mContext.resources.getColor(color)
    }
}