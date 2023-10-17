package com.example.mymoviesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.mymoviesapp.activities.MainActivity
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.discover.DiscoverMoviesAdapter
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.databinding.FragmentDiscoverBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DiscoverMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initListeners()
        initUI()
        initAPI()
    }

    private fun initListeners() {
        binding.rvMovieDiscover.addOnScrollListener(object : OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                (activity as MainActivity).showToolbarDivider(firstVisibleItemPosition > 0)
            }
        })
    }

    private fun initUI() {
        val main = (activity as MainActivity)
        main.displayToolbar(R.id.discoverFragment)
        main.showToolbarDivider(false)
        main.showToolbar(true)
    }


    private fun initRecycler() {
        adapter = DiscoverMoviesAdapter{
            onMovieGenreClicked(it)
        }
        binding.rvMovieDiscover.adapter = adapter

        val layout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMovieDiscover.layoutManager = layout

        binding.rvMovieDiscover.addItemDecoration(
            DividerItemDecoration(
                context,
                layout.orientation
            )
        )
    }

    private fun onMovieGenreClicked(genre : MovieGenre){
        (activity as MainActivity).seeMoreGenres(genre)
    }

    fun initAPI() {
        lifecycleScope.launch {
            binding.rvMovieDiscover.visibility = View.INVISIBLE
            binding.loadingDiscoverMovies.visibility = View.VISIBLE
            binding.tvDiscoverConnectionError.visibility = View.GONE

            try {
                val genres: List<MovieGenre> = APIClient.service.getMoviesGenres().genres

                val moviesByGenre: MutableList<MovieResult> = mutableListOf()
                genres.forEach {
                    while (true){
                        val movie = APIClient.service.getMoviesByGenre(genreID = it.id)
                            .results.shuffled().first()

                        if(!moviesByGenre.contains(movie)){
                            moviesByGenre.add(movie)
                            break
                        }
                    }
                }

                val allMoviesGenres: List<Movies> = genres.mapIndexed { index, genre ->
                    Movies(genre = genre, movieList = listOf(moviesByGenre[index]))
                }

                adapter.updateDiscoverGenre(allMoviesGenres)

                delay(1000)
                binding.loadingDiscoverMovies.visibility = View.GONE
                binding.rvMovieDiscover.visibility = View.VISIBLE

            } catch (e: Exception) {
                delay(1000)
                binding.loadingDiscoverMovies.visibility = View.GONE
                binding.tvDiscoverConnectionError.visibility = View.VISIBLE

            } finally {
                delay(500)
                (activity as MainActivity).refreshingSwipe(false)
            }
        }
    }
}
