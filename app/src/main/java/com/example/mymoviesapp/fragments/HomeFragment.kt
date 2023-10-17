package com.example.mymoviesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.activities.MainActivity
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.home.HomeMoviesFatherAdapter
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.databinding.FragmentHomeBinding
import com.example.mymoviesapp.interfaces.MovieConstants.POPULAR_ID
import com.example.mymoviesapp.interfaces.MovieConstants.copyToClipboard
import com.example.mymoviesapp.interfaces.MovieConstants.downloadImage
import com.example.mymoviesapp.room.FavoriteMovie
import com.example.mymoviesapp.room.FavoriteMovieDAO
import com.example.mymoviesapp.room.FavoriteMovieRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeMoviesFatherAdapter

    private lateinit var roomDAO: FavoriteMovieDAO

    private var selectedState: Boolean = false

    private var isLoading: Boolean = false
    private val limitGenresDisplay = 5
    private lateinit var allMovieGenres: List<MovieGenre>
    private lateinit var allMovies: List<Movies>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        initListeners()
        initUI()
        initAPI()
    }

    private fun initUI() {
        val main = (activity as MainActivity)
        main.displayToolbar(R.id.homeFragment)
        main.showToolbarDivider(false)
        main.showToolbar(true)

        context?.let {
            roomDAO = FavoriteMovieRoom.favoriteMoviesRoom(it).favoriteMovieDAO()
        }
    }

    private fun initComponents() {
        adapter = HomeMoviesFatherAdapter() { action ->
            onMovieAction(action)
        }
        binding.rvHome.adapter = adapter
        binding.rvHome.layoutManager = LinearLayoutManager(context)

        (activity as MainActivity).showFAB(false)
    }

    private fun initListeners() {
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (allMovieGenres.isNotEmpty()) {

                    if (lastVisibleItemPosition == recyclerView.adapter?.itemCount?.minus(1)) {
                        loadMoreMovies()
                    }
                }

                (activity as MainActivity).showFAB(lastVisibleItemPosition > 2)

                val firstVisibleItemPosition =
                    layoutManager.findFirstCompletelyVisibleItemPosition()
                (activity as MainActivity).showToolbarDivider(firstVisibleItemPosition > 0)
            }
        })

    }

    fun initAPI() {
        lifecycleScope.launch {

            isLoading = true

            val main = (activity as MainActivity)
            main.showFAB(false)

            binding.homeConnectionErrorScreen.visibility = View.GONE
            binding.rvHome.visibility = View.INVISIBLE
            binding.loadingHome.visibility = View.VISIBLE

            try {
                val popularGenre =
                    MovieGenre(id = POPULAR_ID, name = getString(R.string.popular_genre))

                val popularMovies: List<MovieResult> =
                    APIClient.service.getHomePopularMovies().results.shuffled().take(12)

                allMovieGenres = APIClient.service.getMoviesGenres().genres.shuffled()

                val currentMoviesGenres: List<MovieGenre> = allMovieGenres.take(limitGenresDisplay)
                allMovieGenres = allMovieGenres.subList(
                    currentMoviesGenres.size, allMovieGenres.size
                )

                val moviesByGenre: MutableList<List<MovieResult>> = mutableListOf()
                currentMoviesGenres.forEach { genre ->
                    while (true) {
                        val moviesPage = Random.nextInt(5) + 1
                        val moviesResult: List<MovieResult> =
                            APIClient.service.getMoviesByGenre(
                                genreID = genre.id, page = moviesPage

                            ).results.filter { movie ->
                                !movie.adult

                            }.shuffled().take(10)

                        if (moviesResult.size == 10) {
                            moviesByGenre.add(moviesResult)
                            break
                        }
                    }
                }

                allMovies = listOf(
                    Movies(genre = popularGenre, movieList = popularMovies)

                ) + currentMoviesGenres.mapIndexed { index, genre ->
                    Movies(genre = genre, movieList = moviesByGenre[index])
                }

                adapter.updateHomeMovies(allMovies, allMovieGenres.isNotEmpty())

                delay(1000)
                binding.loadingHome.visibility = View.GONE
                binding.rvHome.visibility = View.VISIBLE

            } catch (e: Exception) {
                delay(2000)
                binding.loadingHome.visibility = View.GONE
                binding.homeConnectionErrorScreen.visibility = View.VISIBLE

            } finally {
                isLoading = false
                delay(500)
                main.refreshingSwipe(false)
            }
        }
    }

    private fun loadMoreMovies() {
        lifecycleScope.launch {
            if (!isLoading) {
                isLoading = true

                try {
                    val currentMoviesGenres: List<MovieGenre> =
                        allMovieGenres.take(limitGenresDisplay)

                    val newMoviesByGenre: MutableList<List<MovieResult>> = mutableListOf()
                    currentMoviesGenres.forEach { genre ->
                        while (true) {
                            val moviesPage = Random.nextInt(5) + 1
                            val moviesResult: List<MovieResult> =
                                APIClient.service.getMoviesByGenre(
                                    genreID = genre.id, page = moviesPage
                                ).results.filter { movie ->
                                    !movie.adult
                                }.shuffled().take(10)

                            if (moviesResult.size == 10) {
                                newMoviesByGenre.add(moviesResult)
                                break
                            }
                        }
                    }

                    allMovieGenres =
                        allMovieGenres.subList(currentMoviesGenres.size, allMovieGenres.size)

                    allMovies = allMovies.plus(currentMoviesGenres.mapIndexed { index, genre ->
                        Movies(genre = genre, movieList = newMoviesByGenre[index])
                    })

                    delay(1000)

                    adapter.updateHomeMovies(allMovies, allMovieGenres.isNotEmpty())

                } catch (e: Exception) {
                    delay(1000)
                    adapter.updateHomeMovies(allMovies, false)

                } finally {
                    isLoading = false
                }
            }
        }
    }

    private fun onMovieAction(action: MovieAction) {
        when (action) {
            is MovieAction.SelectMovie -> selectMovie(action.movie, action.viewToFocus)
            is MovieAction.ShowMoreGenres -> (activity as MainActivity).seeMoreGenres(action.genre)
            is MovieAction.ShowMovieDetails -> (activity as MainActivity).openMovieDetails(action.movie)
            else -> {}
        }
    }

    private fun selectMovie(movie: MovieResult, view: View) {
        if (!selectedState) {
            selectedState = true

            val popup = PopupMenu(context, view)
            popup.inflate(R.menu.selected_menu)
            var isMovieInFavorites: Boolean = false
            lifecycleScope.launch(Dispatchers.IO) {
                isMovieInFavorites = roomDAO.isMovieInFavorites(movie.id)
                withContext(Dispatchers.Main) {
                    popup.menu.findItem(R.id.menuFavorite).title =
                        getString(
                            if (isMovieInFavorites) {
                                R.string.remove_from_favorites
                            } else {
                                R.string.add_to_favorites
                            }
                        )
                }
            }

            popup.setOnDismissListener {
                selectedState = false
            }

            popup.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menuCopyName -> {
                        context?.let {
                            copyToClipboard(it, movie.title)
                        }
                        true
                    }

                    R.id.menuSaveImage -> {
                        context?.let {
                            downloadImage(it, movie.posterPath, movie.title)
                        }
                        true
                    }

                    R.id.menuFavorite -> addToFavorites(movie, isMovieInFavorites)

                    R.id.menuMoreInformation -> {
                        (activity as MainActivity).openMovieDetails(movie)
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun addToFavorites(movie: MovieResult, isMovieInFavorites: Boolean): Boolean {
        lifecycleScope.launch(Dispatchers.IO) {
            if (isMovieInFavorites) {
                roomDAO.removeMovieById(movie.id)

            } else {
                val lastPosition = roomDAO.getLastPosition() + 1
                roomDAO.addMovieToFavorites(
                    FavoriteMovie(movie.id, movie.title, movie.posterPath, lastPosition, true)
                )
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return true
    }

    fun scrollUp() {
        binding.rvHome.layoutManager?.smoothScrollToPosition(
            binding.rvHome, RecyclerView.State(), 0
        )
    }
}