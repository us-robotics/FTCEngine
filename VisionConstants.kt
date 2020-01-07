package FTCEngine

import org.opencv.core.Scalar

object VisionConstants {
    enum class Stage {
        NOOP, // Passes the input image through
        THRESHOLD, // Convert the image to YUV and threshold it
        ANNOTATED // Show boundaries and points
    }

    @JvmField var OUTPUT_STAGE = Stage.ANNOTATED

    @JvmField var BLUR_RADIUS = 0.0
    @JvmField var EROSION_ITERATIONS = 3

    data class VisionScalar(@JvmField var a: Double, @JvmField var b: Double, @JvmField var c: Double) {
        fun toScalar() = Scalar(a, b, c)
    }

    @JvmField var THRESHOLD_MIN = VisionScalar(100.0, 0.0, 100.0)
    @JvmField var THRESHOLD_MAX = VisionScalar(255.0, 100.0, 255.0)

    @JvmField var LEFT_BOUNARY = 0.8
    @JvmField var RIGHT_BOUNDARY = 0.45
}