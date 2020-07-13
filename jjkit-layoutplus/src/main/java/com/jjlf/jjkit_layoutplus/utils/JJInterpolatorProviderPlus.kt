package com.jjlf.jjkit_layoutplus.utils

import kotlin.math.*

internal object JJInterpolatorProviderPlus {
    const val LINEAR = 0
    const val QUAD_IN= 1
    const val QUAD_OUT= 2
    const val QUAD_IN_OUT= 3
    const val CUBIC_IN= 4
    const val CUBIC_OUT= 5
    const val CUBIC_IN_OUT= 6
    const val QUART_IN= 7
    const val QUART_OUT= 8
    const val QUART_IN_OUT= 9
    const val QUINT_IN= 10
    const val QUINT_OUT= 11
    const val QUINT_IN_OUT= 12
    const val SINE_IN= 13
    const val SINE_OUT= 14
    const val SINE_IN_OUT= 15
    const val BACK_IN= 16
    const val BACK_OUT= 17
    const val BACK_IN_OUT= 18
    const val CIRC_IN= 19
    const val CIRC_OUT= 20
    const val CIRC_IN_OUT= 21
    const val BOUNCE_IN= 22
    const val BOUNCE_OUT= 23
    const val BOUNCE_IN_OUT= 24
    const val ELASTIC_IN= 25
    const val ELASTIC_OUT= 26
    const val ELASTIC_IN_OUT= 27
    const val EASE_IN_EXPO= 28
    const val EASE_OUT_EXPO= 29
    const val EASE_IN_OUT_EXPO= 30
    const val SPRING_BOUNCE_OUT = 31

    
     fun get(ease: Int, elapsedTimeRate: Float): Float {
        var localElapsedTimeRate = elapsedTimeRate
        return when (ease) {
            LINEAR -> localElapsedTimeRate
            QUAD_IN -> getPowIn(localElapsedTimeRate, 2.0)
            QUAD_OUT -> getPowOut(localElapsedTimeRate, 2.0)
            QUAD_IN_OUT -> getPowInOut(localElapsedTimeRate, 2.0)
            CUBIC_IN -> getPowIn(localElapsedTimeRate, 3.0)
            CUBIC_OUT -> getPowOut(localElapsedTimeRate, 3.0)
            CUBIC_IN_OUT -> getPowInOut(localElapsedTimeRate, 3.0)
            QUART_IN -> getPowIn(localElapsedTimeRate, 4.0)
            QUART_OUT -> getPowOut(localElapsedTimeRate, 4.0)
            QUART_IN_OUT -> getPowInOut(localElapsedTimeRate, 4.0)
            QUINT_IN -> getPowIn(localElapsedTimeRate, 5.0)
            QUINT_OUT -> getPowOut(localElapsedTimeRate, 5.0)
            QUINT_IN_OUT -> getPowInOut(localElapsedTimeRate, 5.0)
            SINE_IN -> (1f - cos(localElapsedTimeRate * Math.PI / 2f)).toFloat()
            SINE_OUT -> sin(localElapsedTimeRate * Math.PI / 2f).toFloat()
            SINE_IN_OUT -> (-0.5f * (cos(Math.PI * localElapsedTimeRate) - 1f)).toFloat()
            BACK_IN ->
                (localElapsedTimeRate * localElapsedTimeRate * ((1.7 + 1f) * localElapsedTimeRate - 1.7)).toFloat()
            BACK_OUT ->
                (--localElapsedTimeRate * localElapsedTimeRate * ((1.7 + 1f) * localElapsedTimeRate + 1.7) + 1f).toFloat()
            BACK_IN_OUT ->
                getBackInOut(localElapsedTimeRate, 1.7f)
            CIRC_IN ->
                (-(sqrt(1f - localElapsedTimeRate * localElapsedTimeRate.toDouble()) - 1)).toFloat()
            CIRC_OUT ->
                sqrt(1f - --localElapsedTimeRate * localElapsedTimeRate.toDouble()).toFloat()
            CIRC_IN_OUT -> {
                if (2f.let { localElapsedTimeRate *= it; localElapsedTimeRate } < 1f) {
                    (-0.5f * (sqrt(1f - localElapsedTimeRate * localElapsedTimeRate.toDouble()) - 1f)).toFloat()
                } else (0.5f * (sqrt(1f - 2f.let { localElapsedTimeRate -= it; localElapsedTimeRate } * localElapsedTimeRate.toDouble()) + 1f)).toFloat()
            }
            BOUNCE_IN -> getBounceIn(localElapsedTimeRate)
            BOUNCE_OUT -> getBounceOut(localElapsedTimeRate)
            BOUNCE_IN_OUT -> { if (localElapsedTimeRate < 0.5f) { getBounceIn(localElapsedTimeRate * 2f) * 0.5f }
                                else getBounceOut(localElapsedTimeRate * 2f - 1f) * 0.5f + 0.5f }
            ELASTIC_IN -> getElasticIn(localElapsedTimeRate, 1.0, 0.3)
            ELASTIC_OUT -> getElasticOut(localElapsedTimeRate, 1.0, 0.3)
            ELASTIC_IN_OUT -> getElasticInOut(localElapsedTimeRate, 1.0, 0.45)
            EASE_IN_EXPO -> {
                2.0.pow(10 * (localElapsedTimeRate - 1).toDouble()).toFloat()
            }
            EASE_OUT_EXPO -> {
                (-((2.0).pow(-10 * localElapsedTimeRate.toDouble())) + 1).toFloat()
            }
            EASE_IN_OUT_EXPO -> {
                if (2.let { localElapsedTimeRate *= it; localElapsedTimeRate } < 1) {
                    2.0.pow(10 * (localElapsedTimeRate - 1).toDouble()).toFloat() * 0.5f
                } else {
                    ((-((2.0).pow(-10 * (localElapsedTimeRate.let{ --localElapsedTimeRate }).toDouble())) + 2f) * 0.5f).toFloat()
                }
            }
            SPRING_BOUNCE_OUT -> {
                (((2.0).pow((-10 * localElapsedTimeRate).toDouble())) * sin(((2 * Math.PI) * (localElapsedTimeRate - (0.3f / 4))) / 0.3f) + 1).toFloat()
            }
            else -> localElapsedTimeRate
        }
    }


    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param pow             pow The exponent to use (ex. 3 would return a cubic ease).
     * @return easedValue
     */
    private fun getPowIn(elapsedTimeRate: Float, pow: Double): Float {
        return elapsedTimeRate.toDouble().pow(pow).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param pow             pow The exponent to use (ex. 3 would return a cubic ease).
     * @return easedValue
     */
    private fun getPowOut(elapsedTimeRate: Float, pow: Double): Float {
        return (1.toFloat() - (1 - elapsedTimeRate.toDouble()).pow(pow)).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param pow             pow The exponent to use (ex. 3 would return a cubic ease).
     * @return easedValue
     */
    private fun getPowInOut(elapsedTimeRate: Float, pow: Double): Float {
        val localElapsedTimeRate = elapsedTimeRate * 2
        return if (localElapsedTimeRate < 1) {
            (0.5 * localElapsedTimeRate.toDouble().pow(pow)).toFloat()
        } else (  1 - 0.5 * abs((2 - elapsedTimeRate.toDouble()).pow(pow)) ).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param amount          amount The strength of the ease.
     * @return easedValue
     */
    private fun getBackInOut(elapsedTimeRate: Float, amount: Float): Float {
        val localAmount = amount * 1.525f
        var localElapse = elapsedTimeRate
        return if (2.let { localElapse *= it; localElapse } < 1) {
            (0.5 * (localElapse * localElapse * ((localAmount + 1) * localElapse - localAmount))).toFloat()
        } else (0.5 * (2.let { localElapse -= it; localElapse } * localElapse * ((localAmount + 1) * localElapse + localAmount) + 2)).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @return easedValue
     */
    private fun getBounceIn(elapsedTimeRate: Float): Float {
        return 1f - getBounceOut(1f - elapsedTimeRate)
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @return easedValue
     */
    private fun getBounceOut(elapsedTimeRate: Float): Float {
        var localElapsedTimeRate =  elapsedTimeRate
        return when {
            elapsedTimeRate < 1 / 2.75 -> {
                (7.5625 * elapsedTimeRate * elapsedTimeRate).toFloat()
            }
            elapsedTimeRate < 2 / 2.75 -> {
                (7.5625 * (1.5 / 2.75).let { localElapsedTimeRate -= it.toFloat(); localElapsedTimeRate} * localElapsedTimeRate + 0.75).toFloat()
            }
            elapsedTimeRate < 2.5 / 2.75 -> {
                (7.5625 * (2.25 / 2.75).let { localElapsedTimeRate -= it.toFloat(); localElapsedTimeRate } * localElapsedTimeRate + 0.9375).toFloat()
            }
            else -> {
                (7.5625 * (2.625 / 2.75).let { localElapsedTimeRate -= it.toFloat(); localElapsedTimeRate } * localElapsedTimeRate + 0.984375).toFloat()
            }
        }
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param amplitude       Amplitude of easing
     * @param period          Animation of period
     * @return easedValue
     */
    private fun getElasticIn(elapsedTimeRate: Float, amplitude: Double, period: Double): Float {
        if (elapsedTimeRate == 0f || elapsedTimeRate == 1f) return elapsedTimeRate

        var localElapsedTimeRate = elapsedTimeRate
        val pi2 = Math.PI * 2
        val s = period / pi2 * asin(1 / amplitude)

        return (-(amplitude * 2.0.pow(10f * 1f.let { localElapsedTimeRate -= it; localElapsedTimeRate }.toDouble()) * sin((localElapsedTimeRate - s) * pi2 / period))).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param amplitude       Amplitude of easing
     * @param period          Animation of period
     * @return easedValue
     */
    private fun getElasticOut(elapsedTimeRate: Float, amplitude: Double, period: Double): Float {
        if (elapsedTimeRate == 0f || elapsedTimeRate == 1f) return elapsedTimeRate
        val pi2 = Math.PI * 2
        val s = period / pi2 * asin(1 / amplitude)
        return (amplitude * 2.0.pow(-10 * elapsedTimeRate.toDouble()) * sin((elapsedTimeRate - s) * pi2 / period) + 1).toFloat()
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param amplitude       Amplitude of easing
     * @param period          Animation of period
     * @return easedValue
     */
    private fun getElasticInOut(elapsedTimeRate: Float, amplitude: Double, period: Double): Float {
        var localElapsedTimeRate = elapsedTimeRate
        val pi2 = Math.PI * 2
        val s = period / pi2 * Math.asin(1 / amplitude)

        return if ((2.let{ localElapsedTimeRate *= 2; localElapsedTimeRate}) < 1) {
            (-0.5f * (amplitude * 2.0.pow(10 * 1f.let { localElapsedTimeRate -= it; localElapsedTimeRate }.toDouble()) * sin((localElapsedTimeRate - s) * pi2 / period))).toFloat()
        } else (amplitude * 2.0.pow(-10 * 1.let { localElapsedTimeRate -= it; localElapsedTimeRate }.toDouble()) * sin((localElapsedTimeRate - s) * pi2 / period) * 0.5 + 1).toFloat()
    }


}