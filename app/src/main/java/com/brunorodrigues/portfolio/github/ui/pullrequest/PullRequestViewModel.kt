package com.brunorodrigues.portfolio.github.ui.pullrequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.data.PullRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PullRequestViewModel(private var client: Client) : ViewModel(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    var pullRequests: MutableLiveData<ArrayList<PullRequest>> = MutableLiveData()
    var error: MutableLiveData<Throwable> = MutableLiveData()

    fun loadPullRequests(userName: String, repositoryName: String, page: Int) {
        launch {
            try {
                val items = client.getPullRequests(userName, repositoryName, page)
                var arr = pullRequests.value
                if(arr == null) arr = ArrayList()
                arr.addAll(items)
                pullRequests.value = arr
            } catch (throwable: Throwable) {
                error.value = throwable
            }
        }
    }

    fun destroy() {
        job.cancel()
    }
}
