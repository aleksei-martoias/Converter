package com.alekseimy.converter.rules

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class OverrideSchedulers : TestRule {
    private val scheduler by lazy { Schedulers.trampoline() }

    override fun apply(base: Statement?, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                try {
                    RxJavaPlugins.setComputationSchedulerHandler { scheduler }
                    RxJavaPlugins.setIoSchedulerHandler { scheduler }
                    RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
                    RxJavaPlugins.setSingleSchedulerHandler { scheduler }
                    RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
                    base?.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
}