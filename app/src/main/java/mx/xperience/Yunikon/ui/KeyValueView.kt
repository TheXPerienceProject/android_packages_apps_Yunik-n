/*
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2021 The XPerience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.xperience.Yunikon.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import mx.xperience.Yunikon.R

class KeyValueView : LinearLayout {
    private var mKeyView: TextView? = null
    private var mValueView: TextView? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.key_value_view, this)
        mKeyView = findViewById(R.id.key)
        mValueView = findViewById(R.id.value)
    }

    fun setText(@StringRes attributeTextResId: Int, value: String) {
        if (!value.isEmpty()) {
            mKeyView!!.setText(attributeTextResId)
            mValueView!!.text = value
        } else {
            visibility = GONE
        }
    }
}