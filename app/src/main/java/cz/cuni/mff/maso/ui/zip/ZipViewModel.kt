package cz.cuni.mff.maso.ui.zip

import cz.cuni.mff.maso.ui.BaseViewModel
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class ZipViewModel : BaseViewModel() {

    fun zipUnpack(filesDir: File) {
        val zipFile = File(filesDir, "/maso/pictures.zip")
        if (zipFile.exists()) {
            ZipUtil.unpack(zipFile, File(filesDir, "/maso"))
        }
    }

}