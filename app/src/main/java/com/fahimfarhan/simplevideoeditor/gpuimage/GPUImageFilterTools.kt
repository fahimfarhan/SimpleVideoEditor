package org.mainstring.textee.ui.videoeditor.imageutil;

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import com.fahimfarhan.simplevideoeditor.R
import com.fahimfarhan.simplevideoeditor.gpuimage.gpufilter.*;


import java.util.*

object GPUImageFilterTools {
    fun showDialog(
            context: Context,
            listener: (filter: GPUImageFilter) -> Unit
    ) {
        val filters = FilterList().apply {
            addFilter("Contrast", ImageFilterType.CONTRAST)
            addFilter("Invert", ImageFilterType.INVERT)
            addFilter("Pixelation", ImageFilterType.PIXELATION)
            addFilter("Hue", ImageFilterType.HUE)
            addFilter("Gamma", ImageFilterType.GAMMA)
            addFilter("Brightness", ImageFilterType.BRIGHTNESS)
            addFilter("Sepia", ImageFilterType.SEPIA)
            addFilter("Grayscale", ImageFilterType.GRAYSCALE)
            addFilter("Sharpness", ImageFilterType.SHARPEN)
//            addFilter("Sobel Edge Detection", ImageFilterType.SOBEL_EDGE_DETECTION)
//            addFilter("Threshold Edge Detection", ImageFilterType.THRESHOLD_EDGE_DETECTION)
//            addFilter("3x3 Convolution", ImageFilterType.THREE_X_THREE_CONVOLUTION)
//            addFilter("Emboss", ImageFilterType.EMBOSS)
            addFilter("Posterize", ImageFilterType.POSTERIZE)
            addFilter("Grouped filters", ImageFilterType.FILTER_GROUP)
            addFilter("Saturation", ImageFilterType.SATURATION)
            addFilter("Exposure", ImageFilterType.EXPOSURE)
            addFilter("Highlight Shadow", ImageFilterType.HIGHLIGHT_SHADOW)
            addFilter("Monochrome", ImageFilterType.MONOCHROME)
            addFilter("Opacity", ImageFilterType.OPACITY)
            addFilter("RGB", ImageFilterType.RGB)
            addFilter("White Balance", ImageFilterType.WHITE_BALANCE)
            addFilter("Vignette", ImageFilterType.VIGNETTE)
            addFilter("ToneCurve", ImageFilterType.TONE_CURVE)

            addFilter("Luminance", ImageFilterType.LUMINANCE)
            addFilter("Luminance Threshold", ImageFilterType.LUMINANCE_THRESHSOLD)

//            addFilter("Blend (Difference)", ImageFilterType.BLEND_DIFFERENCE)
//            addFilter("Blend (Source Over)", ImageFilterType.BLEND_SOURCE_OVER)
//            addFilter("Blend (Color Burn)", ImageFilterType.BLEND_COLOR_BURN)
//            addFilter("Blend (Color Dodge)", ImageFilterType.BLEND_COLOR_DODGE)
//            addFilter("Blend (Darken)", ImageFilterType.BLEND_DARKEN)
//            addFilter("Blend (Dissolve)", ImageFilterType.BLEND_DISSOLVE)
//            addFilter("Blend (Exclusion)", ImageFilterType.BLEND_EXCLUSION)
//            addFilter("Blend (Hard Light)", ImageFilterType.BLEND_HARD_LIGHT)
//            addFilter("Blend (Lighten)", ImageFilterType.BLEND_LIGHTEN)
//            addFilter("Blend (Add)", ImageFilterType.BLEND_ADD)
//            addFilter("Blend (Divide)", ImageFilterType.BLEND_DIVIDE)
//            addFilter("Blend (Multiply)", ImageFilterType.BLEND_MULTIPLY)
            addFilter("Blend (Overlay)", ImageFilterType.BLEND_OVERLAY)
//            addFilter("Blend (Screen)", ImageFilterType.BLEND_SCREEN)
//            addFilter("Blend (Alpha)", ImageFilterType.BLEND_ALPHA)
//            addFilter("Blend (Color)", ImageFilterType.BLEND_COLOR)
//            addFilter("Blend (Hue)", ImageFilterType.BLEND_HUE)
//            addFilter("Blend (Saturation)", ImageFilterType.BLEND_SATURATION)
//            addFilter("Blend (Luminosity)", ImageFilterType.BLEND_LUMINOSITY)
//            addFilter("Blend (Linear Burn)", ImageFilterType.BLEND_LINEAR_BURN)
//            addFilter("Blend (Soft Light)", ImageFilterType.BLEND_SOFT_LIGHT)
//            addFilter("Blend (Subtract)", ImageFilterType.BLEND_SUBTRACT)
//            addFilter("Blend (Chroma Key)", ImageFilterType.BLEND_CHROMA_KEY)
//            addFilter("Blend (Normal)", ImageFilterType.BLEND_NORMAL)
//
//            addFilter("Lookup (Amatorka)", ImageFilterType.LOOKUP_AMATORKA)
            addFilter("Gaussian Blur", ImageFilterType.GAUSSIAN_BLUR)
            addFilter("Crosshatch", ImageFilterType.CROSSHATCH)

            addFilter("Box Blur", ImageFilterType.BOX_BLUR)
            addFilter("CGA Color Space", ImageFilterType.CGA_COLORSPACE)
//            addFilter("Dilation", ImageFilterType.DILATION)
//            addFilter("Kuwahara", ImageFilterType.KUWAHARA)
//            addFilter("RGB Dilation", ImageFilterType.RGB_DILATION)
//            addFilter("Sketch", ImageFilterType.SKETCH)
//            addFilter("Toon", ImageFilterType.TOON)
//            addFilter("Smooth Toon", ImageFilterType.SMOOTH_TOON)
            addFilter("Halftone", ImageFilterType.HALFTONE)

            addFilter("Bulge Distortion", ImageFilterType.BULGE_DISTORTION)
//            addFilter("Glass Sphere", ImageFilterType.GLASS_SPHERE)
            addFilter("Haze", ImageFilterType.HAZE)
//            addFilter("Laplacian", ImageFilterType.LAPLACIAN)
//            addFilter("Non Maximum Suppression", ImageFilterType.NON_MAXIMUM_SUPPRESSION)
            addFilter("Sphere Refraction", ImageFilterType.SPHERE_REFRACTION)
            addFilter("Swirl", ImageFilterType.SWIRL)
            addFilter("Weak Pixel Inclusion", ImageFilterType.WEAK_PIXEL_INCLUSION)
//            addFilter("False Color", ImageFilterType.FALSE_COLOR)

//            addFilter("Color Balance", ImageFilterType.COLOR_BALANCE)

//            addFilter("Levels Min (Mid Adjust)", ImageFilterType.LEVELS_FILTER_MIN)

            addFilter("Bilateral Blur", ImageFilterType.BILATERAL_BLUR)

            addFilter("Zoom Blur", ImageFilterType.ZOOM_BLUR)

//            addFilter("Transform (2-D)", ImageFilterType.TRANSFORM2D)

            addFilter("Solarize", ImageFilterType.SOLARIZE)

            addFilter("Vibrance", ImageFilterType.VIBRANCE)
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
        builder.setItems(filters.names.toTypedArray()) { _, item ->
            listener(createFilterForType(context, filters.filters[item]))
        }
        builder.create().show()
    }

    public fun createFilterForType(context: Context, type: ImageFilterType): GPUImageFilter {
        return when (type) {
            ImageFilterType.CONTRAST -> GPUImageContrastFilter(2.0f)
            ImageFilterType.GAMMA -> GPUImageGammaFilter(2.0f)
            ImageFilterType.INVERT -> GPUImageColorInvertFilter()
            ImageFilterType.PIXELATION -> GPUImagePixelationFilter(0.2f)
            ImageFilterType.HUE -> GPUImageHueFilter(90.0f)
            ImageFilterType.BRIGHTNESS -> GPUImageBrightnessFilter(.8f)
            ImageFilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
            ImageFilterType.SEPIA -> GPUImageSepiaToneFilter()
            ImageFilterType.SHARPEN -> GPUImageSharpenFilter(0.8f)
//            ImageFilterType.SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetectionFilter()
//            ImageFilterType.THRESHOLD_EDGE_DETECTION -> GPUImageThresholdEdgeDetectionFilter()
//            ImageFilterType.THREE_X_THREE_CONVOLUTION -> GPUImage3x3ConvolutionFilter()
//            ImageFilterType.EMBOSS -> GPUImageEmbossFilter()
            ImageFilterType.POSTERIZE -> GPUImagePosterizeFilter(2)
            ImageFilterType.FILTER_GROUP -> GPUImageFilterGroup(
                    listOf(
                            GPUImageContrastFilter(),
                            GPUImageDirectionalSobelEdgeDetectionFilter(),
                            GPUImageGrayscaleFilter()
                    )
            )
            ImageFilterType.SATURATION -> GPUImageSaturationFilter(0.8f)
            ImageFilterType.EXPOSURE -> GPUImageExposureFilter(0.5f)
            ImageFilterType.HIGHLIGHT_SHADOW -> GPUImageHighlightShadowFilter(
                    0.2f,
                    0.8f
            )
            ImageFilterType.MONOCHROME -> GPUImageMonochromeFilter(
                    1.0f, floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
            )
            ImageFilterType.OPACITY -> GPUImageOpacityFilter(0.8f)
            ImageFilterType.RGB -> GPUImageRGBFilter(0.2f, 0.7f, 0.5f)
            ImageFilterType.WHITE_BALANCE -> GPUImageWhiteBalanceFilter(
                    5000.0f,
                    0.0f
            )
            ImageFilterType.VIGNETTE -> GPUImageVignetteFilter(
                    PointF(0.5f, 0.5f),
                    floatArrayOf(0.0f, 0.0f, 0.0f),
                    0.3f,
                    0.75f
            )
            ImageFilterType.TONE_CURVE -> GPUImageToneCurveFilter().apply {
                setFromCurveFileInputStream(context.resources.openRawResource(R.raw.tone_cuver_sample))
            }
            ImageFilterType.LUMINANCE -> GPUImageLuminanceFilter()
            ImageFilterType.LUMINANCE_THRESHSOLD -> GPUImageLuminanceThresholdFilter(0.5f)
//            ImageFilterType.BLEND_DIFFERENCE -> createBlendFilter(
//                    context,
//                    GPUImageDifferenceBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_SOURCE_OVER -> createBlendFilter(
//                    context,
//                    GPUImageSourceOverBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_COLOR_BURN -> createBlendFilter(
//                    context,
//                    GPUImageColorBurnBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_COLOR_DODGE -> createBlendFilter(
//                    context,
//                    GPUImageColorDodgeBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_DARKEN -> createBlendFilter(
//                    context,
//                    GPUImageDarkenBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_DISSOLVE -> createBlendFilter(
//                    context,
//                    GPUImageDissolveBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_EXCLUSION -> createBlendFilter(
//                    context,
//                    GPUImageExclusionBlendFilter::class.java
//            )
//
//            ImageFilterType.BLEND_HARD_LIGHT -> createBlendFilter(
//                    context,
//                    GPUImageHardLightBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_LIGHTEN -> createBlendFilter(
//                    context,
//                    GPUImageLightenBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_ADD -> createBlendFilter(
//                    context,
//                    GPUImageAddBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_DIVIDE -> createBlendFilter(
//                    context,
//                    GPUImageDivideBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_MULTIPLY -> createBlendFilter(
//                    context,
//                    GPUImageMultiplyBlendFilter::class.java
//            )
            ImageFilterType.BLEND_OVERLAY -> createBlendFilter(
                    context,
                    GPUImageOverlayBlendFilter::class.java
            )
//            ImageFilterType.BLEND_SCREEN -> createBlendFilter(
//                    context,
//                    GPUImageScreenBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_ALPHA -> createBlendFilter(
//                    context,
//                    GPUImageAlphaBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_COLOR -> createBlendFilter(
//                    context,
//                    GPUImageColorBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_HUE -> createBlendFilter(
//                    context,
//                    GPUImageHueBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_SATURATION -> createBlendFilter(
//                    context,
//                    GPUImageSaturationBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_LUMINOSITY -> createBlendFilter(
//                    context,
//                    GPUImageLuminosityBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_LINEAR_BURN -> createBlendFilter(
//                    context,
//                    GPUImageLinearBurnBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_SOFT_LIGHT -> createBlendFilter(
//                    context,
//                    GPUImageSoftLightBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_SUBTRACT -> createBlendFilter(
//                    context,
//                    GPUImageSubtractBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_CHROMA_KEY -> createBlendFilter(
//                    context,
//                    GPUImageChromaKeyBlendFilter::class.java
//            )
//            ImageFilterType.BLEND_NORMAL -> createBlendFilter(
//                    context,
//                    GPUImageNormalBlendFilter::class.java
//            )

//            ImageFilterType.LOOKUP_AMATORKA -> GPUImageLookupFilter().apply {
//                bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lookup_amatorka)
//            }
            ImageFilterType.GAUSSIAN_BLUR -> GPUImageGaussianBlurFilter(0.5f)
            ImageFilterType.CROSSHATCH -> GPUImageCrosshatchFilter()
            ImageFilterType.BOX_BLUR -> GPUImageBoxBlurFilter()
            ImageFilterType.CGA_COLORSPACE -> GPUImageCGAColorspaceFilter()
//            ImageFilterType.DILATION -> GPUImageDilationFilter()
//            ImageFilterType.KUWAHARA -> GPUImageKuwaharaFilter()
//            ImageFilterType.RGB_DILATION -> GPUImageRGBDilationFilter()
//            ImageFilterType.SKETCH -> GPUImageSketchFilter()
//            ImageFilterType.TOON -> GPUImageToonFilter()
//            ImageFilterType.SMOOTH_TOON -> GPUImageSmoothToonFilter()
            ImageFilterType.BULGE_DISTORTION -> GPUImageBulgeDistortionFilter()
//            ImageFilterType.GLASS_SPHERE -> GPUImageGlassSphereFilter()
            ImageFilterType.HAZE -> GPUImageHazeFilter(0.2f, 0.2f)  // todo: might not work. check
//            ImageFilterType.LAPLACIAN -> GPUImageLaplacianFilter()
//            ImageFilterType.NON_MAXIMUM_SUPPRESSION -> GPUImageNonMaximumSuppressionFilter()
            ImageFilterType.SPHERE_REFRACTION -> GPUImageSphereRefractionFilter()
            ImageFilterType.SWIRL -> GPUImageSwirlFilter()
            ImageFilterType.WEAK_PIXEL_INCLUSION -> GPUImageWeakPixelInclusionFilter()
//            ImageFilterType.FALSE_COLOR -> GPUImageFalseColorFilter()
//            ImageFilterType.COLOR_BALANCE -> GPUImageColorBalanceFilter()
//            ImageFilterType.LEVELS_FILTER_MIN -> GPUImageLevelsFilter()
            ImageFilterType.HALFTONE -> GPUImageHalftoneFilter()
            ImageFilterType.BILATERAL_BLUR -> GPUImageBilateralBlurFilter()
            ImageFilterType.ZOOM_BLUR -> GPUImageZoomBlurFilter()
//            ImageFilterType.TRANSFORM2D -> GPUImageTransformFilter()
            ImageFilterType.SOLARIZE -> GPUImageSolarizeFilter()
            ImageFilterType.VIBRANCE -> GPUImageVibranceFilter(0.8f)
            else -> GPUImageBrightnessFilter(1f)  // so by default, it should get the exact same image...
        }
    }

    private fun createBlendFilter(
            context: Context,
            filterClass: Class<out GPUImageTwoInputFilter>
    ): GPUImageFilter {
        return try {
            filterClass.newInstance().apply {
                bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_smile_heart_eyes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            GPUImageFilter()
        }
    }

    public enum class ImageFilterType {
        DEFAULT,
        BILATERAL_BLUR,
        BOX_BLUR,
        BRIGHTNESS,
        BULGE_DISTORTION,
        CGA_COLORSPACE,
        CONTRAST,
        CROSSHATCH,
        EXPOSURE,
        FILTER_GROUP,
        GAMMA,
        GAUSSIAN_BLUR,
        GRAYSCALE,
        HALFTONE,
        HAZE,
        HIGHLIGHT_SHADOW,
        HUE,
        INVERT,
        LUMINANCE,
        LUMINANCE_THRESHSOLD,
        MONOCHROME,
        OPACITY,
        BLEND_OVERLAY,
        PIXELATION,
        POSTERIZE,
        RGB,
        SATURATION,
        SEPIA,
        SHARPEN,
        SOLARIZE,
        SPHERE_REFRACTION,
        SWIRL,
        TONE_CURVE,
        VIBRANCE,
        VIGNETTE,
        WEAK_PIXEL_INCLUSION,
        WHITE_BALANCE,
        ZOOM_BLUR
    }

    private class FilterList {
        val names: MutableList<String> = LinkedList()
        val filters: MutableList<ImageFilterType> = LinkedList()

        fun addFilter(name: String, filter: ImageFilterType) {
            names.add(name)
            filters.add(filter)
        }
    }

    class FilterAdjuster(filter: GPUImageFilter) {
        private val adjuster: Adjuster<out GPUImageFilter>?

        init {
            adjuster = when (filter) {
                is GPUImageSharpenFilter -> SharpnessAdjuster(filter)
                is GPUImageSepiaToneFilter -> SepiaAdjuster(filter)
                is GPUImageContrastFilter -> ContrastAdjuster(filter)
                is GPUImageGammaFilter -> GammaAdjuster(filter)
                is GPUImageBrightnessFilter -> BrightnessAdjuster(filter)
//                is GPUImageSobelEdgeDetectionFilter -> SobelAdjuster(filter)
//                is GPUImageThresholdEdgeDetectionFilter -> ThresholdAdjuster(filter)
//                is GPUImage3x3ConvolutionFilter -> ThreeXThreeConvolutionAjuster(filter)
//                is GPUImageEmbossFilter -> EmbossAdjuster(filter)
                is GPUImage3x3TextureSamplingFilter -> GPU3x3TextureAdjuster(filter)
                is GPUImageHueFilter -> HueAdjuster(filter)
                is GPUImagePosterizeFilter -> PosterizeAdjuster(filter)
                is GPUImagePixelationFilter -> PixelationAdjuster(filter)
                is GPUImageSaturationFilter -> SaturationAdjuster(filter)
                is GPUImageExposureFilter -> ExposureAdjuster(filter)
                is GPUImageHighlightShadowFilter -> HighlightShadowAdjuster(filter)
                is GPUImageMonochromeFilter -> MonochromeAdjuster(filter)
                is GPUImageOpacityFilter -> OpacityAdjuster(filter)
                is GPUImageRGBFilter -> RGBAdjuster(filter)
                is GPUImageWhiteBalanceFilter -> WhiteBalanceAdjuster(filter)
                is GPUImageVignetteFilter -> VignetteAdjuster(filter)
                is GPUImageLuminanceThresholdFilter -> LuminanceThresholdAdjuster(filter)
//                is GPUImageDissolveBlendFilter -> DissolveBlendAdjuster(filter)
                is GPUImageGaussianBlurFilter -> GaussianBlurAdjuster(filter)
                is GPUImageCrosshatchFilter -> CrosshatchBlurAdjuster(filter)
                is GPUImageBulgeDistortionFilter -> BulgeDistortionAdjuster(filter)
//                is GPUImageGlassSphereFilter -> GlassSphereAdjuster(filter)
                is GPUImageHazeFilter -> HazeAdjuster(filter)
                is GPUImageSphereRefractionFilter -> SphereRefractionAdjuster(filter)
                is GPUImageSwirlFilter -> SwirlAdjuster(filter)
//                is GPUImageColorBalanceFilter -> ColorBalanceAdjuster(filter)
//                is GPUImageLevelsFilter -> LevelsMinMidAdjuster(filter)
                is GPUImageBilateralBlurFilter -> BilateralAdjuster(filter)
//                is GPUImageTransformFilter -> RotateAdjuster(filter)
                is GPUImageSolarizeFilter -> SolarizeAdjuster(filter)
                is GPUImageVibranceFilter -> VibranceAdjuster(filter)
                else -> null
            }
        }

        fun canAdjust(): Boolean {
            return adjuster != null
        }

        fun adjust(percentage: Int) {
            adjuster?.adjust(percentage)
        }

        private abstract inner class Adjuster<T : GPUImageFilter>(protected val filter: T) {

            abstract fun adjust(percentage: Int)

            protected fun range(percentage: Int, start: Float, end: Float): Float {
                return (end - start) * percentage / 100.0f + start
            }

            protected fun range(percentage: Int, start: Int, end: Int): Int {
                return (end - start) * percentage / 100 + start
            }
        }

        private inner class SharpnessAdjuster(filter: GPUImageSharpenFilter) :
                Adjuster<GPUImageSharpenFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSharpness(range(percentage, -4.0f, 4.0f))
            }
        }

        private inner class PixelationAdjuster(filter: GPUImagePixelationFilter) :
                Adjuster<GPUImagePixelationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setPixel(range(percentage, 1.0f, 100.0f))
            }
        }

        private inner class HueAdjuster(filter: GPUImageHueFilter) :
                Adjuster<GPUImageHueFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setHue(range(percentage, 0.0f, 360.0f))
            }
        }

        private inner class ContrastAdjuster(filter: GPUImageContrastFilter) :
                Adjuster<GPUImageContrastFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setContrast(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class GammaAdjuster(filter: GPUImageGammaFilter) :
                Adjuster<GPUImageGammaFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setGamma(range(percentage, 0.0f, 3.0f))
            }
        }

        private inner class BrightnessAdjuster(filter: GPUImageBrightnessFilter) :
                Adjuster<GPUImageBrightnessFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBrightness(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class SepiaAdjuster(filter: GPUImageSepiaToneFilter) :
                Adjuster<GPUImageSepiaToneFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 2.0f))
            }
        }

