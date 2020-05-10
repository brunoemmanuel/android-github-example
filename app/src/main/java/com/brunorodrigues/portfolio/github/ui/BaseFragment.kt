package com.brunorodrigues.portfolio.github.ui

import androidx.fragment.app.Fragment
import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

open class BaseFragment(private val resourceName: String) : Fragment(), IdlingResource {
    private val isIdle = AtomicBoolean(true)

    @Volatile private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = resourceName

    override fun isIdleNow(): Boolean = isIdle.get()

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    open fun setIdle(isIdleNow: Boolean) {
        if (isIdleNow == isIdle.get()) return
        isIdle.set(isIdleNow)
        if (isIdleNow) {
            resourceCallback?.onTransitionToIdle()
        }
    }
}