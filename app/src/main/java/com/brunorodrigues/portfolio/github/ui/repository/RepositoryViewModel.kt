package com.brunorodrigues.portfolio.github.ui.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RepositoryViewModel(private var client: Client) : ViewModel(), CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    var repositories: MutableLiveData<ArrayList<Repository>>  = MutableLiveData()
    var error: MutableLiveData<Throwable> = MutableLiveData()

    fun loadRepositories(page: Int) {
        launch {
            try {
                val response = client.getRepositories(page)
                var arr = repositories.value
                if(arr == null) arr = ArrayList()
                if(response != null) arr.addAll(response.items)
                repositories.value = arr
            } catch (throwable: Throwable) {
                error.value = throwable
            }
        }
    }

    fun destroy() {
        job.cancel()
    }
}