//        private inner class SobelAdjuster(filter: GPUImageSobelEdgeDetectionFilter) :
//                Adjuster<GPUImageSobelEdgeDetectionFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setLineSize(range(percentage, 0.0f, 5.0f))
//            }
//        }
//
//        private inner class ThresholdAdjuster(filter: GPUImageThresholdEdgeDetectionFilter) :
//                Adjuster<GPUImageThresholdEdgeDetectionFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setLineSize(range(percentage, 0.0f, 5.0f))
//                filter.setThreshold(0.9f)
//            }
//        }

//        private inner class ThreeXThreeConvolutionAjuster(filter: GPUImage3x3ConvolutionFilter) :
//                Adjuster<GPUImage3x3ConvolutionFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setConvolutionKernel(
//                        floatArrayOf(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
//                )
//            }
//        }
//
//        private inner class EmbossAdjuster(filter: GPUImageEmbossFilter) :
//                Adjuster<GPUImageEmbossFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.intensity = range(percentage, 0.0f, 4.0f)
//            }
//        }

        private inner class PosterizeAdjuster(filter: GPUImagePosterizeFilter) :
                Adjuster<GPUImagePosterizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                // In theorie to 256, but only first 50 are interesting
                filter.setColorLevels(range(percentage, 1, 50))
            }
        }

        private inner class GPU3x3TextureAdjuster(filter: GPUImage3x3TextureSamplingFilter) :
                Adjuster<GPUImage3x3TextureSamplingFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class SaturationAdjuster(filter: GPUImageSaturationFilter) :
                Adjuster<GPUImageSaturationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSaturation(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ExposureAdjuster(filter: GPUImageExposureFilter) :
                Adjuster<GPUImageExposureFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setExposure(range(percentage, -10.0f, 10.0f))
            }
        }

        private inner class HighlightShadowAdjuster(filter: GPUImageHighlightShadowFilter) :
                Adjuster<GPUImageHighlightShadowFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setShadows(range(percentage, 0.0f, 1.0f))
                filter.setHighlights(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class MonochromeAdjuster(filter: GPUImageMonochromeFilter) :
                Adjuster<GPUImageMonochromeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class OpacityAdjuster(filter: GPUImageOpacityFilter) :
                Adjuster<GPUImageOpacityFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setOpacity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class RGBAdjuster(filter: GPUImageRGBFilter) :
                Adjuster<GPUImageRGBFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRed(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class WhiteBalanceAdjuster(filter: GPUImageWhiteBalanceFilter) :
                Adjuster<GPUImageWhiteBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setTemperature(range(percentage, 2000.0f, 8000.0f))
            }
        }

        private inner class VignetteAdjuster(filter: GPUImageVignetteFilter) :
                Adjuster<GPUImageVignetteFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVignetteStart(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class LuminanceThresholdAdjuster(filter: GPUImageLuminanceThresholdFilter) :
                Adjuster<GPUImageLuminanceThresholdFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

//        private inner class DissolveBlendAdjuster(filter: GPUImageDissolveBlendFilter) :
//                Adjuster<GPUImageDissolveBlendFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setMix(range(percentage, 0.0f, 1.0f))
//            }
//        }

        private inner class GaussianBlurAdjuster(filter: GPUImageGaussianBlurFilter) :
                Adjuster<GPUImageGaussianBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBlurSize(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class CrosshatchBlurAdjuster(filter: GPUImageCrosshatchFilter) :
                Adjuster<GPUImageCrosshatchFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setCrossHatchSpacing(range(percentage, 0.0f, 0.06f))
                filter.setLineWidth(range(percentage, 0.0f, 0.006f))
            }
        }

        private inner class BulgeDistortionAdjuster(filter: GPUImageBulgeDistortionFilter) :
                Adjuster<GPUImageBulgeDistortionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
                filter.setScale(range(percentage, -1.0f, 1.0f))
            }
        }

