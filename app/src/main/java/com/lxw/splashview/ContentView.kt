package com.lxw.splashview

import android.content.Context
import android.widget.ImageView

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2017/09/18
 *     desc   :
 * </pre>
 */
class ContentView : ImageView {

    constructor(context: Context?) : super(context) {
        setImageResource(R.mipmap.content)
    }
}