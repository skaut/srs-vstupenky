package cz.skaut.srs.ticketsreader.scanner

import android.graphics.Rect
import android.media.ThumbnailUtils
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import cz.skaut.srs.ticketsreader.processor.QrProcessor

class QrAnalyzer(
    private val qrProcessor: QrProcessor,
    private val qrBoxView: QrBoxView,
    private val previewViewWidth: Float,
    private val previewViewHeight: Float
) : ImageAnalysis.Analyzer {

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
            val bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputImage)
            val resizedBitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                previewViewWidth.toInt(),
                previewViewHeight.toInt()
            )
            val analysisImage = InputImage.fromBitmap(resizedBitmap, 0)

            val options = BarcodeScannerOptions.Builder().build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(analysisImage)
                .addOnSuccessListener { barcodes ->
                    if (!qrProcessor.processingActive) {
                        if (barcodes.isNotEmpty()) {
                            qrProcessor.processingActive = true
                            qrProcessor.process(barcodes.get(0).rawValue!!)
                            barcodes.get(0).boundingBox?.let { rect -> qrBoxView.setRect(rect) }
                        } else {
                            qrBoxView.setRect(Rect())
                        }
                    }
                }
                .addOnFailureListener { }
        }
        image.close()
    }
}