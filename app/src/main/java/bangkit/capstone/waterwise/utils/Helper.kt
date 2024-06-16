package bangkit.capstone.waterwise.utils

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import bangkit.capstone.waterwise.R

object Helper {
    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    fun dialogBuilder (context: Context, layout: Int, isCancelable: Boolean = false): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(isCancelable)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layout)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = android.R.transition.fade
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    fun infoDialog(
        context: Context,
        message: String,
        alignment: Int = Gravity.CENTER
    ): Dialog {
        val dialog = dialogBuilder(context, R.layout.dialog_info_layout, false)
        val tvMessage = dialog.findViewById<TextView>(R.id.info_dialog_message)
        val closButton = dialog.findViewById<TextView>(R.id.close_info_dialog_btn)

        tvMessage.text = message
        tvMessage.gravity = alignment

        closButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun loadingDialog (context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loader_dialog)
        dialog.setCancelable(false)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }
}