//        private inner class GlassSphereAdjuster(filter: GPUImageGlassSphereFilter) :
//                Adjuster<GPUImageGlassSphereFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setRadius(range(percentage, 0.0f, 1.0f))
//            }
//        }

        private inner class HazeAdjuster(filter: GPUImageHazeFilter) :
                Adjuster<GPUImageHazeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistance(range(percentage, -0.3f, 0.3f))
                filter.setSlope(range(percentage, -0.3f, 0.3f))
            }
        }

        private inner class SphereRefractionAdjuster(filter: GPUImageSphereRefractionFilter) :
                Adjuster<GPUImageSphereRefractionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class SwirlAdjuster(filter: GPUImageSwirlFilter) :
                Adjuster<GPUImageSwirlFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setAngle(range(percentage, 0.0f, 2.0f))
            }
        }

//        private inner class ColorBalanceAdjuster(filter: GPUImageColorBalanceFilter) :
//                Adjuster<GPUImageColorBalanceFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setMidtones(
//                        floatArrayOf(
//                                range(percentage, 0.0f, 1.0f),
//                                range(percentage / 2, 0.0f, 1.0f),
//                                range(percentage / 3, 0.0f, 1.0f)
//                        )
//                )
//            }
//        }

//        private inner class LevelsMinMidAdjuster(filter: GPUImageLevelsFilter) :
//                Adjuster<GPUImageLevelsFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                filter.setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f)
//            }
//        }

        private inner class BilateralAdjuster(filter: GPUImageBilateralBlurFilter) :
                Adjuster<GPUImageBilateralBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f))
            }
        }

//        private inner class RotateAdjuster(filter: GPUImageTransformFilter) :
//                Adjuster<GPUImageTransformFilter>(filter) {
//            override fun adjust(percentage: Int) {
//                val transform = FloatArray(16)
//                Matrix.setRotateM(transform, 0, (360 * percentage / 100).toFloat(), 0f, 0f, 1.0f)
//                filter.transform3D = transform
//            }
//        }

        private inner class SolarizeAdjuster(filter: GPUImageSolarizeFilter) :
                Adjuster<GPUImageSolarizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class VibranceAdjuster(filter: GPUImageVibranceFilter) :
                Adjuster<GPUImageVibranceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVibrance(range(percentage, -1.2f, 1.2f))
            }
        }
    }
}
