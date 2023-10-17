package com.example.mymoviesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.activities.MainActivity
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.search.SearchMoviesAdapter
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.FragmentSearchBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var title: String = ""
    private var totalPages: Int = 1
    private var currentPage: Int = 1
    private var isLoading: Boolean = false
    private lateinit var searchedMovies: List<MovieResult>

    private lateinit var adapter: SearchMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        initListeners()
        initUI()
    }

    private fun initUI() {
        val main = (activity as MainActivity)
        main.displayToolbar(R.id.searchFragment)
        main.showToolbarDivider(false)
        main.showToolbar(true)
    }

    private fun initComponents() {
        adapter = SearchMoviesAdapter { onSearchMovieClicked(it) }
        binding.rvSearchMovie.adapter = adapter
        binding.rvSearchMovie.layoutManager = GridLayoutManager(context, 3)

        (activity as MainActivity).showFAB(false)
    }

    private fun initListeners() {
        binding.rvSearchMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                if (currentPage < totalPages) {
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItemPosition == recyclerView.adapter?.itemCount?.minus(1)) {
                        loadMoreMovies()
                    }

                    (activity as MainActivity).showFAB(lastVisibleItemPosition > 12)
                }

                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                (activity as MainActivity).showToolbarDivider(firstVisibleItemPosition > 0)
            }
        })
    }

    private fun onSearchMovieClicked(movie: MovieResult) {
        (activity as MainActivity).openMovieDetails(movie)
    }

    fun searchMovie(searchTitle: String) {
        if (searchTitle.isEmpty()) {
            Toast.makeText(context, getString(R.string.error_search_empty), Toast.LENGTH_SHORT).show()

        } else {
            lifecycleScope.launch {
                isLoading = true
                title = searchTitle

                val main = (activity as MainActivity)
                main.showFAB(false)

                binding.tvSearchSubtitle.visibility = View.GONE
                binding.rvSearchMovie.visibility = View.INVISIBLE
                binding.loadingSearchMovies.visibility = View.VISIBLE

                try {
                    val newSearchedList = APIClient.service.getSearchMovies(title = title)

                    if (newSearchedList.results.isNotEmpty()) {
                        totalPages = newSearchedList.totalPages
                        currentPage = newSearchedList.page
                        searchedMovies = newSearchedList.results

                        adapter.updateSearchedMovies(
                            searchedMovies,
                            currentPage < totalPages
                        )

                        delay(1000)
                        binding.loadingSearchMovies.visibility = View.GONE
                        binding.rvSearchMovie.visibility = View.VISIBLE


                    } else {
                        delay(2000)
                        binding.loadingSearchMovies.visibility = View.GONE

                        binding.tvSearchSubtitle.text =
                            getString(R.string.error_search, searchTitle)
                        binding.tvSearchSubtitle.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    delay(2000)
                    binding.loadingSearchMovies.visibility = View.GONE

                    binding.tvSearchSubtitle.text = getString(R.string.error_connection_subtitle)
                    binding.tvSearchSubtitle.visibility = View.VISIBLE

                } finally {
                    isLoading = false
                    delay(500)
                    main.refreshingSwipe(false)
                }
            }
        }
    }

    private fun loadMoreMovies() {
        lifecycleScope.launch {
            if (!isLoading) {
                isLoading = true

                delay(1000)
                currentPage++

                try {
                    val newLoadedMovies =
                        APIClient.service.getSearchMovies(title = title, page = currentPage)

                    searchedMovies = searchedMovies.plus(newLoadedMovies.results)
                    adapter.updateSearchedMovies(searchedMovies, currentPage < totalPages)

                } catch (e: Exception) {
                    delay(1000)
                    adapter.updateSearchedMovies(searchedMovies, false)

                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun scrollUp() {
        binding.rvSearchMovie.layoutManager?.smoothScrollToPosition(
            binding.rvSearchMovie, RecyclerView.State(), 0
        )
    }

    fun refreshSearch() {
        if (title.isEmpty()) {
            (activity as MainActivity).refreshingSwipe(false)

        } else {
            searchMovie(title)
        }
    }
}