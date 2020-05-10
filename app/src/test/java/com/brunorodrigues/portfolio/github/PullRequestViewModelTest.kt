package com.brunorodrigues.portfolio.github

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.data.PullRequest
import com.brunorodrigues.portfolio.github.data.User
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList

class PullRequestViewModelTest {
    @get:Rule
    val instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var client: Client
    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    @Mock
    lateinit var observerPullRequests: Observer<ArrayList<PullRequest>>
    @Mock
    lateinit var observerError: Observer<Throwable>
    @Mock
    lateinit var exception:HttpException

    private lateinit var lifeCycle: Lifecycle
    private lateinit var viewModel: PullRequestViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockitoAnnotations.initMocks(this)
        viewModel = PullRequestViewModel(client)
        viewModel.pullRequests.observeForever(observerPullRequests)
        viewModel.error.observeForever(observerError)
        lifeCycle = LifecycleRegistry(lifecycleOwner)
    }

    @Test
    fun testObserverNull() = runBlockingTest {
        `when`( client.getRepositories(1) ).thenReturn(null)
        assertNotNull(viewModel.pullRequests)
        assertTrue(viewModel.pullRequests.hasObservers())
    }

    @Test
    fun testApiLoadSuccess() = runBlockingTest {
        val items = ArrayList<PullRequest>()
        items.add(PullRequest(user = User(), createdAt = Date()))

        `when`( client.getPullRequests("", "", 1) ).thenReturn(items)
        viewModel.loadPullRequests("", "", 1)
        verify(observerPullRequests).onChanged(items)
    }

    @Test
    fun testApiLoadFailed() = runBlockingTest {
        `when`(client.getPullRequests("", "", 1)).thenThrow(exception)
        viewModel.loadPullRequests("", "", 1)
        verify(observerError).onChanged(exception)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}