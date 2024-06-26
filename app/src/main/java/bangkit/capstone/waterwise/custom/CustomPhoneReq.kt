package bangkit.capstone.waterwise.custom

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import bangkit.capstone.waterwise.R

class CustomPhoneReq : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int) : super(context, attrs, defStyleAttrs) {
        init()
    }

    private fun init() {
        filters = arrayOf(InputFilter.LengthFilter(14))  // Set maximum length to 14 characters
        inputType = android.text.InputType.TYPE_CLASS_PHONE  // Set input type to phone

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (!isPhoneValid(s as Editable)) {
                    context.getString(R.string.invalid_phone)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun isPhoneValid(phone: Editable): Boolean {
        return phone.length <= 14 && phone.length > 0 && phone.matches(Regex("^[0-9]*\$"))
    }
}
