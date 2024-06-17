package bangkit.capstone.waterwise.water_detection.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import bangkit.capstone.waterwise.R
import com.google.android.material.textfield.TextInputLayout

class CustomInputText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val textInputLayout: TextInputLayout
    private val labelTextView: TextView
    private val inputEditText: EditText
    private val unitTextView: TextView
    private val errorTextView: TextView

    init {
        // Inflate the layout
        val view = LayoutInflater.from(context).inflate(R.layout.custom_input_text, this, true)
        textInputLayout = view.findViewById(R.id.inputLayout)
        labelTextView = view.findViewById(R.id.labelTextView)
        inputEditText = view.findViewById(R.id.inputEditText)
        unitTextView = view.findViewById(R.id.unit)
        errorTextView = view.findViewById(R.id.errorTextView)

        // Load attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomInputText, 0, 0)

            val label = typedArray.getString(R.styleable.CustomInputText_labelText)
            setLabel(label)

            val editTextHeight = typedArray.getDimensionPixelSize(R.styleable.CustomInputText_editTextHeight, LayoutParams.WRAP_CONTENT)
            setEditTextHeight(editTextHeight)

            val unit = typedArray.getString(R.styleable.CustomInputText_editTextUnit)
            setUnit(unit)

            val labelTextColor = typedArray.getColor(R.styleable.CustomInputText_labelTextColor, resources.getColor(R.color.black))
            labelTextView.setTextColor(labelTextColor)

            val errorText = typedArray.getString(R.styleable.CustomInputText_errorText)
            errorTextView.text = errorText

            typedArray.recycle()
        }

        inputEditText.apply {
            textSize = 16f
            textAlignment = TEXT_ALIGNMENT_TEXT_START
        }

        onChangeListener()
    }

    fun setLabel(label: String?) {
        labelTextView.text = label
    }

    fun getText(): String {
        return inputEditText.text.toString()
    }

    fun setEditTextHeight(height: Int) {
        val params = inputEditText.layoutParams
        params.height = height
        inputEditText.layoutParams = params
    }

    fun setUnit(unit: String?) {
        unitTextView.text = unit
    }

    fun isEditTextEmpty(): Boolean {
        return inputEditText.text.toString().isEmpty()
    }

    fun setReadOnly() {
        inputEditText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = false
            setTextIsSelectable(false)
        }
    }

    private fun onChangeListener(){
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                errorTextView.apply {
                    if(isEditTextEmpty()) {
                        text = "This field cannot be empty"
                        textInputLayout.boxStrokeColor = resources.getColor(R.color.toast_error)
                    } else {
                        text = ""
                        textInputLayout.boxStrokeColor = resources.getColor(R.color.pearlBlue)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}