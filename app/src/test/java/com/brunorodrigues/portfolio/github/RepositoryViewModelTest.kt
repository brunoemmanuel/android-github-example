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
import com.brunorodrigues.portfolio.github.data.RepositoriesResponse
import com.brunorodrigues.portfolio.github.data.Repository
import com.brunorodrigues.portfolio.github.data.User
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import retrofit2.HttpException
import kotlin.collections.ArrayList

class RepositoryViewModelTest {
    @get:Rule
    val instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var client: Client
    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    @Mock
    lateinit var observerRepository: Observer<ArrayList<Repository>>
    @Mock
    lateinit var observerError: Observer<Throwable>
    @Mock
    lateinit var exception:HttpException

    private lateinit var lifeCycle: Lifecycle
    private lateinit var viewModel: RepositoryViewModel
    private val items = ArrayList<Repository>(listOf(Repository(owner = User()), Repository(owner = User()), Repository(owner = User())))

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockitoAnnotations.initMocks(this)
        viewModel = RepositoryViewModel(client)
        viewModel.repositories.observeForever(observerRepository)
        viewModel.error.observeForever(observerError)
        lifeCycle = LifecycleRegistry(lifecycleOwner)
    }

    @Test
    fun testObserverNull() = runBlockingTest {
        `when`( client.getRepositories(1) ).thenReturn(null)
        assertNotNull(viewModel.repositories)
        assertTrue(viewModel.repositories.hasObservers())
    }

    @Test
    fun testApiLoadSuccess() = runBlockingTest {
        val response = RepositoriesResponse(items = items)

        `when`( client.getRepositories(1) ).thenReturn(response)
        viewModel.loadRepositories(1)
        verify(observerRepository).onChanged(response.items)
    }

    @Test
    fun testApiLoadFailed() = runBlockingTest {
        `when`(client.getRepositories(1)).thenThrow(exception)
        viewModel.loadRepositories(1)
        verify(observerError).onChanged(exception)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
