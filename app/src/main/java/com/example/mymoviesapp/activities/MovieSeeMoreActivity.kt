package com.example.mymoviesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.seemore.SeeMoreAdapter
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.DetailsItem
import com.example.mymoviesapp.api.MoviesResponse
import com.example.mymoviesapp.databinding.ActivityMovieSeeMoreBinding
import com.example.mymoviesapp.interfaces.MovieConstants.DETAILS_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_ID_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_SUBTITLE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TITLE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TYPE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MOVIES_BY_GENRE
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MOVIE_CAST
import com.example.mymoviesapp.interfaces.MovieConstants.openDialog
import com.example.mymoviesapp.interfaces.MovieConstants.show
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MovieSeeMoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieSeeMoreBinding
    private lateinit var adapter: SeeMoreAdapter

    private var id: Int? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var seeType: Int? = null

    private var itemList: List<DetailsItem> = emptyList()
    private var totalPages: Int = 0
    private var currentPage: Int = 0
    private var isLoading: Boolean = false

    private var isDialogOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSeeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initComponents()
        initListeners()
        initAPI()

        // Manejar el ingles y español
        // DOCUMENTAR
    }

    private fun initUI() {
        id = intent?.extras?.getInt(SEE_MORE_ID_KEY)
        title = intent?.extras?.getString(SEE_MORE_TITLE_KEY)
        subtitle = intent?.extras?.getString(SEE_MORE_SUBTITLE_KEY)
        seeType = intent?.extras?.getInt(SEE_MORE_TYPE_KEY)

        setSupportActionBar(binding.seeMoreToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title.orEmpty()
        supportActionBar?.subtitle = subtitle.orEmpty()

        binding.swipeSeeMoreError.visibility = GONE
        binding.rvSeeMore.visibility = GONE
        binding.seeMoreAppBarLayout.visibility = GONE

        binding.pbSeeMoreLoading.visibility = VISIBLE
    }

    private fun initComponents() {
        binding.rvSeeMore.layoutManager = GridLayoutManager(this, 3)

        adapter = SeeMoreAdapter { onItemClicked(it) }
        binding.rvSeeMore.adapter = adapter
    }

    private fun initListeners() {
        binding.swipeSeeMoreError.setOnRefreshListener {
            initAPI()
        }

        binding.rvSeeMore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val manager = recyclerView.layoutManager as GridLayoutManager
                if (currentPage < totalPages && seeType != SEE_MOVIE_CAST) {
                    val lastItemPosition = manager.findLastVisibleItemPosition()

                    val isBottomReached =
                        lastItemPosition == recyclerView.adapter?.itemCount?.minus(1)
                    if (isBottomReached) loadMoreItems()
                }

                val firstItemPosition = manager.findFirstCompletelyVisibleItemPosition()
                binding.seeMoreToolbarDivider.show(
                    if (firstItemPosition > 0) VISIBLE else GONE, this@MovieSeeMoreActivity
                )

            }
        })
    }

    private fun initAPI() {
        lifecycleScope.launch {
            if (!isLoading) {
                isLoading = true

                seeType?.let { type ->
                    try {
                        if (type == SEE_MOVIE_CAST) {
                            val castList: List<DetailsItem> =
                                APIClient.service.getMovieCredits(id!!).cast.map {
                                    DetailsItem(true, it.id, it.photoPath, it.name, it.character)
                                }
                            adapter.updateList(castList, false)

                        } else {
                            findMoreMovies(type == SEE_MOVIES_BY_GENRE)
                        }

                        delay(1000)
                        binding.swipeSeeMoreError.show(GONE, this@MovieSeeMoreActivity)

                        binding.rvSeeMore.show(VISIBLE, this@MovieSeeMoreActivity)
                        binding.seeMoreAppBarLayout.show(VISIBLE, this@MovieSeeMoreActivity)


                    } catch (e: Exception) {
                        binding.rvSeeMore.show(GONE, this@MovieSeeMoreActivity)
                        binding.seeMoreAppBarLayout.show(GONE, this@MovieSeeMoreActivity)

                        binding.swipeSeeMoreError.show(VISIBLE, this@MovieSeeMoreActivity)

                    } finally {
                        binding.pbSeeMoreLoading.show(GONE, this@MovieSeeMoreActivity)
                        isLoading = false
                    }

                } ?: kotlin.run {
                    delay(1000)
                    errorEmptyType()
                    isLoading = false
                }
            }
        }
    }

    private fun loadMoreItems() {
        lifecycleScope.launch {
            if (!isLoading) {
                isLoading = true

                seeType?.let { type ->
                    try {
                        findMoreMovies(type == SEE_MOVIES_BY_GENRE)

                    } catch (e: Exception) {
                        delay(1000)
                        adapter.updateList(itemList, false)

                    } finally {
                        isLoading = false
                    }

                } ?: kotlin.run {
                    errorEmptyType()
                    isLoading = false
                }
            }
        }
    }

    private suspend fun findMoreMovies(findMoviesByGenre: Boolean) {
        currentPage++

        val response: MoviesResponse = if (findMoviesByGenre) {
            APIClient.service.getMoviesByGenre(id!!, currentPage)
        } else {
            APIClient.service.getRecommendedMovies(id!!, currentPage)
        }

        totalPages = response.totalPages

        itemList = itemList + response.results.map {
            DetailsItem(false, it.id, it.posterPath, it.title)
        }

        adapter.updateList(itemList, currentPage < totalPages)
    }

    private fun errorEmptyType() {
        val titleMsj: String = getString(
            if (seeType == SEE_MOVIES_BY_GENRE) R.string.movie_genre_not_found_title
            else R.string.movie_not_found_title
        )
        val subtitleMsj: String = getString(
            if (seeType == SEE_MOVIES_BY_GENRE) R.string.movie_genre_not_found_subtitle
            else R.string.movie_not_found_subtitle
        )

        binding.swipeSeeMoreLayout.tvSwipeErrorTitle.text = titleMsj
        binding.swipeSeeMoreLayout.tvSwipeErrorSubtitle.text = subtitleMsj

        binding.pbSeeMoreLoading.show(GONE, this@MovieSeeMoreActivity)
        binding.rvSeeMore.show(GONE, this@MovieSeeMoreActivity)
        binding.seeMoreAppBarLayout.show(GONE, this@MovieSeeMoreActivity)

        binding.swipeSeeMoreError.isRefreshing = false
        binding.swipeSeeMoreError.show(VISIBLE, this@MovieSeeMoreActivity)
    }

    private fun onItemClicked(item: DetailsItem) {
        if (item.isAnActor) {
            if (!isDialogOpen) {
                isDialogOpen = true

                openDialog(this, item.imagePath.orEmpty(), item.title, item.subtitle)
                    .setOnDismissListener { isDialogOpen = false }
            }

        } else {
            val intent = Intent(this, MoviesDetailsActivity::class.java)
            intent.putExtra(DETAILS_KEY, item.id)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true

        } else {
            super.onOptionsItemSelected(item)
        }
    }

}