package com.lh.kete.threadpool

import android.os.Process
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

interface KeteExec {
    companion object {
        private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
        private val threadPool: ThreadPoolExecutor

        init {
            threadPool = ThreadPoolExecutor(
                    NUMBER_OF_CORES * 2,
                    NUMBER_OF_CORES * 2,
                    10L,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue<Runnable>(),
                    PriorityThreadFactory(Thread.MAX_PRIORITY)
            )
        }

        fun doBackground(what: Runnable) {
            threadPool.execute(what)
        }
    }

    class PriorityThreadFactory(private val mThreadPriority: Int) : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            val wrapperRunnable = Runnable {
                try {
                    Process.setThreadPriority(mThreadPriority)
                } catch (t: Throwable) {

                }
                runnable.run()
            }
            return Thread(wrapperRunnable)
        }
    }
}