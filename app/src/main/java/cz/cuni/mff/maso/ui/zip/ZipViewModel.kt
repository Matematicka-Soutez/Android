package cz.cuni.mff.maso.ui.zip

import android.os.Environment
import cz.cuni.mff.maso.ui.BaseViewModel
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class ZipViewModel : BaseViewModel() {

    fun zipUnpack() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        ZipUtil.unpack(File(path, "/maso/pictures.zip"), File(path, "/maso"))
    }

}