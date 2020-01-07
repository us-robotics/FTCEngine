package FTCEngine

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import FTCEngine.VisionConstants.BLUR_RADIUS
import FTCEngine.VisionConstants.EROSION_ITERATIONS
import FTCEngine.VisionConstants.LEFT_BOUNARY
import FTCEngine.VisionConstants.OUTPUT_STAGE
import FTCEngine.VisionConstants.RIGHT_BOUNDARY
import FTCEngine.VisionConstants.Stage.*
import FTCEngine.VisionConstants.VisionScalar
import FTCEngine.VisionConstants.THRESHOLD_MAX
import FTCEngine.VisionConstants.THRESHOLD_MIN
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvCameraBase
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvPipeline

class Vision(hardwareMap: HardwareMap) {
    private val cameraPreviewId = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId", "id",
            hardwareMap.appContext.packageName)
    private val webcam = OpenCvCameraFactory.getInstance()!!.createWebcam(
            hardwareMap[WebcamName::class.java, "Webcam 1"], cameraPreviewId) as OpenCvCameraBase

    private val pipeline = VisionPipeline()
    val position: VisionPipeline.Position
        get() = pipeline.skystonePos

    init {
        webcam.openCameraDevice()
        webcam.setPipeline(pipeline)
        webcam.startStreaming(320, 240)
        webcam.showFpsMeterOnViewport(false)
    }

    fun destroy() {
        webcam.stopStreaming()
        webcam.closeCameraDevice()
    }
}

class VisionPipeline : OpenCvPipeline() {

    enum class Position {
        LEFT, CENTER, RIGHT, UNKNOWN
    }
    var skystonePos = Position.UNKNOWN

    override fun onViewportTapped() {
        val stages = VisionConstants.Stage.values()
        OUTPUT_STAGE = stages[(OUTPUT_STAGE.ordinal + 1) % stages.size]
    }

    override fun processFrame(input: Mat): Mat {
        val output = Mat()
        if (OUTPUT_STAGE == NOOP) input.copyTo(output)

        val yuv = cvtColor(input, Imgproc.COLOR_RGB2YUV, THRESHOLD_MIN, THRESHOLD_MAX)
        if (OUTPUT_STAGE == THRESHOLD) yuv.copyTo(output)

        val annotated = input.clone()!!
        val width = annotated.width().toDouble()
        val height = annotated.height().toDouble()

        // Draw on the horizontal boundaries
        val leftLim = height * LEFT_BOUNARY
        val rightLim = height * RIGHT_BOUNDARY
        Imgproc.line(annotated, Point(0.0, leftLim), Point(height, leftLim),
                Scalar(255.0, 255.0, 255.0), 2)
        Imgproc.line(annotated, Point(0.0, rightLim), Point(height, rightLim),
                Scalar(255.0, 0.0, 0.0), 2)

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(yuv, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
        Imgproc.drawContours(annotated, contours, -1,
                Scalar(0.0, 255.0, 0.0))

        var inLeft = false
        var inCenter = false
        var inRight = false
        contours.forEach {
            val point = it.toPoint()
            Imgproc.drawMarker(annotated, point, Scalar(0.0, 0.0, 255.0))

            when {
                point.y > leftLim -> inLeft = true
                point.y > rightLim -> inCenter = true
                else -> inRight = true
            }
        }
        if (OUTPUT_STAGE == ANNOTATED) annotated.copyTo(output)

        skystonePos = when {
            !inLeft -> Position.LEFT
            !inCenter -> Position.CENTER
            !inRight -> Position.RIGHT
            else -> Position.UNKNOWN
        }

        return output
    }

    private fun cvtColor(input: Mat, mode: Int, limLow: VisionScalar, limHigh: VisionScalar): Mat {
        val out = Mat()
        Imgproc.cvtColor(input, out, mode)
        return threshold(out, limLow, limHigh)
    }

    private fun threshold(input: Mat, limLow: VisionScalar, limHigh: VisionScalar): Mat {
        val out = Mat()
        Core.inRange(input, limLow.toScalar(), limHigh.toScalar(), out)
        return denoise(out)
    }

    private fun denoise(input: Mat): Mat {
        val blurred = Mat()
        if (BLUR_RADIUS >= 1.0)
            Imgproc.blur(input, blurred, Size(BLUR_RADIUS, BLUR_RADIUS))
        else input.copyTo(blurred)

        val eroded = Mat()
        if (EROSION_ITERATIONS >= 1) {
            val erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    Size(11.0, 11.0))
            Imgproc.erode(blurred, eroded, erodeKernel, Point(0.0, 0.0), EROSION_ITERATIONS)
        } else blurred.copyTo(eroded)

        return eroded
    }

    private fun MatOfPoint.toPoint(): Point {
        val moments = Imgproc.moments(this)
        return Point(moments.m10 / moments.m00, moments.m01 / moments.m00)
    }

    private fun List<MatOfPoint>.avg(): Point {
        val points = this.map { it.toPoint() }
        val xavg = points.fold(0.0) { acc, point -> acc + point.x } / points.size
        val yavg = points.fold(0.0) { acc, point -> acc + point.y } / points.size
        return Point(xavg, yavg)
    }
}