package cz.cuni.mff.maso.ui.zip

import android.content.Context
import android.content.Intent
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.databinding.ActivityZipBinding
import cz.cuni.mff.maso.ui.BaseActivity

interface ZipView {
    fun onNextClicked()
}

class ZipActivity : BaseActivity<ActivityZipBinding, ZipViewModel, ZipView>() {
    override val layoutResId: Int = R.layout.activity_zip
    override val viewModel by lazy { initViewModel<ZipViewModel>() }
    override val view = object : ZipView {
        override fun onNextClicked() {
            viewModel.zipUnpack(filesDir)
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ZipActivity::class.java)
    }
}
