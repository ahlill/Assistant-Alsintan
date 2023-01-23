package id.tpusk.assistantalsintan

import android.widget.EditText


fun EditText.validate(): Boolean {
    return if (this.text.trim().toString().isBlank() || this.text.trim().toString().isEmpty()) {
        this.error = "Empty"
        this.requestFocus()
        false
    } else {
        true
    }
}

fun EditText.str(): String {
    return this.text.trim().toString()
}
